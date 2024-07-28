package cn.youyou.yysharding.datasource;

import cn.youyou.yysharding.engine.ShardingContext;
import cn.youyou.yysharding.engine.ShardingResult;
import cn.youyou.yysharding.config.ShardingProperties;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 技巧1：继承AbstractRoutingDataSource
 * 继承AbstractRoutingDataSource的作用：
 * 1、设置多数据源
 * 2、运行时多数据源的路由判定实现
 *
 * 技巧2：使用Druid连接池工具创建数据源
 *
 * 补充说明：
 *  继承的AbstractRoutingDataSource是spring框架的数据源路由抽象类，实现类要成为spring的bean才能起作用.
 *
 */
@Slf4j
public class ShardingDataSource extends AbstractRoutingDataSource {

    public ShardingDataSource(ShardingProperties properties) {
        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
        properties.getDatasources().forEach((k, v) -> {
            try {
                dataSourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // 设置多数据源
        setTargetDataSources(dataSourceMap);
        // 默认数据源：当determineCurrentLookupKey没有结果的时候，那么就使用默认的数据源
        setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.get();
        Object key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();
        log.info("determineCurrentLookupKey: {}", key);
        return key;
    }
}
