/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.databasetuning.cases.ReserveWithUs.Application;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author philippe
 */
public class DB2SQLStatementsTest {

    public DB2SQLStatementsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test (expected=IllegalArgumentException.class)
    public void testQuery_hotel_cond_null() {
        System.out.println("query_hotel: null argument");
        SearchCondition cond = null;
        String expResult = "";
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test 
    public void testQuery_hotel_all_null() {
        System.out.println("query_hotel: cond argument with null conditions");
        SearchCondition cond = new SearchCondition();
        String expResult = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center from hotel h";
        System.out.println(expResult);
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
    }

    @Test
    public void testQuery_hotel_by_country() {
        System.out.println("query_hotel: country condition");
        SearchCondition cond = new SearchCondition();
        cond.setCountry("country4");
        String expResult = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center from hotel h where h.country = 'country4'";
        System.out.println(expResult);
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
    }

    @Test
    public void testQuery_hotel_by_country_and_city() {
        System.out.println("query_hotel: country and city");
        SearchCondition cond = new SearchCondition();
        cond.setCountry("country4");
        cond.setCity("city2");
        String expResult = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center from hotel h where h.city = 'city2' and h.country = 'country4'";
        System.out.println(expResult);
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
    }

    @Test
    public void testQuery_hotel_by_country_and_room_type() {
        System.out.println("query_hotel: country and room_type");
        SearchCondition cond = new SearchCondition();
        cond.setCountry("country4");
        cond.setRoom_type("type8");
        String expResult = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center from hotel h, room_type rde where h.hotel_id = rde.hotel_id and rde.room_type = 'type8' and h.country = 'country4'";
        System.out.println(expResult);
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
    }


    @Test
    public void testQuery_hotel_by_country_and_room_type_and_date_start() {
        System.out.println("query_hotel: country and room_type and date_start");
        SearchCondition cond = new SearchCondition();
        cond.setCountry("country4");
        cond.setRoom_type("type8");
        cond.setDate_start(java.sql.Date.valueOf("2010-06-19"));
        String expResult = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center "+
                        "from hotel h, room_type rde, room_date rda "+
                        "where  h.hotel_id = rde.hotel_id and rde.room_type = 'type8'"+
                        " and rde.room_type_id = rda.room_type_id and rda.single_day_date >= '2010-06-19' and rda.numavail > 0 and h.country = 'country4'";
        System.out.println(expResult);
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
    }

    @Test
    public void testQuery_hotel_by_country_and_date_start() {
        System.out.println("query_hotel: country and date_start");
        SearchCondition cond = new SearchCondition();
        cond.setCountry("country4");
        cond.setDate_start(java.sql.Date.valueOf("2010-06-19"));
        String expResult = "select h.hotel_id, h.name, h.street, h.city, h.zip_code, h.state, h.country, h.rating, h.distance_to_center "+
                        "from hotel h, room_type rde, room_date rda "+
                        "where h.hotel_id = rde.hotel_id"+
                        " and rde.room_type_id = rda.room_type_id and rda.single_day_date >= '2010-06-19' and rda.numavail > 0 and h.country = 'country4'";

        System.out.println(expResult);
        String result = DB2SQLStatements.query_hotel(cond);
        assertEquals(expResult, result);
    }

    @Test
    public void testQuery_room_by_date_start_and_date_stop() {
        System.out.println("query_room: date_start and date_stop");
        SearchCondition cond = new SearchCondition();
        cond.setNumrooms(1);
        cond.setDate_start(java.sql.Date.valueOf("2010-05-04"));
        cond.setDate_stop(java.sql.Date.valueOf("2010-05-07"));
        String expResult = "select h.hotel_id, rde.room_type, q.room_type_id, q.date_start, q.date_stop, " +
                                  "q.numrooms, q.total_price "+
                          "from hotel h, room_type rde, ("+
                                "select rda.room_type_id, min(rda.single_day_date) as date_start,"+
                                    "max(rda.single_day_date) as date_stop, "+cond.getNumrooms()+" as numrooms, sum(rda.price) as total_price "+
                                "from room_date rda where "+
                                    "rda.numavail >= "+cond.getNumrooms()+" and "+
                                    "rda.single_day_date >= '2010-05-04' and "+
                                    "rda.single_day_date < '2010-05-07' "+
                                "group by rda.room_type_id having count(*) = (days('2010-05-07') - days('2010-05-04'))"+
                                ") q "+
                                "where h.hotel_id = rde.hotel_id and q.room_type_id = rde.room_type_id";
        String result = DB2SQLStatements.query_room(cond);
        assertEquals(expResult, result);
    }

   @Test
    public void testQuery_room_by_country_date_start_and_date_stop() {
        System.out.println("query_room: country, date_start and date_stop");
        SearchCondition cond = new SearchCondition();
        cond.setCountry("country3");
        cond.setNumrooms(1);
        cond.setDate_start(java.sql.Date.valueOf("2010-04-30"));
        cond.setDate_stop(java.sql.Date.valueOf("2010-05-02"));
        String expResult = "select h.hotel_id, rde.room_type, q.room_type_id, q.date_start, q.date_stop, " +
                                  "q.numrooms, q.total_price "+
                          "from hotel h, room_type rde, ("+
                                "select rda.room_type_id, min(rda.single_day_date) as date_start,"+
                                    "max(rda.single_day_date) as date_stop, "+cond.getNumrooms()+" as numrooms, sum(rda.price) as total_price "+
                                "from room_date rda where "+
                                    "rda.numavail >= "+cond.getNumrooms()+" and "+
                                    "rda.single_day_date >= '2010-04-30' and "+
                                    "rda.single_day_date < '2010-05-02' "+
                                "group by rda.room_type_id having count(*) = (days('2010-05-02') - days('2010-04-30'))"+
                                ") q "+
                                "where h.hotel_id = rde.hotel_id and q.room_type_id = rde.room_type_id and h.country = 'country3'";
        System.out.println(expResult);
        String result = DB2SQLStatements.query_room(cond);
        assertEquals(expResult, result);
    }



}