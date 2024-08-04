package cn.youyou.yysharding.engine;

import cn.youyou.yysharding.config.ShardingProperties;
import cn.youyou.yysharding.strategy.HashShardingStrategy;
import cn.youyou.yysharding.strategy.ShardingStrategy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * sharding engine 的标准实现
 * 主要功能：
 * 根据用户使用的sharding的具体配置，对sql中涉及的逻辑逻辑表进行sharding
 * <p>
 * 注意：
 * 这里的sharding实现覆盖的场景比较简单，目前只适配单表且配置了分库分表字段的sql（insert/update/delete/select）
 * <p>
 * 技术栈说明：
 * 1、sql解析：使用druid的sql解析器，解析出sql中的表名、字段名、参数等
 */
@Slf4j
public class StandardShardingEngine implements ShardingEngine {

    // 分库涉及的所有库（数据源），key为逻辑表名，value为实际可用的库名（是个list）
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    // 分表涉及的所有表，key为逻辑表名，value为实际可用的表名（是个list）
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    // 逻辑表对应的分库策略（当前只支持一种基于hash的sharding计算，后期可考虑扩展）
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    // 逻辑表对应的分表策略（当前只支持一种基于hash的sharding计算，后期可考虑扩展）
    private final Map<String, ShardingStrategy> tableStrategies = new HashMap<>();

    /**
     * 根据sharding的用户配置，解析出sharding参数，用于运行时的sharding计算逻辑
     *
     * @param properties
     */
    public StandardShardingEngine(ShardingProperties properties) {
        properties.getTables().forEach((table, tableProperties) -> {
            // 1、解析分库和分表的候选库和表
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] parts = actualDataNode.split("\\.");
                String databaseName = parts[0], tableName = parts[1];
                actualDatabaseNames.add(table, databaseName);
                actualTableNames.add(table, tableName);
            });
            // 2、解析分库和分表的策略
            databaseStrategies.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
        });
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        // 1、解析sql，获取sql中的表名、字段名、参数等
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        // 逻辑表
        String table;
        // sql的入参
        Map<String, Object> shardingColumnsMap;

        // 2、处理insert
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = new HashMap<>();
            // 入参的columns
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                shardingColumnsMap.put(((SQLIdentifierExpr) columns.get(i)).getSimpleName(), args[i]);
            }
        } else {
            // 3、处理update、delete、select
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> tablesNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (tablesNames.size() > 1) {
                throw new RuntimeException("not support multi tables sharding: " + tablesNames);
            }
            table = tablesNames.iterator().next().getSimpleName();
            log.info(" ===>>> [StandardShardingEngine] visitor.getOriginalTables = {}", table);
            shardingColumnsMap = visitor.getConditions().stream().collect(Collectors.toMap(c -> c.getColumn().getName(), c -> c.getValues().get(0)));
            log.info(" ===>>> [StandardShardingEngine] visitor.getConditions = {}", shardingColumnsMap);
        }

        // 4、根据sharding策略计算出分库分表结果
        String targetDatabase = databaseStrategies.get(table).doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
        String targetTable = tableStrategies.get(table).doSharding(actualTableNames.get(table), table, shardingColumnsMap);
        log.info(" ===>>> ");
        log.info(" ===>>> [StandardShardingEngine] target db.table = {}.{}", targetDatabase, targetTable);
        log.info(" ===>>> ");

        // TODO：这里的sql 替换，目前只做最简单的表替换，后续这块sql改写的能力待扩展，扩展成和ShardingSphere类似的一个改写引擎。专门用来扩展分布式查询下的sql改写
        return new ShardingResult(targetDatabase, sql.replace(table, targetTable));
    }
}
