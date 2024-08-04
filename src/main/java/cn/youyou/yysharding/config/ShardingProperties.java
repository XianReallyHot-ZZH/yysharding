package cn.youyou.yysharding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {
    // 数据源（库），key为数据源名称， value为数据源配置
    private Map<String, Properties> datasources = new LinkedHashMap<>();

    // sharding的逻辑表配置，key为逻辑表名称，value为逻辑表的sharding具体配置
    private Map<String, TableProperties> tables = new LinkedHashMap<>();

    /**
     * 逻辑表的具体sharding配置
     */
    @Data
    public static class TableProperties {
        private List<String> actualDataNodes;
        private Properties databaseStrategy;
        private Properties tableStrategy;
    }

}
