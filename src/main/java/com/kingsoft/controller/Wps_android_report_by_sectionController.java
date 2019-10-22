package com.kingsoft.controller;

import com.kingsoft.service.Wps_android_report_by_sectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * company: wetech
 * user: chenzuoli
 * date: 2018/8/23 20:36
 * description: wps报活查询
 */
@RestController
@RequestMapping("/search")
public class Wps_android_report_by_sectionController {
    @Autowired
    Wps_android_report_by_sectionService wps_android_report_by_sectionService;

    @RequestMapping(value = "/wps_android_report_by_section", method = RequestMethod.POST)
    public String findBySearchContent(@RequestParam(value = "id") String searchContent) {
        return wps_android_report_by_sectionService._searchById(searchContent);
    }
}
