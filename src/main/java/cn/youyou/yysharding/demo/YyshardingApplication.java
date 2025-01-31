package cn.youyou.yysharding.demo;

import cn.youyou.yysharding.config.ShardingAutoConfiguration;
import cn.youyou.yysharding.demo.mapper.OrderMapper;
import cn.youyou.yysharding.demo.model.Order;
import cn.youyou.yysharding.mybatis.ShardingMapperFactoryBean;
import cn.youyou.yysharding.demo.model.User;
import cn.youyou.yysharding.demo.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "cn.youyou.yysharding.demo", factoryBean = ShardingMapperFactoryBean.class)
public class YyshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(YyshardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Autowired
    OrderMapper orderMapper;

    @Bean
    ApplicationRunner runner() {
        return args -> {

            System.out.println(" ===============>  ===============>  ===============>");
            System.out.println(" ===============> test user sharding ===============>");
            System.out.println(" ===============>  ===============>  ===============>");
            for (int id = 1; id <= 60; id++) {
                testUser(id);
            }

            System.out.println("\n\n\n\n");
            System.out.println(" ===============>  ===============>   ===============>");
            System.out.println(" ===============> test order sharding ===============>");
            System.out.println(" ===============>  ===============>   ===============>");
            for (int id = 1; id <= 40; id++) {
                testOrder(id);
            }

        };
    }

    private void testUser(int id) {

        System.out.println("\n\n ===> 1. test insert ...");
        int inserted = userMapper.insert(new User(id, "youyou", 20));
        System.out.println(" ===> inserted = " + inserted);

        System.out.println(" ===> 2. test find ...");
        User user = userMapper.findById(id);
        System.out.println(" ===> find = " + user);

        System.out.println(" ===> 3. test update ...");
        user.setName("YY");
        int updated = userMapper.update(user);
        System.out.println(" ===> updated = " + updated);

        System.out.println(" ===> 4. test new find ...");
        User user2 = userMapper.findById(id);
        System.out.println(" ===> find = " + user2);

        System.out.println(" ===> 5. test delete ...");
        int deleted = userMapper.delete(id);
        System.out.println(" ===> deleted = " + deleted);
    }

    private void testOrder(int id) {
        int id2 = id + 100;
        System.out.println("\n\n ===============> id = " + id);
        System.out.println(" ===> 1. test insert ...");
        int inserted = orderMapper.insert(new Order(id, 1, 10d));
        System.out.println(" ===> inserted = " + inserted);
        inserted = orderMapper.insert(new Order(id2, 2, 20d));
        System.out.println(" ===> inserted = " + inserted);

        System.out.println(" ===> 2. test find ...");
        Order order1 = orderMapper.findById(id, 1);
        System.out.println(" ===> find = " + order1);
        Order order2 = orderMapper.findById(id2, 2);
        System.out.println(" ===> find = " + order2);

        System.out.println(" ===> 3. test update ...");
        order1.setPrice(11d);
        int updated = orderMapper.update(order1);
        System.out.println(" ===> updated = " + updated);
        order2.setPrice(22d);
        updated = orderMapper.update(order2);
        System.out.println(" ===> updated = " + updated);

        System.out.println(" ===> 4. test new find ...");
        Order order11 = orderMapper.findById(id, 1);
        System.out.println(" ===> find = " + order11);
        Order order22 = orderMapper.findById(id2, 2);
        System.out.println(" ===> find = " + order22);

        System.out.println(" ===> 5. test delete ...");
        int deleted = orderMapper.delete(id, 1);
        System.out.println(" ===> deleted = " + deleted);
        deleted = orderMapper.delete(id2, 2);
        System.out.println(" ===> deleted = " + deleted);
    }

}
