package cn.youyou.yysharding.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

/**
 * mybatis 拦截器
 * 作用: 在执行sql语句前，拦截并根据ShardingResult修改sql语句，具体为将sql语句中的表名替换成sharding后对应的表名
 */
@Intercepts(@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {java.sql.Connection.class, Integer.class}
))
public class SqlStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }
}
