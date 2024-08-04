create database 'db0';
create database 'db1';

create table db0.user
(
    `id`   int         not NULL,
    `name` varchar(32) NOT NULL,
    `age`  int DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

create table db1.user
(
    `id`   int         not NULL,
    `name` varchar(32) NOT NULL,
    `age`  int DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

create table user0 like user;
create table user1 like user;
create table user2 like user;

delete from user0;
delete from user1;
delete from user2;

select * from (select * from db0.user0 union select * from db0.user1 union select * from db0.user2
               union select * from db1.user0 union select * from db1.user1 union select * from db1.user2) as a order by id asc;




CREATE TABLE `t_order`
(
    `id`    int         NOT NULL,
    `uid`   varchar(32) NOT NULL,
    `price` decimal(10, 0) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

create table t_order0 like t_order;
create table t_order1 like t_order;

delete from t_order;
delete from t_order0;
delete from t_order1;

delete from t_order0;
delete from t_order1;