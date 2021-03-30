package org.msr.masterslave;

import org.msr.masterslave.model.UserInfo;
import org.msr.masterslave.service.UserInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:17
 **/
@SpringBootApplication
public class SpringbootDynamicDatasourceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootDynamicDatasourceApplication.class, args);
        UserInfoService userInfoService = applicationContext.getBean(UserInfoService.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setName("springboot");
        int write = userInfoService.write(userInfo);
        System.out.println(write);

        UserInfo read = userInfoService.read(1);
        System.out.println(read.toString());
    }

}
