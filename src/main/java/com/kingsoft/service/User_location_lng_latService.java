package com.kingsoft.service;

import com.kingsoft.dao.User_location_lng_latRepository;
import com.kingsoft.entity.User_location_lng_lat;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class User_location_lng_latService extends BaseService<User_location_lng_lat> {

    @Autowired
    User_location_lng_latRepository userReturnRepository; // 工厂初始化，懒加载

    public String _searchById(String searchContent) {
        if (checkParmNull(searchContent)) {
            //根据具体的字段填写
            QueryBuilder queryBuilder = QueryBuilders.matchQuery("user_id", searchContent);//字段查询
            SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
            logger.info("查询的语句：" + searchQuery.getQuery().toString());
            Page<User_location_lng_lat> searchPageResults = userReturnRepository.search(searchQuery);
            code = "200";
            message = "success";
            item = new ArrayList<>(searchPageResults.getContent());
            count = String.valueOf(userReturnRepository.search(new NativeSearchQueryBuilder().withQuery(queryBuilder).build()).getTotalElements());
            return returnData();
        } else {
            return null;
        }
    }


}
