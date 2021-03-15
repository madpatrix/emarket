create table "customer" (
    "id" varchar primary key,
    "first_name" varchar not null,
    "last_name" varchar not null,
    "street_name" varchar,
    "street_number" numeric(3,0),
    "card_number" varchar,
    "phone_number" varchar,
    "registration_date" timestamp,
    "last_login_date" timestamp,
    "version" numeric(15) not null
);


create table "customer_order" (
    "customer_id" varchar not null,
    "order_id" varchar not null,
    constraint fk_customer
    foreign key ("customer_id")
    references "customer"("id")
);

create unique index customer_orders_unq_idx on
  "customer_order"("customer_id", "order_id");
