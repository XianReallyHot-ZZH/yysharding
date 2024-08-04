package cn.youyou.yysharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sharding 的结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardingResult {

    // （分库结果）分片结果对应的数据源名称，比如mysql的话，对应的就是database
    String targetDataSourceName;

    // （分表结果）分片结果对应的表名称，比如mysql的话，对应的就是table
    String targetSqlStatement;

}
