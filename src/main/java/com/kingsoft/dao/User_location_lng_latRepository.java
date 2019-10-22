package com.kingsoft.dao;

import com.kingsoft.entity.User_location_lng_lat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface User_location_lng_latRepository extends ElasticsearchRepository<User_location_lng_lat, String> {

}
