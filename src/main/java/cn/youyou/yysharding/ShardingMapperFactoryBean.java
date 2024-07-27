package cn.youyou.yysharding;

import cn.youyou.yysharding.demo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

/**
 * ShardingMapperFactoryBean 泛型T 为 Mapper接口
 *
 * 技巧：使用FactoryBean
 * FactoryBean其实是一个工厂Bean，它负责为spring容器创建一个Bean，bean的创建逻辑在方法getObject()中实现。
 * 这里这个Bean是Mapper接口的代理对象（实现类），目的是为。
 *
 * @param <T>
 */
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ShardingMapperFactoryBean() {}

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public T getObject() throws Exception {
        Class<T> clazz = getMapperInterface();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {

            String mapperId = clazz.getName() + "." + method.getName();
            MappedStatement statement = getSqlSession().getConfiguration().getMappedStatement(mapperId);
            BoundSql boundSql = statement.getBoundSql(args);
            log.info("sql: {}", boundSql.getSql());
            log.info("parameter: {}", boundSql.getParameterObject().toString());

            // sharding engine,根据sql进行sharding，这里先写死
            Object parameterObject = args[0];
            if(parameterObject instanceof User user) {
                ShardingContext.set(new ShardingResult(user.getId() % 2 == 0 ? "ds0" : "ds1"));
            } else if(parameterObject instanceof Integer id) {
                ShardingContext.set(new ShardingResult(id % 2 == 0 ? "ds0" : "ds1"));
            }

            return method.invoke(super.getObject(), args);
        });
    }
}
