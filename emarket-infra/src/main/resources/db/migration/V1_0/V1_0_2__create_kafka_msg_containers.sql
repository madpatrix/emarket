create table "sent_msg_container" (
    "id" varchar primary key,
    "transaction_id" varchar,
    "creation_time" timestamp not null,
    "json_serialized_msg" text not null,
    "msg_type" varchar not null,
    "status" varchar,
    "topic" varchar not null,
    "origin_message" varchar not null,
    "block_time" timestamp
);

create table "received_msg_container" (
    "id" varchar primary key,
    "json_serialized_msg" text not null,
    "msg_creation_time" timestamp not null,
    "msg_type" varchar not null,
    "received_time" timestamp not null,
    "status" varchar,
    "topic" varchar not null,
    "origin_message" varchar not null,
    "block_time" timestamp
);
