load from "./customer.data" of del modified by coldel| method P (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17) INSERT  INTO CUSTOMER;
load from "./hotel.data" of del modified by coldel| method P (1,2,3,4,5,6,7,8,9) INSERT  INTO HOTEL;
load from "./roomtype.data" of del modified by coldel| method P (1,2,3) INSERT  INTO ROOM_TYPE;
set integrity for ROOM_TYPE allow no access immediate checked;
load from "./roomdate.data" of del modified by coldel| method P (1,2,3,4,5) INSERT  INTO ROOM_DATE;
set integrity for ROOM_DATE allow no access immediate checked;
insert into LOGIN_COUNTER values (1);
