
    create sequence users_sequence start with 1 increment by 1;

    create table users (
        id bigint not null,
        joined timestamp(6),
        password varchar(255),
        username varchar(255),
        primary key (id)
    );
