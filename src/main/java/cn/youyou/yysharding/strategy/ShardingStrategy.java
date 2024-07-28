package cn.youyou.yysharding.strategy;

import java.util.List;
import java.util.Map;

public interface ShardingStrategy {

    List<String> getShardingColumns();

    /**
     * 根据逻辑表的配置和当前的参数计算出目标数据源
     *
     * @param availableTargetNames      分库或者分表的可选择的真实数据源（分库对应的就是数据库，分表对应的就是数据表）
     * @param logicTableName            逻辑表名
     * @param shardingParams            运行时的sharding参数（对应用户操作sql的入参）
     * @return
     */
    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);

}
