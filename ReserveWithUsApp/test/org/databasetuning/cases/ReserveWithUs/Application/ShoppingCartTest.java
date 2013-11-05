/**
 * Copyright (c) 2010, Ph.Bonnet and D.Shasha
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the package nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.databasetuning.cases.ReserveWithUs.Application;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.*;


/**
 *
 * @author philippe
 */
public class ShoppingCartTest {
    private AppServerContext as;

    public ShoppingCartTest() {
        this.as = new AppServerContext();
        // TODO - Change property/configure file
//        _as.setDB("jdbc:db2://ec2-174-129-69-159.compute-1.amazonaws.com:5001/tuning");
        this.as.setDb("jdbc:db2://localhost:2121/tuning");
        this.as.setUserName("db2inst1");
        this.as.setPassword("thebest1");
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
        try{
            DB2Session session = new DB2Session(this.as);
            Connection con = session.open();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(DB2SQLStatements.shopping_cart_deleteAll());
            stmt.executeUpdate(DB2SQLStatements.booked_deleteAll());
            con.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
       
    }

    @Test
    public void test_singleton() throws Exception {
        System.out.println("singleton");
        ShoppingCartItem s = new ShoppingCartItem(1, Date.valueOf("2010-04-21"), Date.valueOf("2010-04-23"), 1, 1, 150);
        System.out.println("item created: "+s);
        ShoppingCart instance = new ShoppingCart(this.as, 1);
        try {
            instance.add_item(s);
        }
        catch (Error e) {
            e.printStackTrace();
        }
        
        ShoppingCartItem[] ss = new ShoppingCartItem[1];
        try {
            ss = instance.get_all();
        }
        catch (Error e) {
            e.printStackTrace();
        }
        assertTrue("check a single item is retrieved", ss.length == 1);
        assertNotNull("check added/retrieved SC item #0 not null", ss[0]);
        System.out.println("item retrieved: "+ss[0]);
    }

    @Test (expected=SQLException.class)
    public void testAdd_duplicate() throws Exception {
        System.out.println("duplicate");
        ShoppingCartItem s = new ShoppingCartItem(2, Date.valueOf("2010-04-21"), Date.valueOf("2010-04-23"),1, 1, 150);
        ShoppingCart instance = new ShoppingCart(this.as, 2);
        System.out.print("1st insertion ...");
        instance.add_item(s);
        System.out.println("done.");
        System.out.print("2nd insertion ...");
        instance.add_item(s);
        System.out.println("done.");
    }

    @Test 
    public void testAdd_item_multiple() throws Exception {
        System.out.println("add_item_multiple");
        ShoppingCart instance = new ShoppingCart(this.as, 3);
        for (int i = 0; i < 10; i++){
            ShoppingCartItem s = new ShoppingCartItem(3, Date.valueOf("2010-05-21"), Date.valueOf("2010-05-24"), i, i, 150);
            instance.add_item(s);
        }
        System.out.println("10 insertions done.");
        ShoppingCartItem[] ss = new ShoppingCartItem[10];
        ss = instance.get_all();
        assertNotNull("check added/retrieved SC item not null", ss[0]);
        assertTrue(ss.length == 10);

    }

    @Test
    public void testAdd_item_multiple_sc_multiple() throws Exception {
        System.out.println("add_item_multiple_sc_multiple");
        ShoppingCart [] instance = new ShoppingCart[10];
        for (int j = 0; j < 10; j++){
            instance [j] = new ShoppingCart(this.as, 10+j);
        }
        System.out.println("10 shopping carts created.");

        for (int i = 0; i < 20; i++){
            int n = i % 10;
            int q = i / 10;
            ShoppingCartItem s = new ShoppingCartItem(10+n, Date.valueOf("2010-06-21"), Date.valueOf("2010-07-01"), q, q, 150);
            instance[n].add_item(s);
        }
        System.out.println("20 items created in 10 shopping carts.");

        ShoppingCartItem[] ss = new ShoppingCartItem[100];
        for (int j = 0; j < 10; j++){
            ss = instance [j].get_all();
            assertNotNull("check added/retrieved SC item not null", ss[0]);
            assertTrue(ss.length == 2);
        }

    }

    @Test
    public void testRemove_item() throws Exception {
        System.out.println("remove_item");
        ShoppingCart [] instance = new ShoppingCart[10];
        for (int j = 0; j < 10; j++){
            instance [j] = new ShoppingCart(this.as, 20+j);
        }
        System.out.println("10 shopping carts created.");

        for (int i = 0; i < 20; i++){
            int n = i % 10;
            int q = i / 10;
            ShoppingCartItem s = new ShoppingCartItem(20+n, Date.valueOf("2010-07-25"), Date.valueOf("2010-07-31"), q, q, 150);
            instance[n].add_item(s);
        }
        System.out.println("20 items created in 10 shopping carts.");

        for (int k = 0; k < 2; k++){
            ShoppingCartItem s = new ShoppingCartItem(20, Date.valueOf("2010-07-25"), Date.valueOf("2010-07-31"), k, k, 150);
            instance[0].remove_item(s);
        }

        for (int k = 0; k < 1; k++){
            ShoppingCartItem s = new ShoppingCartItem(21, Date.valueOf("2010-07-25"),Date.valueOf("2010-07-31"), k, k, 150);
            instance[1].remove_item(s);
        }
       
        ShoppingCartItem[] ss = new ShoppingCartItem[100];
        ss = instance[0].get_all();
        System.out.println("SC#1 should be empty.");
        assertNotNull("check added/retrieved SC item not null", ss);
        System.out.println("ss.length ="+ss.length);
        assertTrue(ss.length == 0);
        ss = instance[1].get_all();
        System.out.println("SC#1 should be half empty.");
        assertNotNull("check added/retrieved SC item not null", ss);
        assertTrue(ss.length == 1);
        System.out.println("SC#2--9 should be full.");
        for (int j = 2; j < 10; j++){
            ss = instance[j].get_all();
            assertNotNull("check added/retrieved SC item not null", ss);
            assertTrue(ss.length == 2);
        }
    }

    @Test
    public void testRemove_item_empty() throws Exception {
        System.out.println("remove_item_empty");
        ShoppingCart instance = new ShoppingCart(this.as, 30);
        ShoppingCartItem s = new ShoppingCartItem(30, Date.valueOf("2010-04-21"), Date.valueOf("2010-04-24"), 0, 0, 150);
        // item is NOT inserted in shopping cart instance
        instance.remove_item(s);
     }

    @Test
    public void testCheckout() throws Exception {
        System.out.println("checkout");

        System.out.println("check BOOKED state.");
        // select count(*) from booked -> X

        ShoppingCart [] instance = new ShoppingCart[10];
        for (int j = 0; j < 10; j++){
            instance [j] = new ShoppingCart(this.as, 40+j);
        }
        System.out.println("10 shopping carts created.");

        for (int i = 0; i < 20; i++){
            int n = i % 10;
            int q = i / 10;
            ShoppingCartItem s = new ShoppingCartItem(40+n, Date.valueOf("2010-08-21"), Date.valueOf("2010-05-21"),q, q, 150);
            instance[n].add_item(s);
        }
        System.out.println("20 items created in 10 shopping carts.");

        System.out.print("checking out ...");
        instance[0].checkout();
        System.out.println("done.");

        ShoppingCartItem[] ss = new ShoppingCartItem[100];
        ss = instance[0].get_all();
        System.out.println("SC#0 should be empty.");
        assertNotNull("check added/retrieved SC item not null", ss);
        assertTrue(ss.length == 0);

        System.out.println("SC#1--9 should be full.");
        for (int j = 1; j < 10; j++){
            ss = instance[j].get_all();
            assertNotNull("check added/retrieved SC item not null", ss);
            assertTrue(ss.length == 2);
        }

        System.out.println("check that BOOKED modified.");
        // select count(*) from booked -> X + 100

     }

}