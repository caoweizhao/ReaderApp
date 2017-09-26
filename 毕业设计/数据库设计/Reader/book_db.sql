
drop table if exists Book;

drop table if exists Collection;

drop table if exists User;

create table Book
(
   id                   int(11) not null auto_increment,
   name                 text not null,
   url                  text not null,
   img_url              text not null,
   primary key (id)
);


create table Collection
(
   id                   int(11) not null,
   user_name            text not null,
   primary key (id, user_name)
);


create table User
(
   user_name            text not null,
   user_password        text not null,
   primary key (user_name)
);

alter table Collection add constraint FK_Collection foreign key (id)
      references Book (id) on delete restrict on update restrict;

alter table Collection add constraint FK_Collection2 foreign key (user_name)
      references User (user_name) on delete restrict on update restrict;

