package cn.youyou.yysharding.engine;

import cn.youyou.yysharding.config.ShardingProperties;

/**
 * sharding engine 的标准实现
 */
public class StandardShardingEngine implements ShardingEngine{
    public StandardShardingEngine(ShardingProperties properties) {

    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        return null;
    }
}
