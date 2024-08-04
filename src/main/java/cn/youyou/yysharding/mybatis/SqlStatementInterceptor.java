package cn.youyou.yysharding.mybatis;

import cn.youyou.yysharding.engine.ShardingContext;
import cn.youyou.yysharding.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * mybatis 拦截器
 * 作用: 在执行sql语句前，拦截并根据ShardingResult修改mybatis相关对象中的属性值（这里主要是修改BoundSql对象中的sql字符串）
 *
 * 技术栈说明：
 *  1、java的UnsafeUtils内存操作工具：用于实现对对象final属性运行时进行属性值的替换，原理相当于直接操作对象的内存模型，直接替换对应属性的内存地址引用，引用到新的值
 */
@Intercepts(@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {java.sql.Connection.class, Integer.class}
))
public class SqlStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ShardingResult result = ShardingContext.get();
        if (result != null) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            String targetSqlStatement = result.getTargetSqlStatement();
            if (!sql.equalsIgnoreCase(targetSqlStatement)) {
                replaceSql(boundSql, targetSqlStatement);
            }
        }
        return invocation.proceed();
    }

    /**
     * 替换BoundSql对象中的sql属性值
     * @param boundSql
     * @param sql
     */
    private void replaceSql(BoundSql boundSql, String sql) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        // 找到对象对应属性的内存地址偏移量
        long offset = unsafe.objectFieldOffset(field);
        // 设置对象属性的内存地址引用为新值
        unsafe.putObject(boundSql, offset, sql);
    }


}
