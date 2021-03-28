package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.OrderService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * author: MaiShuRen
 * site: http://www.maishuren.top
 * since: 2021-03-28 23:32
 **/
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        OrderService orderService = applicationContext.getBean(OrderService.class);
        UserInfo read = orderService.read(3);
        System.out.println(read.toString());
        UserInfo userInfo = new UserInfo();
        userInfo.setName("hahaha");
        int write = orderService.write(userInfo);
        System.out.println(write);
    }
}
