package com.kingsoft.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 属于event_collection库下
 * 记录用户经纬度信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "factor_user_location_lng_lat", type = "user_location_lng_lat", shards = 1, replicas = 0)
public class User_location_lng_lat {
    private String id;
    private String user_id;
    private String longitude;
    private String latitude;
    private String create_time;
    private Double[] location;

    private String in_01Km;
    private String in_05Km;
    private String in_10Km;
    private String in_20Km;
    private String in_30Km;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    public String getIn_01Km() {
        return in_01Km;
    }

    public void setIn_01Km(String in_01Km) {
        this.in_01Km = in_01Km;
    }

    public String getIn_05Km() {
        return in_05Km;
    }

    public void setIn_05Km(String in_05Km) {
        this.in_05Km = in_05Km;
    }

    public String getIn_10Km() {
        return in_10Km;
    }

    public void setIn_10Km(String in_10Km) {
        this.in_10Km = in_10Km;
    }

    public String getIn_20Km() {
        return in_20Km;
    }

    public void setIn_20Km(String in_20Km) {
        this.in_20Km = in_20Km;
    }

    public String getIn_30Km() {
        return in_30Km;
    }

    public void setIn_30Km(String in_30Km) {
        this.in_30Km = in_30Km;
    }

}
