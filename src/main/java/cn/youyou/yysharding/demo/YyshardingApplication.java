package cn.youyou.yysharding.demo;

import cn.youyou.yysharding.config.ShardingAutoConfiguration;
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

    @Bean
    ApplicationRunner runner() {
        return args -> {
            for (int i = 0; i < 10; i++) {
                test(i);
            }
        };
    }

    private void test(int id) {

        System.out.println(" ===> 1. test insert ...");
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

    }

}
