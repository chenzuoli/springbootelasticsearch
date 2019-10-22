package com.kingsoft.util;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;

public class FieldsUtil<T> {

    public void fieldsToBean(T t, String field, String value) {
        Object o = new Object();
        try {
            t.getClass().getMethod("set" + StringUtils.capitalize(field), String.class).invoke(t, value == null ? "" : value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
