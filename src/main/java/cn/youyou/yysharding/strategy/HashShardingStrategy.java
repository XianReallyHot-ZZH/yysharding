package cn.youyou.yysharding.strategy;

import java.util.List;
import java.util.Map;

public class HashShardingStrategy implements ShardingStrategy{
    @Override
    public List<String> getShardingColumns() {
        return null;
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        return null;
    }
}
