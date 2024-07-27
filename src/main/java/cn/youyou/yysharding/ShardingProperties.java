package cn.youyou.yysharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {
    private Map<String, Properties> datasources = new LinkedHashMap<>();
}
