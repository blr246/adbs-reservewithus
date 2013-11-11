/*
 * Copyright (c) 2010, Ph.Bonnet and D.Shasha
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.databasetuning.cases.ReserveWithUs.Application;

import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author philippe
 */
class DB2SQLStatements {

    // Shopping Cart and Login
    public static String login_check(int customer_id) {
        return "select count(*) from LOGIN where customer_id = "+customer_id;
    }

    public static String login_insert(int customer_id, int login_id) {
        return "insert into LOGIN values ("+login_id+","+customer_id+")";
    }

    public static String login_remove(int customer_id) {
        return "delete from LOGIN where customer_id = "+customer_id;
    }

   public static String login_count() {
        return "select count(*) from LOGIN";
    }

    public static String login_select_logcounter() {
        return "select value from LOGIN_COUNTER for update";
    }
    
    public static String login_increment_logcounter(int value) {
        return "update LOGIN_COUNTER set value = "+value;
    }

    public static String shopping_cart_insert(ShoppingCartItem sci) {
        return "insert into SHOPPING_CART values ("
              +sci.getCustomer_id()+",'"+sci.getDate_start()+"','"+sci.getDate_stop()+"',"
              +sci.getRoom_type_id()+","+sci.getNumtaken()+","+sci.getTotal_price()+")";
    }

    public static String shopping_cart_delete(int customer_id, Date date_start,
            Date date_stop, int room_type_id) {
        return "delete from SHOPPING_CART where customer_id ="+customer_id+
              " and date_start='"+date_start+"' and date_stop='"+date_stop
              +"' and room_type_id="+room_type_id;
    }

    public static String shopping_cart_getAll(int customer_id) {
        return "select * from SHOPPING_CART where customer_id ="+customer_id;
    }


    public static String shopping_cart_deleteAll(int customer_id) {
        return "delete from SHOPPING_CART where customer_id ="+customer_id;
    }

    public static String booked_insert(ShoppingCartItem sci, int status) {
        return "insert into BOOKED values ("+sci.getCustomer_id()+",'"+
                    sci.getDate_start()+"','"+sci.getDate_stop()+"',"+
                    sci.getRoom_type_id()+","+sci.getNumtaken()+","+
                    sci.getTotal_price()+","+status+")";
    }

    // Room_date update
    public static String room_date_update(ShoppingCartItem sci){
        return "update ROOM_DATE set numtaken = numtaken + "+sci.getNumtaken()+
                ", numavail = numavail - "+sci.getNumtaken()+
                " where room_type_id = "+sci.getRoom_type_id()+
                " and single_day_date between '"+sci.getDate_start()+
                "' and '"+sci.getDate_stop()+"'";
    }

    // Search Conditions
    public static String query_room(SearchCondition cond) throws IllegalArgumentException {
        // requires a valid condition on a room table
        if (cond == null) throw new IllegalArgumentException();
        // (a) at least a condition must be given
        if (cond.isDate_startNull() && cond.isDate_stopNull() && cond.isNumroomsNull() 
                && cond.isPrice_maxNull() && cond.isRoom_typeNull()) 
            throw new IllegalArgumentException("at least date_start and date_stop and numrooms should be defined");
        // (b) the range of dato must be given
        if (cond.isDate_startNull() || cond.isDate_stopNull())
            throw new IllegalArgumentException("date_start and date_stop should be defined");
        // (c) the number of rooms must be given
        if (cond.getNumrooms() < 1) throw new IllegalArgumentException("numrooms should be defined");

        // First build the hotels filter. This is the minimal set of
        // room_type_id used to query the room_date table looking for
        // contiguous ranges of hotel dates. It is much faster to filter by
        // hotel prior to searching for dates.
        final StringBuilder hotelFilterBuilder =
            new StringBuilder(
                    "select h.hotel_id, rde.room_type, rde.room_type_id ")
            .append("from hotel h, room_type rde ")
            .append("where h.hotel_id = rde.hotel_id");
        boolean add_and = true;
        if (!cond.isCityNull()) {
            if (add_and) hotelFilterBuilder.append(" and ");
            hotelFilterBuilder.append("h.city = '")
                .append(cond.getCity()).append("'");
            add_and = true;
        }
        if (!cond.isCountryNull()) {
            if (add_and) hotelFilterBuilder.append(" and ");
            hotelFilterBuilder.append("h.country = '")
                .append(cond.getCountry()).append("'");
            add_and = true;
        }
        if (!cond.isRoom_typeNull()) {
            if (add_and) hotelFilterBuilder.append(" and ");
            hotelFilterBuilder.append("rde.room_type = '")
                .append(cond.getRoom_type()).append("'");
            add_and = true;
        }
        if (!cond.isDistance_to_centerNull()) {
            if (add_and) hotelFilterBuilder.append(" and ");
            hotelFilterBuilder.append("h.distance_to_center < ")
                .append(cond.getDistance_to_center());
            add_and = true;
        }

        // Query contiguous ranges of room_date.single_day_date using the
        // filtered hotel room_type_id query.
        final StringBuilder roomQueryBuilder =
            new StringBuilder(
                    "select q.hotel_id, q.room_type, q.room_type_id, ")
            .append("min(rda.single_day_date) as date_start, ")
            .append("max(rda.single_day_date) as date_stop, ")
            .append(cond.getNumrooms()).append(" as numrooms, ")
            .append("sum(rda.price) as total_price " +
                    "from room_date rda, ")
            .append("(").append(hotelFilterBuilder.toString()).append(") q ")
            .append("where ");
        add_and = false;
        if (!cond.isPrice_maxNull()) {
            if (add_and) roomQueryBuilder.append(" and ");
            roomQueryBuilder.append("rda.price < ").append(cond.getPrice_max());
            add_and = true;
        }
        if (cond.isNumroomsNull()) {
            if (add_and) roomQueryBuilder.append(" and ");
            roomQueryBuilder.append("rda.numavail > 0");
            add_and = true;
        } else {
            if (add_and) roomQueryBuilder.append(" and ");
            roomQueryBuilder.append("rda.numavail >= ").append(cond.getNumrooms());
            add_and = true;
        }
        // Get the day range [start, stop) and match the hotel filter on
        // room_type_id.
        final DateTime start = new DateTime(cond.getDate_start());
        final DateTime stop = new DateTime(cond.getDate_stop());
        final int daysBetween = Days.daysBetween(start, stop).getDays();
        if (add_and) roomQueryBuilder.append(" and ");
        roomQueryBuilder
            .append("rda.single_day_date >= '").append(cond.getDate_start()).append("'")
            .append(" and ")
            .append("rda.single_day_date < '").append(cond.getDate_stop()).append("'")
            .append(" and ")
            .append("rda.room_type_id = q.room_type_id ")
            .append("group by (q.hotel_id, q.room_type, q.room_type_id) ")
            .append("having count(single_day_date) = ")
            .append(daysBetween);
        final String query = roomQueryBuilder.toString();

        return roomQueryBuilder.toString();
    }

    public static String query_hotel(SearchCondition cond)
            throws IllegalArgumentException {
        if (cond == null) throw new IllegalArgumentException();
        boolean add_and = false;
        boolean add_where = false;
        String query;
        if (cond.isRoom_typeNull() && cond.isPrice_maxNull() && cond.isDate_stopNull() &&
            cond.isDate_startNull() && cond.isNumroomsNull()) {
            query = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center "+
                    "from hotel h";
            add_where = true;
        }
        else if (!cond.isRoom_typeNull()) {
            if (cond.isDate_startNull() && cond.isDate_stopNull() && cond.isPrice_maxNull()) {
                query = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center "+
                        "from hotel h, room_type rde "+
                        "where h.hotel_id = rde.hotel_id and rde.room_type = '"+cond.getRoom_type()+"'";
                add_and = true;
                add_where = false;
            } else {
                query = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center "+
                        "from hotel h, room_type rde, room_date rda "+
                        "where  h.hotel_id = rde.hotel_id and rde.room_type = '"+cond.getRoom_type()+"'"+
                        " and rde.room_type_id = rda.room_type_id";
                add_and = true;
                add_where = false;
               if (!cond.isPrice_maxNull()) {
                    if (add_and) query += " and ";
                    query+= "rda.price < "+cond.getPrice_max();
                    add_and = true;
                }
                // date interval of the form [date_start, date_stop[
                if (!cond.isDate_startNull()) {
                    if (add_and) query += " and ";
                    query+= "rda.single_day_date >= '"+cond.getDate_start()+"'";
                    add_and = true;
                }
                if (!cond.isDate_stopNull()) {
                    if (add_and) query += " and ";
                    query+= "rda.single_day_date < '"+cond.getDate_stop()+"'";
                    add_and = true;
                }
                if (cond.isNumroomsNull()) {
                    if (add_and) query += " and ";
                    query += "rda.numavail > 0";
                    add_and = true;
                } else {
                    if (add_and) query += " and ";
                    query += "rda.numavail >= "+cond.getNumrooms();
                    add_and = true;
                }
            }
        } else {
            query = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center "+
                    "from hotel h, room_type rde, room_date rda "+
                    "where h.hotel_id = rde.hotel_id and rde.room_type_id = rda.room_type_id";
            add_where = false;
            add_and = true;
            if (!cond.isPrice_maxNull()) {
                if (add_and) query += " and ";
                query+= "rda.price < "+cond.getPrice_max();
                add_and = true;
            }
            // date interval of the form [date_start, date_stop[
            if (!cond.isDate_startNull()) {
                if (add_and) query += " and ";
                query+= "rda.single_day_date >= '"+cond.getDate_start()+"'";
                add_and = true;
            }
            if (!cond.isDate_stopNull()) {
                if (add_and) query += " and ";
                query+= "rda.single_day_date < '"+cond.getDate_stop()+"'";
                add_and = true;
            }
            if (cond.isNumroomsNull()) {
                if (add_and) query += " and ";
                query += "rda.numavail > 0";
                add_and = true;
            } else {
                if (add_and) query += " and ";
                query += "rda.numavail >= "+cond.getNumrooms();
                add_and = true;
            }
        }
        if (!cond.isCityNull()) {
            if (add_where) query += " where ";
            if (add_and) query += " and ";
            query+= "h.city = '"+cond.getCity()+"'";
            add_and = true;
            add_where = false;
        }
        if (!cond.isCountryNull()) {
            if (add_where) query += " where ";
            if (add_and) query += " and ";
            query+= "h.country = '"+cond.getCountry()+"'";
            add_and = true;
            add_where = false;
        }
        if (!cond.isDistance_to_centerNull()) {
            if (add_and) query += " and ";
            query+= "h.distance_to_center < "+cond.getDistance_to_center();
            add_and = true;
        }
        return query;
    }

    // Test statements
    public static String shopping_cart_deleteAll() {
        return "delete from SHOPPING_CART";
    }
    public static String booked_deleteAll() {
        return "delete from BOOKED";
    }
}
