package com.kingsoft.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "report_risk_es", type = "wps_android_report_by_section", shards = 1, replicas = 0)
public class Wps_android_report_by_section {
    @Id
    private String uuid;
    private String report_front_15day_days;
    private String report_front_15day_times;
    private String report_front_7day_days;
    private String report_front_7day_times;
    private String report_front_3day_days;
    private String report_front_3day_times;
    private String dt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReport_front_15day_days() {
        return report_front_15day_days;
    }

    public void setReport_front_15day_days(String report_front_15day_days) {
        this.report_front_15day_days = report_front_15day_days;
    }

    public String getReport_front_15day_times() {
        return report_front_15day_times;
    }

    public void setReport_front_15day_times(String report_front_15day_times) {
        this.report_front_15day_times = report_front_15day_times;
    }

    public String getReport_front_7day_days() {
        return report_front_7day_days;
    }

    public void setReport_front_7day_days(String report_front_7day_days) {
        this.report_front_7day_days = report_front_7day_days;
    }

    public String getReport_front_7day_times() {
        return report_front_7day_times;
    }

    public void setReport_front_7day_times(String report_front_7day_times) {
        this.report_front_7day_times = report_front_7day_times;
    }

    public String getReport_front_3day_days() {
        return report_front_3day_days;
    }

    public void setReport_front_3day_days(String report_front_3day_days) {
        this.report_front_3day_days = report_front_3day_days;
    }

    public String getReport_front_3day_times() {
        return report_front_3day_times;
    }

    public void setReport_front_3day_times(String report_front_3day_times) {
        this.report_front_3day_times = report_front_3day_times;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
