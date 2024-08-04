package cn.youyou.yysharding.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * sharding的具体执行策略
 * 用法说明：
 *  每个逻辑表都会有一个对应的shardingStrategy的实现类
 *
 * 技术栈说明：
 *  1、groovy：用于自定义文本表达式的计算
 *
 */
public class HashShardingStrategy implements ShardingStrategy{

    private final String shardingColumn;

    private final String algorithmExpression;

    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }

    @Override
    public List<String> getShardingColumns() {
        return null;
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        String expression = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parse = new InlineExpressionParser(expression);
        Closure<?> closure = parse.evaluateClosure();
        closure.setProperty(shardingColumn, shardingParams.get(shardingColumn));
        return closure.call().toString();
    }
}
