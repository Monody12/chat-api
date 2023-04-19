create database if not exists chat;


create table user
(
    `id` int auto_increment primary key,
    `username` varchar(127) not null comment '用户名',
    `password` varchar(127) not null comment '密码',
    `nickname` varchar(127) not null comment '昵称',
    `key` varchar(127) null comment '密钥',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    `update_time` timestamp not null default current_timestamp on update current_timestamp comment '更新时间'
) engine=InnoDB default charset=utf8mb4 comment '用户表';
/* 对用户名建立唯一索引 */
alter table user add unique index `username` (`username`);

create table chat_key
(
    `id` int auto_increment primary key,
    `key` varchar(127) not null comment '密钥',
    `times` int not null default 0 comment '剩余次数',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    `update_time` timestamp not null default current_timestamp on update current_timestamp comment '更新时间'
) engine=InnoDB default charset=utf8mb4 comment '密钥表';
/* 对密钥建立索引 */
alter table chat_key add index `key` (`key`);

create table chat_key_bind
(
    `id` int auto_increment primary key,
    `key_id` int not null comment '密钥id',
    `user_id` int not null comment '用户id',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    `update_time` timestamp not null default current_timestamp on update current_timestamp comment '更新时间'
) engine=InnoDB default charset=utf8mb4 comment '密钥绑定表';
/* 对密钥id和用户id建立唯一联合索引 */
alter table chat_key_bind add unique index `key_id_user_id` (`key_id`, `user_id`);

create table chat_topic
(
    `id` int auto_increment primary key,
    `user_id` int not null comment '用户id',
    `topic` varchar(127) not null comment '话题',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    `update_time` timestamp not null default current_timestamp on update current_timestamp comment '更新时间'
) engine=InnoDB default charset=utf8mb4 comment '话题表';
/* 对用户id建立索引 */
alter table chat_topic add index `user_id` (`user_id`);

create table chat_history
(
    `id` int auto_increment primary key,
    `user_id` int not null comment '用户id',
    `topic_id` int not null comment '话题id',
    `role` varchar(31) not null comment '角色 user or assistant',
    `content` varchar(4095) not null comment '内容',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    `update_time` timestamp not null default current_timestamp on update current_timestamp comment '更新时间'
) engine=InnoDB default charset=utf8mb4 comment '聊天记录表';
/* 对用户id和话题id建立联合索引 */
alter table chat_history add index `user_id_topic_id` (`user_id`, `topic_id`);
