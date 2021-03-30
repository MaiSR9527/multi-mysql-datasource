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
@MapperScan(basePackages = {"org.msr.masterslave.master.mapper"}, sqlSessionFactoryRef = "masterSqlSessionFactoryBean")
public class MasterDataSourceConfig {

    @Value("master.datasource.username")
    private String masterUsername;
    @Value("master.datasource.password")
    private String masterPassword;
    @Value("master.datasource.driver")
    private String masterDriver;
    @Value("master.datasource.url")
    private String masterUrl;

    /**
     * 配置master数据源
     *
     * @return
     */
    @Bean
    public DruidDataSource masterDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(masterUrl);
        druidDataSource.setUsername(masterUsername);
        druidDataSource.setPassword(masterPassword);
        druidDataSource.setDriverClassName(masterDriver);
        return druidDataSource;
    }

    @Bean
    public SqlSessionFactoryBean masterSqlSessionFactoryBean(DruidDataSource masterDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(masterDataSource);
        return sqlSessionFactoryBean;
    }
}
