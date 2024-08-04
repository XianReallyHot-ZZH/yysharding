package cn.youyou.yysharding.mybatis;

import cn.youyou.yysharding.engine.ShardingContext;
import cn.youyou.yysharding.engine.ShardingEngine;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ShardingMapperFactoryBean 泛型T 为 Mapper接口
 * <p>
 * 技巧：使用FactoryBean
 * FactoryBean其实是一个工厂Bean，它负责为spring容器创建一个Bean，bean的创建逻辑在方法getObject()中实现。
 * 这里这个Bean是Mapper接口的代理对象（实现类），目的是为mapper的方法进行增强，在执行方法前，完成分库分表的计算。
 *
 * @param <T>
 */
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    @Setter
    ShardingEngine engine;

    public ShardingMapperFactoryBean() {
    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        Class<T> clazz = getMapperInterface();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {

            String mapperId = clazz.getName() + "." + method.getName();
            MappedStatement statement = getSqlSession().getConfiguration().getMappedStatement(mapperId);
            BoundSql boundSql = statement.getBoundSql(args);
            log.info(">>> sql: {}", boundSql.getSql());
            log.info(">>> parameter: {}", boundSql.getParameterObject().toString());

            // 使用sharding engine 针对sql和真实入参进行sharding得到sharding result
            ShardingContext.set(engine.sharding(boundSql.getSql(), getParams(boundSql, args)));

            return method.invoke(proxy, args);
        });
    }

    /**
     * boundSql的解析结果，将入参args，解析成对应sql的入参的数组形式返回(将mybatis的入参对应于sql的入参做平铺处理)
     *
     * @param boundSql
     * @param args
     * @return
     */
    @SneakyThrows
    private Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] result = args;
        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {  // 不是原始对象
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).collect(Collectors.toList());
            Object[] newParams = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                newParams[i] = getFieldValue(arg, cols.get(i));
            }
            result = newParams;
        }
        return result;
    }


    private static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
