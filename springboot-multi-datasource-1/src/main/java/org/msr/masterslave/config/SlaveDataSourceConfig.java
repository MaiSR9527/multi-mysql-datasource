package org.msr.masterslave.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author MaiShuRen
 * @site http://www.maishuren.top
 * @since 2021-03-28 23:32
 */
@Getter
@Setter
@Configuration
@MapperScan(basePackages = {"org.msr.masterslave.slave.mapper"}, sqlSessionFactoryRef = "slaveSqlSessionFactoryBean")
public class SlaveDataSourceConfig {

    @Value("slave.datasource.username")
    private String slaveUsername;
    @Value("slave.datasource.password")
    private String slavePassword;
    @Value("slave.datasource.driver")
    private String slaveDriver;
    @Value("slave.datasource.url")
    private String slaveUrl;

    /**
     * 配置slave数据源
     *
     * @return
     */
    @Bean
    public DruidDataSource slaveDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(slaveDriver);
        dataSource.setUrl(slaveUrl);
        dataSource.setUsername(slaveUsername);
        dataSource.setPassword(slavePassword);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean slaveSqlSessionFactoryBean(DruidDataSource slaveDruidDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(slaveDruidDataSource);
        return sqlSessionFactoryBean;
    }
}
