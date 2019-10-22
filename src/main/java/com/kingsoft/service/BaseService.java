package com.kingsoft.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kingsoft.entity.Data;
import com.kingsoft.entity.Return;
import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class BaseService<T> {

    public String code;//返回数据标识
    public String message;//返回数据标识原因
    public List<T> item;//返回的数据
    public String count;//查询出的数据条数
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    Return retrunData = new Return();
    Data data = new Data();

    public abstract String _searchById(String searchContent);

    public String returnData() {
        return returnData(false);
    }

    protected boolean checkParmNull(String searchContent) {
        boolean flag = true;
        if (searchContent == null || Strings.isEmpty(searchContent.toString())) {
            code = "400";
            message = "Parameter is null！";
            item = null;
            count = "0";
            flag = false;
        }
        return flag;
    }

    public String returnData(Boolean falg) {
        data.setItem(item);
        data.setCount(count);
        retrunData.setCode(code);
        retrunData.setMessage(message);
        retrunData.setData(data);
        if (falg) {
            return JSON.toJSONString(retrunData, SerializerFeature.WriteMapNullValue);
        } else {
            return JSON.toJSONString(retrunData);
        }
    }
}
