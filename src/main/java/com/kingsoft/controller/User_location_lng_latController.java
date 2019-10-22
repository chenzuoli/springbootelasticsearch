package com.kingsoft.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kingsoft.entity.Data;
import com.kingsoft.entity.Return;
import com.kingsoft.entity.User_location_lng_lat;
import com.kingsoft.service.User_location_lng_latService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * company: wetech
 * user: chenzuoli
 * date: 2018/8/23 20:30
 * description: 用户经纬度查询
 */
@RestController
@RequestMapping("/search")
public class User_location_lng_latController {
    @Autowired
    User_location_lng_latService user_location_lng_latService;

    @RequestMapping(value = "/user_location_lng_lat", method = RequestMethod.POST)
    public String findBySearchContent(@RequestParam(value = "userId") String searchContent) {
//        return user_location_lng_latService._searchById(searchContent);

        // return the stable data.
        String code = "200";
        String message = "success";
        User_location_lng_lat user_location_lng_lat = new User_location_lng_lat();
        user_location_lng_lat.setId("34418");
        user_location_lng_lat.setUser_id("1019663596");
        user_location_lng_lat.setLongitude("106.523438");
        user_location_lng_lat.setLatitude("29.589409");
        user_location_lng_lat.setCreate_time("1536231069");
        user_location_lng_lat.setLocation(new Double[]{106.523438, 29.589409});
        user_location_lng_lat.setIn_01Km("0");
        user_location_lng_lat.setIn_05Km("1");
        user_location_lng_lat.setIn_10Km("3");
        user_location_lng_lat.setIn_20Km("4");
        user_location_lng_lat.setIn_30Km("4");
        ArrayList<User_location_lng_lat> users = new ArrayList<>();
        users.add(user_location_lng_lat);
        String count = "1";
        Data<User_location_lng_lat> datas = new Data<>();
        datas.setItem(users);
        datas.setCount(count);
        Return<User_location_lng_lat> returns = new Return<>();
        returns.setCode(code);
        returns.setMessage(message);
        returns.setData(datas);
        return JSON.toJSONString(returns, SerializerFeature.WriteMapNullValue);
    }
}
