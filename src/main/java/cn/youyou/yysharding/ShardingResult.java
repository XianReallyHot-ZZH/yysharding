package cn.youyou.yysharding;

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

    // 分片结果对应的数据源名称，比如mysql的话，对应的就是database
    String targetDataSourceName;

}
