package cn.springcamp.springdatajpa.multisource.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "otherEntityManagerFactory",
        transactionManagerRef = "otherTransactionManager",
        basePackages = {"cn.springcamp.springdatajpa.multisource.other.data"}
)
public class OtherDataConfig {
    @Autowired
    private OtherJpaProperties jpaProperties;

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
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(otherDataSource())
                .packages("cn.springcamp.springdatajpa.multisource.other.data")
                .properties(jpaProperties.getProperties())
                .persistenceUnit("other")
                .build();

//        LocalContainerEntityManagerFactoryBean em
//                = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(otherDataSource());
//        em.setPackagesToScan("cn.springcamp.springdatajpa.multisource.other.data");
//        em.setPersistenceUnitName("other");
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setDatabasePlatform(otherJpaProperties.getDatabasePlatform());
//        vendorAdapter.setDatabase(otherJpaProperties.getDatabase());
//        vendorAdapter.setGenerateDdl(otherJpaProperties.isGenerateDdl());
//        vendorAdapter.setShowSql(otherJpaProperties.isShowSql());
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.setJpaPropertyMap(otherJpaProperties.getProperties());
//        return em;
    }

    @Bean(name = "otherTransactionManager")
    public PlatformTransactionManager otherTransactionManager(
            @Qualifier("otherEntityManagerFactory") EntityManagerFactory otherEntityManagerFactory) {
        return new JpaTransactionManager(otherEntityManagerFactory);
    }

}
