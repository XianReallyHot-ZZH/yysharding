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


