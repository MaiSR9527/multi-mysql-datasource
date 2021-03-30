package org.msr.masterslave.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-29 23:01
 **/
@Getter
@Setter
@Configuration
@MapperScan(basePackages = {"org.msr.masterslave.mapper"}, sqlSessionFactoryRef = "sqlSessionFactoryBean")
public class MyDataSourceConfig {

    @Value("master.datasource.username")
    private String masterUsername;
    @Value("master.datasource.password")
    private String masterPassword;
    @Value("master.datasource.driver")
    private String masterDriver;
    @Value("master.datasource.url")
    private String masterUrl;

    @Value("slave.datasource.username")
    private String slaveUsername;
    @Value("slave.datasource.password")
    private String slavePassword;
    @Value("slave.datasource.driver")
    private String slaveDriver;
    @Value("slave.datasource.url")
    private String slaveUrl;

    @Bean
    public DruidDataSource masterDruidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(masterDriver);
        druidDataSource.setUrl(masterUrl);
        druidDataSource.setUsername(masterUsername);
        druidDataSource.setPassword(masterPassword);
        return druidDataSource;
    }

    @Bean
    public DruidDataSource salveDruidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(slaveDriver);
        druidDataSource.setUrl(slaveUrl);
        druidDataSource.setUsername(slaveUsername);
        druidDataSource.setPassword(slavePassword);
        return druidDataSource;
    }

    @Bean
    public MyDataSource myDataSource(DruidDataSource masterDruidDataSource, DruidDataSource salveDruidDataSource) {
        MyDataSource myDataSource = new MyDataSource();
        myDataSource.setDefaultTargetDataSource(masterDruidDataSource);
        Map dataSourceMap = new HashMap(16);
        dataSourceMap.put("master", masterDruidDataSource);
        dataSourceMap.put("slave", salveDruidDataSource);
        myDataSource.setTargetDataSources(dataSourceMap);
        return myDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(MyDataSource myDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(myDataSource);
        return sqlSessionFactoryBean;
    }
}
