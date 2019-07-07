package cn.springcamp.springdatajpa.multisource.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "other.jpa")
public class OtherJpaProperties {
    private Map<String, String> properties = new HashMap<>();

    /**
     * Mapping resources (equivalent to "mapping-file" entries in persistence.xml).
     */
    private final List<String> mappingResources = new ArrayList<>();

    /**
     * Name of the target database to operate on, auto-detected by default. Can be
     * alternatively set using the "Database" enum.
     */
    private String databasePlatform;

    /**
     * Target database to operate on, auto-detected by default. Can be alternatively set
     * using the "databasePlatform" property.
     */
    private Database database;

    /**
     * Whether to initialize the schema on startup.
     */
    private boolean generateDdl = false;

    /**
     * Whether to enable logging of SQL statements.
     */
    private boolean showSql = false;

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<String> getMappingResources() {
        return this.mappingResources;
    }

    public String getDatabasePlatform() {
        return this.databasePlatform;
    }

    public void setDatabasePlatform(String databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    public Database getDatabase() {
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public boolean isGenerateDdl() {
        return this.generateDdl;
    }

    public void setGenerateDdl(boolean generateDdl) {
        this.generateDdl = generateDdl;
    }

    public boolean isShowSql() {
        return this.showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

}
