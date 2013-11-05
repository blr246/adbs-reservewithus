-- ReserveWithUs schema
-- Copyright (c) 2010 Philippe Bonnet, Dennis Shasha
--
-- ReserveWithUs is an electronic portal for hotel rooms.
-- It buys rooms at discounted rates and sells them for a bit more.
-- Its clientele is international so the website is multi-lingual.
-- 
-- This is the schema for the ReserveWithUs tuning case study.
-- 
-- This original schema defines:
-- - HOTEL: The hotels available through ReserveWithUs.
-- - ROOM: ROOM_TYPE keeps track of the types of rooms per hotel
--         ROOM_DESC stores the multilanguage representation for each room
--         ROOM_DATE table keeps track of the number of available rooms and the number of rooms
--         that have already been taken. Triggers ensure that numtaken is in the range [0,numavail]
--         on insertions and updates.
--         Descriptions (possibly in multiple languages) are associated to those rooms.
-- - CUSTOMER: stores information about the ReserveWithUs customers.
-- - ORDERS:  The BOOKED table is used to regiser all booking transactions.
--            The SHOPPING_CART table keeps track of the active shopping carts.
--            The LOGIN table keeps track of the customers logged in.
--            The LOGIN_COUNTER table keeps track of the log in sequence number
--

-- HOTEL
create table HOTEL (hotel_id int not null,  name varchar(50),  street varchar(100),  city varchar(30),  zip_code int,  state varchar(30),  country varchar(30),  rating int,  distance_to_center float,PRIMARY KEY (hotel_id) );

-- ROOM
create table ROOM_TYPE (  room_type_id int not null,  hotel_id int,  room_type varchar(100),  PRIMARY KEY (room_type_id),	  FOREIGN KEY (hotel_id) REFERENCES HOTEL (hotel_id));

-- create table ROOM_DESC (  room_type_id int not null,  language varchar(10) not null,  description varchar(500),  PRIMARY KEY (room_type_id, language),  FOREIGN KEY (room_type_id) REFERENCES ROOM_TYPE (room_type_id));

create table ROOM_DATE (  room_type_id int not null,  single_day_date date not null,  numavail int,   numtaken int,   price decimal,  PRIMARY KEY (room_type_id, single_day_date),  FOREIGN KEY (room_type_id) REFERENCES ROOM_TYPE (room_type_id));


-- CUSTOMER
create table CUSTOMER (  customer_id int not null,  username varchar(30 ),  password blob,  first_name varchar(30),  last_name varchar(30),   home_street varchar(100),  home_city varchar(30),  home_zip_code int,  home_state varchar(30),  business_street varchar(100),  business_city varchar(30),  business_zip_code int,  business_state varchar(30),  home_phone varchar(10),  business_phone varchar(10),  email varchar(20),  language varchar(12),  PRIMARY KEY (customer_id));
 
--  ORDERS
-- create table BOOKED (  customer_id int not null,  single_day_date date not null,  room_type_id int not null,  numtaken int,  price decimal,   checkout_status int,   PRIMARY KEY (customer_id, room_type_id, single_day_date),  FOREIGN KEY (customer_id) REFERENCES CUSTOMER (customer_id),  FOREIGN KEY (room_type_id) REFERENCES ROOM_TYPE (room_type_id));
create table BOOKED (  customer_id int not null,  date_start date not null,  date_stop date not null,  room_type_id int not null,  numtaken int,  total_price decimal,   checkout_status int,   PRIMARY KEY (customer_id, room_type_id, date_start, date_stop));

-- create table SHOPPING_CART (  customer_id int not null,  single_day_date date not null, room_type_id int not null,  numtaken int,  total_price decimal,   PRIMARY KEY (customer_id, room_type_id, single_day_date),  FOREIGN KEY (customer_id) REFERENCES CUSTOMER (customer_id),  FOREIGN KEY (room_type_id) REFERENCES ROOM_TYPE (room_type_id));
create table SHOPPING_CART (  customer_id int not null,  date_start date not null, date_stop date not null, room_type_id int not null,  numtaken int,  total_price decimal,   PRIMARY KEY (customer_id, room_type_id, date_start, date_stop));

-- LOGIN
create table LOGIN (login_id int not null, customer_id int not null, primary key (login_id, customer_id));
create table LOGIN_COUNTER (value int default 1);
insert into LOGIN_COUNTER values (1);