# --- !Ups

create table UserData(
id int auto_increment primary key,
firstname varchar(20) not null,
middlename varchar(20),
lastname varchar(20) not null,
username varchar(20) not null,
password varchar(20) not null,
mobileNo varchar(10),
age int,
gender varchar(10),
hobbies varchar(20),
isEnabled boolean default true,
isAdmin boolean
);

create table Assignment(
id int auto_increment primary key,
title varchar(30) not null,
description varchar(100)
);

# --- !Downs

drop table UserData;
drop table Assignment;