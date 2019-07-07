package cn.springcamp.springdatajpa.multisource.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "otherEntityManagerFactory",
        transactionManagerRef = "otherTransactionManager",
        basePackages = {"cn.springcamp.springdatajpa.multisource.other.data"}
)
public class OtherDataConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Bean
    @ConfigurationProperties(prefix = "other.datasource")
    public DataSourceProperties otherDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "otherDataSource")
    @ConfigurationProperties(prefix = "other.datasource.hikari")
    public HikariDataSource otherDataSource() {
        return otherDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "otherEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean otherEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("otherDataSource") DataSource otherDataSource) {
        return builder
                .dataSource(otherDataSource)
                .packages("cn.springcamp.springdatajpa.multisource.other.data")
                .persistenceUnit("other")
                .build();
    }

    @Bean(name = "otherTransactionManager")
    public PlatformTransactionManager otherTransactionManager(
            @Qualifier("otherEntityManagerFactory") EntityManagerFactory otherEntityManagerFactory) {
        return new JpaTransactionManager(otherEntityManagerFactory);
    }

}
