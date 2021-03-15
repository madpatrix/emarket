create table "order_item" (
    "id" varchar primary key,
    "customer_id" varchar not null,
    "status" varchar not null,
    "version" numeric(15) not null,
    constraint fk_order_customer
    foreign key ("customer_id")
    references "customer"("id")
);


create table "order_line" (
    "order_id" varchar not null,
    "quantity" numeric(10) not null,
    "product_id" varchar not null,
    constraint fk_order_line
    foreign key ("order_id")
    references "order_item"("id")
);
