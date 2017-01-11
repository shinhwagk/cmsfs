create database oso_config;

drop table host;
create table host (
  id int auto_increment primary key,
  label varchar(20),
  hostname varchar(20),
  ip varchar(20),
  port int,
  tags json,
  status int
) charset=utf8;

drop table rdatabase;
create table rdatabase (
  id int auto_increment primary key,
  host_id int,
  label varchar(20),
  type varchar(30),
  jdbcUrl varchar(530),
  user varchar(20),
  password varchar(20),
  status int,
  tag json
);

drop table monitor;
create table monitor (
  id int auto_increment primary key,
  name varchar(30),
  label varchar(30),
  category varchar(30),
  code text,
  args json,
  cron varchar(20),
  host_id json
);

insert into monitor values(1,"tablespace_space","表空间","ORACLE","select * from dual",'["aa"]',"0 * * * * *",'[1,2,3]');
