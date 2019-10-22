package com.kingsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElasticsearchSpringDataApplication {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchSpringDataApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchSpringDataApplication.class, args);
    }

//    @Override
//    public void run(String... strings) throws Exception {
//        List<Mf_cus_borrowdetailsterm> mfCusBorrowdetailsterms = mf_cus_borrowdetailstermService.findAll();
//        List<Mf_cus_borrowdetailsterm> mf_cus_borrowdetailsterms = mf_cus_borrowdetailstermService.searchMf_cus_borrowdetailsterm(1, 10, "180509004519150329_8", "180509004519150329_8");
//        //System.out.println(mfCusBorrowdetailsterms.size());
//        for(int i = 0;i < mf_cus_borrowdetailsterms.size();i++){
//            System.out.println("*************"+mf_cus_borrowdetailsterms.get(i)+"************");
//        }
//    }
}
