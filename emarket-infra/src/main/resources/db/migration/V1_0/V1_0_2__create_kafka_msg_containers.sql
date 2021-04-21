create table "sent_msg_container" (
    "id" varchar primary key,
    "transaction_id" varchar,
    "creation_time" timestamp not null,
    "json_serialized_msg" text not null,
    "msg_type" varchar not null,
    "status" varchar,
    "topic" varchar not null,
    "origin_message" varchar not null,
    "block_time" timestamp,
    "version_schema" varchar,
    "id_utilisateur" varchar,
    "id_objet" varchar,
    "num_version_objet" numeric
);

create table "received_ult" (
    "ulid" varchar not null,
    "topic" varchar not null,
    "partition" numeric not null,
    "retries_no" numeric not null,
    "msg_offset" numeric not null,
    "ts" timestamp,
    PRIMARY KEY("topic", "partition")
);
