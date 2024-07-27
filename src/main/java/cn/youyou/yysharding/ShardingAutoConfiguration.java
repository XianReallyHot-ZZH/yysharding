package cn.youyou.yysharding;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * sharding 框架的自动装配类
 * 技巧: 框架的自动装配类
 * 后续框架要自动注入的类,都需要在ShardingAutoConfiguration中进行显式的注册bean，后续方便以注解的方式在应用代码中引入框架
 */
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }

}
