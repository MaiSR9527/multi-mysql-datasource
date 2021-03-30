package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.UserInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MultiDatasource1Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MultiDatasource1Application.class, args);
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setName("springboot");
        int write = userInfoService.write(userInfo);
        System.out.println(write);

        UserInfo read = userInfoService.read(1);
        System.out.println(read.toString());
    }

}
