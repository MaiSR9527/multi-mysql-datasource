package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.UserInfoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * author: MaiShuRen
 * site: http://www.maishuren.top
 * since: 2021-03-28 23:32
 **/
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);
        UserInfo read = userInfoService.read(3);
        System.out.println(read.toString());
        UserInfo userInfo = new UserInfo();
        userInfo.setName("hahaha");
        int write = userInfoService.write(userInfo);
        System.out.println(write);
    }
}
