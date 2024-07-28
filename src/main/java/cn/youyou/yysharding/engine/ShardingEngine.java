package cn.youyou.yysharding.engine;

public interface ShardingEngine {

    /**
     * 根据sql语句和sql执行参数，计算出sharding结果（分库分表结果）
     *
     * @param sql
     * @param args
     * @return
     */
    ShardingResult sharding(String sql, Object[] args);

}
