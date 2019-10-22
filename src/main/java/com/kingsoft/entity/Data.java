package com.kingsoft.entity;

import java.util.List;

public class Data<T> {
    private List<T> item;
    private String count;

    public List<T> getItem() {
        return item;
    }

    public void setItem(List<T> item) {
        this.item = item;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
