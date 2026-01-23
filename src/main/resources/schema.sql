drop table if exists "SMSMessage";
drop table if exists "OptOut";

create table if not exists "SMSMessage"(
    id int auto_increment primary key,
    message varchar(255) not null,
    senderPhoneNuber  varchar(20) not null,
    receiverPhoneNumber  varchar(20) not null,
    areaCode varchar(20),
    status varchar(20),
    carrier varchar(20)
);

create table if not exists "OptOut"(
    id int  auto_increment primary key,
    phone_number varchar(20) not null
);

