CREATE TABLE "CUSTOMER" (
    "ID" VARCHAR2(36 CHAR) PRIMARY KEY,
    "FIRST_NAME" VARCHAR2(50 CHAR) NOT NULL,
    "LAST_NAME" VARCHAR2(50 CHAR) NOT NULL,
    "STREET_NAME" VARCHAR2(200 CHAR) NOT NULL,
    "STREET_NUMBER" NUMBER(3,0) NOT NULL,
    "CARD_NUMBER" VARCHAR2(24 CHAR),
    "PHONE_NUMBER" VARCHAR2(100 CHAR),
    "REGISTRATION_DATE" TIMESTAMP,
    "LAST_LOGIN_DATE" TIMESTAMP,
    "VERSION" NUMBER(15) NOT NULL
);


CREATE TABLE "CUSTOMER_ORDER" (
    "CUSTOMER_ID" VARCHAR2(36 CHAR) NOT NULL,
    "ORDER_ID" VARCHAR2(36 CHAR) NOT NULL,
    CONSTRAINT FK_CUSTOMER
    FOREIGN KEY (CUSTOMER_ID)
    REFERENCES CUSTOMER(ID)
);

CREATE UNIQUE INDEX CUSTOMER_ORDERS_UNQ_IDX ON
  CUSTOMER_ORDER(CUSTOMER_ID, ORDER_ID);
