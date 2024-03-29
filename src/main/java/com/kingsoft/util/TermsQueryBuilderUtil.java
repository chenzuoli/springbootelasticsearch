package com.kingsoft.util;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.logging.DeprecationLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.lucene.BytesRefs;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.mapper.MappedFieldType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.indices.TermsLookup;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TermsQueryBuilderUtil extends AbstractQueryBuilder<TermsQueryBuilderUtil> {
    public static final String NAME = "terms";
    public static final ParseField QUERY_NAME_FIELD = new ParseField(NAME, "in");
    private static final DeprecationLogger DEPRECATION_LOGGER = new DeprecationLogger(Loggers.getLogger(org.elasticsearch.index.query.TermsQueryBuilder.class));
    private static final List<Class<? extends Number>> INTEGER_TYPES = new ArrayList<>(
            Arrays.asList(Byte.class, Short.class, Integer.class, Long.class));
    private static final List<Class<?>> STRING_TYPES = new ArrayList<>(
            Arrays.asList(BytesRef.class, String.class));
    private final String fieldName;
    private final List<?> values;
    private final TermsLookup termsLookup;

    public TermsQueryBuilderUtil(String fieldName, TermsLookup termsLookup) {
        this(fieldName, null, termsLookup);
    }

    /**
     * constructor used internally for serialization of both value / termslookup variants
     */
    TermsQueryBuilderUtil(String fieldName, List<Object> values, TermsLookup termsLookup) {
        if (Strings.isEmpty(fieldName)) {
            throw new IllegalArgumentException("field name cannot be null.");
        }
        if (values == null && termsLookup == null) {
            throw new IllegalArgumentException("No value or termsLookup specified for terms query");
        }
        if (values != null && termsLookup != null) {
            throw new IllegalArgumentException("Both values and termsLookup specified for terms query");
        }
        this.fieldName = fieldName;
        this.values = values == null ? null : convert(values);
        this.termsLookup = termsLookup;
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, String... values) {
        this(fieldName, values != null ? Arrays.asList(values) : null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, int... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, long... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, float... values) {
        this(fieldName, values != null ? IntStream.range(0, values.length)
                .mapToObj(i -> values[i]).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, double... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, Object... values) {
        this(fieldName, values != null ? Arrays.asList(values) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values    The terms
     */
    public TermsQueryBuilderUtil(String fieldName, Iterable<?> values) {
        if (Strings.isEmpty(fieldName)) {
            throw new IllegalArgumentException("field name cannot be null.");
        }
        if (values == null) {
            throw new IllegalArgumentException("No value specified for terms query");
        }
        this.fieldName = fieldName;
        this.values = convert(values);
        this.termsLookup = null;
    }

    /**
     * Read from a stream.
     */
    public TermsQueryBuilderUtil(StreamInput in) throws IOException {
        super(in);
        fieldName = in.readString();
        termsLookup = in.readOptionalWriteable(TermsLookup::new);
        values = (List<?>) in.readGenericValue();
    }

    /**
     * Same as {@link #convert(List)} but on an {@link Iterable}.
     */
    private static List<?> convert(Iterable<?> values) {
        List<?> list;
        if (values instanceof List<?>) {
            list = (List<?>) values;
        } else {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            for (Object o : values) {
                arrayList.add(o);
            }
            list = arrayList;
        }
        return convert(list);
    }

    /**
     * Convert the list in a way that optimizes storage in the case that all
     * elements are either integers or {@link String}s/{@link BytesRef}s. This
     * is useful to help garbage collections for use-cases that involve sending
     * very large terms queries to Elasticsearch. If the list does not only
     * contain integers or {@link String}s, then a list is returned where all
     * {@link String}s have been replaced with {@link BytesRef}s.
     */
    static List<?> convert(List<?> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        final boolean allNumbers = list.stream().allMatch(o -> o != null && INTEGER_TYPES.contains(o.getClass()));
        if (allNumbers) {
            final long[] elements = list.stream().mapToLong(o -> ((Number) o).longValue()).toArray();
            return new AbstractList<Object>() {
                @Override
                public Object get(int index) {
                    return elements[index];
                }

                @Override
                public int size() {
                    return elements.length;
                }
            };
        }

        final boolean allStrings = list.stream().allMatch(o -> o != null && STRING_TYPES.contains(o.getClass()));
        if (allStrings) {
            final BytesRefBuilder builder = new BytesRefBuilder();
            try (BytesStreamOutput bytesOut = new BytesStreamOutput()) {
                final int[] endOffsets = new int[list.size()];
                int i = 0;
                for (Object o : list) {
                    BytesRef b;
                    if (o instanceof BytesRef) {
                        b = (BytesRef) o;
                    } else {
                        builder.copyChars(o.toString());
                        b = builder.get();
                    }
                    bytesOut.writeBytes(b.bytes, b.offset, b.length);
                    if (i == 0) {
                        endOffsets[0] = b.length;
                    } else {
                        endOffsets[i] = Math.addExact(endOffsets[i - 1], b.length);
                    }
                    ++i;
                }
                final BytesReference bytes = bytesOut.bytes();
                return new AbstractList<Object>() {
                    public Object get(int i) {
                        final int startOffset = i == 0 ? 0 : endOffsets[i - 1];
                        final int endOffset = endOffsets[i];
                        return bytes.slice(startOffset, endOffset - startOffset).toBytesRef();
                    }

                    public int size() {
                        return endOffsets.length;
                    }
                };
            }
        }

        return list.stream().map(o -> o instanceof String ? new BytesRef(o.toString()) : o).collect(Collectors.toList());
    }

    /**
     * Convert the internal {@link List} of values back to a user-friendly list.
     * Integers are kept as-is since the terms query does not make any difference
     * between {@link Integer}s and {@link Long}s, but {@link BytesRef}s are
     * converted back to {@link String}s.
     */
    static List<Object> convertBack(List<?> list) {
        return new AbstractList<Object>() {
            @Override
            public int size() {
                return list.size();
            }

            @Override
            public Object get(int index) {
                Object o = list.get(index);
                if (o instanceof BytesRef) {
                    o = ((BytesRef) o).utf8ToString();
                }
                // we do not convert longs, all integer types are equivalent
                // as far as this query is concerned
                return o;
            }
        };
    }

    public static Optional<org.elasticsearch.index.query.TermsQueryBuilder> fromXContent(QueryParseContext parseContext) throws IOException {
        XContentParser parser = parseContext.parser();

        String fieldName = null;
        List<Object> values = null;
        TermsLookup termsLookup = null;

        String queryName = null;
        float boost = AbstractQueryBuilder.DEFAULT_BOOST;

        XContentParser.Token token;
        String currentFieldName = null;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parseContext.isDeprecatedSetting(currentFieldName)) {
                // skip
            } else if (token == XContentParser.Token.START_ARRAY) {
                if (fieldName != null) {
                    throw new ParsingException(parser.getTokenLocation(),
                            "[" + org.elasticsearch.index.query.TermsQueryBuilder.NAME + "] query does not support multiple fields");
                }
                fieldName = currentFieldName;
                values = parseValues(parser);
            } else if (token == XContentParser.Token.START_OBJECT) {
                if (fieldName != null) {
                    throw new ParsingException(parser.getTokenLocation(),
                            "[" + org.elasticsearch.index.query.TermsQueryBuilder.NAME + "] query does not support more than one field. "
                                    + "Already got: [" + fieldName + "] but also found [" + currentFieldName + "]");
                }
                fieldName = currentFieldName;
                termsLookup = TermsLookup.parseTermsLookup(parser);
            } else if (token.isValue()) {
                if (AbstractQueryBuilder.BOOST_FIELD.match(currentFieldName)) {
                    boost = parser.floatValue();
                } else if (AbstractQueryBuilder.NAME_FIELD.match(currentFieldName)) {
                    queryName = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(),
                            "[" + org.elasticsearch.index.query.TermsQueryBuilder.NAME + "] query does not support [" + currentFieldName + "]");
                }
            } else {
                throw new ParsingException(parser.getTokenLocation(),
                        "[" + org.elasticsearch.index.query.TermsQueryBuilder.NAME + "] unknown token [" + token + "] after [" + currentFieldName + "]");
            }
        }

        if (fieldName == null) {
            throw new ParsingException(parser.getTokenLocation(), "[" + org.elasticsearch.index.query.TermsQueryBuilder.NAME + "] query requires a field name, " +
                    "followed by array of terms or a document lookup specification");
        }
        return Optional.of(new org.elasticsearch.index.query.TermsQueryBuilder(fieldName, values, termsLookup)
                .boost(boost)
                .queryName(queryName));
    }

    private static List<Object> parseValues(XContentParser parser) throws IOException {
        List<Object> values = new ArrayList<>();
        while (parser.nextToken() != XContentParser.Token.END_ARRAY) {
            Object value = parser.objectBytes();
            if (value == null) {
                throw new ParsingException(parser.getTokenLocation(), "No value specified for terms query");
            }
            values.add(value);
        }
        return values;
    }

    private static Query handleTermsQuery(List<?> terms, String fieldName, QueryShardContext context) {
        MappedFieldType fieldType = context.fieldMapper(fieldName);
        String indexFieldName;
        if (fieldType != null) {
            indexFieldName = fieldType.name();
        } else {
            indexFieldName = fieldName;
        }

        Query query;
        if (context.isFilter()) {
            if (fieldType != null) {
                query = fieldType.termsQuery(terms, context);
            } else {
                BytesRef[] filterValues = new BytesRef[terms.size()];
                for (int i = 0; i < filterValues.length; i++) {
                    filterValues[i] = BytesRefs.toBytesRef(terms.get(i));
                }
                query = new TermInSetQuery(indexFieldName, filterValues);
            }
        } else {
            BooleanQuery.Builder bq = new BooleanQuery.Builder();
            for (Object term : terms) {
                if (fieldType != null) {
                    bq.add(fieldType.termQuery(term, context), BooleanClause.Occur.SHOULD);
                } else {
                    bq.add(new TermQuery(new Term(indexFieldName, BytesRefs.toBytesRef(term))), BooleanClause.Occur.SHOULD);
                }
            }
            query = bq.build();
        }
        return query;
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeString(fieldName);
        out.writeOptionalWriteable(termsLookup);
        out.writeGenericValue(values);
    }

    public String fieldName() {
        return this.fieldName;
    }

    public List<Object> values() {
        return convertBack(this.values);
    }

    public TermsLookup termsLookup() {
        return this.termsLookup;
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        if (this.termsLookup != null) {
            builder.startObject(fieldName);
            termsLookup.toXContent(builder, params);
            builder.endObject();
        } else {
            builder.field(fieldName, convertBack(values));
        }
        printBoostAndQueryName(builder);
        builder.endObject();
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    protected Query doToQuery(QueryShardContext context) throws IOException {
        if (termsLookup != null) {
            throw new UnsupportedOperationException("query must be rewritten first");
        }
        if (values == null || values.isEmpty()) {
            return Queries.newMatchNoDocsQuery("No terms supplied for \"" + getName() + "\" query.");
        }
        return handleTermsQuery(values, fieldName, context);
    }

    private List<Object> fetch(TermsLookup termsLookup, Client client) {
        List<Object> terms = new ArrayList<>();
        GetRequest getRequest = new GetRequest(termsLookup.index(), termsLookup.type(), termsLookup.id())
                .preference("_local").routing(termsLookup.routing());
        final GetResponse getResponse = client.get(getRequest).actionGet();
        if (getResponse.isSourceEmpty() == false) { // extract terms only if the doc source exists
            List<Object> extractedValues = XContentMapValues.extractRawValues(termsLookup.path(), getResponse.getSourceAsMap());
            terms.addAll(extractedValues);
        }
        return terms;
    }

    @Override
    protected int doHashCode() {
        return Objects.hash(fieldName, values, termsLookup);
    }

    @Override
    protected boolean doEquals(TermsQueryBuilderUtil other) {
        return Objects.equals(fieldName, other.fieldName) &&
                Objects.equals(values, other.values) &&
                Objects.equals(termsLookup, other.termsLookup);
    }

    @Override
    protected QueryBuilder doRewrite(QueryRewriteContext queryRewriteContext) throws IOException {
        if (this.termsLookup != null) {
            TermsLookup termsLookup = new TermsLookup(this.termsLookup);
            if (termsLookup.index() == null) { // TODO this should go away?
                DEPRECATION_LOGGER.deprecated("Omitting the index in terms lookup is deprecated");
                if (queryRewriteContext.getIndexSettings() != null) {
                    termsLookup.index(queryRewriteContext.getIndexSettings().getIndex().getName());
                } else {
                    return this; // can't rewrite until we have index scope on the shard
                }
            }
            List<Object> values = fetch(termsLookup, queryRewriteContext.getClient());
            return new org.elasticsearch.index.query.TermsQueryBuilder(this.fieldName, values);
        }
        return this;
    }

}
