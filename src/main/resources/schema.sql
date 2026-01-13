drop table if exists "SMSMessage";
drop table if exists "OptOut";

create table if not exists "SMSMessage"(
    id int auto_increment primary key,
    messageBody varchar (225) not null,
    senderPhoneNumber  varchar(225) not null,
    receiverPhoneNumber varchar (225) not null,
    areaCode varchar (225) not null,
    carrier varchar (225) not null

);





create table if not exists "OptOut"(
    id int auto_increment primary key,
    phone_number varchar(20) not null
);

