package cn.springcamp.springdynamicdatasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "dynamic-data")
public class DatabaseConfig {
    private List<DbSchema> schemas = new ArrayList<>();

    @PostConstruct
    public void init() {
        for (DbSchema current : this.getSchemas()) {
            HikariConfig jdbcConfig = new HikariConfig();
            jdbcConfig.setJdbcUrl(current.getDatasource().getUrl());
            jdbcConfig.setUsername(current.getDatasource().getUsername());
            String password = current.getDatasource().getPassword();
            jdbcConfig.setPassword(password);
            try {
                HikariDataSource hikariDataSource = new HikariDataSource(jdbcConfig);
                current.setJdbcTemplate(new JdbcTemplate(hikariDataSource));
            } catch (Exception e) {
                log.error("connect to " + current.getDatasource().getUrl() + "  failed.");
                throw e;
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class DbSchema {
        private String code;
        private DataSourceProperties datasource;
        private String query;
        private JdbcTemplate jdbcTemplate;
    }
}
