package com.kingsoft.service;

import com.kingsoft.context.BusinessContext;
import com.kingsoft.dao.Wps_android_report_by_sectionRepository;
import com.kingsoft.entity.Wps_android_report_by_section;
import com.kingsoft.util.TimeUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class Wps_android_report_by_sectionService extends BaseService<Wps_android_report_by_section> {

    TimeUtil t = new TimeUtil();

    @Autowired
    Wps_android_report_by_sectionRepository androidRepository; // 工厂初始化，懒加载

    @Override
    public String _searchById(String searchContent) {
        if (checkParmNull(searchContent)) {
            QueryBuilder queryBuilder = QueryBuilders.matchQuery("uuid", searchContent);
            SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
            logger.info("查询的语句：" + searchQuery.getQuery().toString());
            Page<Wps_android_report_by_section> searchPageResults = androidRepository.search(searchQuery);
            checkTime(searchPageResults);
            code = "200";
            message = "success";
            item = new ArrayList<>(searchPageResults.getContent());
            count = String.valueOf(searchPageResults.getContent().size());
            return returnData();
        } else {
            return null;
        }
    }

    public String returnData() {
        return returnData(true);
    }

    private void checkTime(Page<Wps_android_report_by_section> searchPageResults) {
        Iterator<Wps_android_report_by_section> it = searchPageResults.iterator();
        while (it.hasNext()) {
            if (t.getDaysAgo(BusinessContext.dayAgo) > t.parsedayWhitsecond(it.next().getDt())) {
                it.remove();
            }
        }
    }
}
