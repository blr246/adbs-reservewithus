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

import java.sql.*;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author philippe
 */
class ShoppingCart {
    private int customer_id;
    private AppServerContext as;
    private DB2Session session;

    public ShoppingCart(AppServerContext as, int customer_id){
        this.as = as;
        this.customer_id = customer_id;
        this.session = new DB2Session(as);
    }

    public void add_item (ShoppingCartItem s) 
            throws IllegalArgumentException, SQLException {
        // Check arguments
        if (s.getCustomer_id() != this.customer_id)
            throw new IllegalArgumentException("Customer_id "+
                    s.getCustomer_id()+" should be "+this.customer_id);
        if (!s.isFullyDefined()) throw new IllegalArgumentException("Shopping cart item not fully defined");
        // Perform action via JDBC
        Connection con = this.session.open(); // might generate an Error - remains unchecked
        String sql_stmt = DB2SQLStatements.shopping_cart_insert(s);
        Statement stmt = con.createStatement();
        System.out.println(sql_stmt);
        stmt.executeUpdate(sql_stmt);
        this.session.close(con);
    }

    public void remove_item (ShoppingCartItem s) 
            throws IllegalArgumentException, SQLException {
        // Check arguments
        if (s.getCustomer_id() != this.customer_id)
            throw new IllegalArgumentException("Illegal Argument:"+
                    s.getCustomer_id()+" should be "+this.customer_id);
        // Perform action via JDBC
        Connection con = this.session.open(); // might generate an Error - remains unchecked
        String sql_stmt = DB2SQLStatements.shopping_cart_delete(s.getCustomer_id(),
                s.getDate_start(), s.getDate_stop(), s.getRoom_type_id());
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql_stmt);
        this.session.close(con);
    }

    public ShoppingCartItem[] get_all()
            throws SQLException {
        Connection con = this.session.open(); // might generate an Error - remains unchecked
        String sql_stmt = DB2SQLStatements.shopping_cart_getAll(this.customer_id);
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmt.executeQuery(sql_stmt);
        // Need cardinality of result set to initialize array
        res.last();
        int rowcount = res.getRow();
        res.beforeFirst();
        ShoppingCartItem[] resArray = new ShoppingCartItem[rowcount];
        // Materialize result set into array
        int i = 0; // index over rows
        while (res.next()) {
            // retrieve and materialize a row
            resArray[i] = new ShoppingCartItem();
            resArray[i].setCustomer_id(res.getInt("customer_id"));
            resArray[i].setDate_start(res.getDate("date_start"));
            resArray[i].setDate_stop(res.getDate("date_stop"));
            resArray[i].setRoom_type_id(res.getInt("room_type_id"));
            resArray[i].setNumtaken(res.getInt("numtaken"));
            resArray[i].setTotal_price(res.getInt("total_price"));
            i ++;
        }
        this.session.close(con);
        return resArray;
     }

    public synchronized void checkout() throws SQLException, InterruptedException {
        Connection con = this.session.open();
	con.setAutoCommit(false);
	String sql_stmt = DB2SQLStatements.new_checkout(this.customer_id);
	Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	ResultSet res = stmt.executeQuery(sql_stmt);
	con.commit();
	this.session.close(con);
    }

    public synchronized void oldCheckout() throws SQLException, InterruptedException {
        // First, get all items for this customer_id and create connection
        Connection con = this.session.open(); // might generate an Error - remains unchecked
        con.setAutoCommit(false);
        String sql_stmt = DB2SQLStatements.shopping_cart_getAll(this.customer_id);
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmt.executeQuery(sql_stmt);
        // Need cardinality of result set to initialize array
        res.last();
        int rowcount = res.getRow();
        res.beforeFirst();
        ShoppingCartItem[] resArray = new ShoppingCartItem[rowcount];
        // Materialize result set into array
        int i = 0; // index over rows
        while (res.next()) {
            // retrieve and materialize a row
            resArray[i] = new ShoppingCartItem();
            resArray[i].setCustomer_id(res.getInt("customer_id"));
            resArray[i].setDate_start(res.getDate("date_start"));
            resArray[i].setDate_stop(res.getDate("date_stop"));
            resArray[i].setRoom_type_id(res.getInt("room_type_id"));
            resArray[i].setNumtaken(res.getInt("numtaken"));
            resArray[i].setTotal_price(res.getInt("total_price"));
            i ++;
        }
        
        // Second, validate with user 
        // This wait cannot be removed - it simulates a user interaction
        this.wait(4000);

        // Third, update ROOM_DATE
        try {
            for (int j = 0; j < rowcount; j++) {
                sql_stmt = DB2SQLStatements.room_date_update(resArray[j]);
                stmt = con.createStatement();
                stmt.executeUpdate(sql_stmt);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }

        // Four, insert data into BOOKED

        for (int j=0; j < rowcount; j++) {
            System.out.println(j);
            sql_stmt = DB2SQLStatements.booked_insert(resArray[j], 2); // status is confirmed
            stmt = con.createStatement();
            stmt.executeUpdate(sql_stmt);
        }


        // Five, remove data from shopping cart
        sql_stmt = DB2SQLStatements.shopping_cart_deleteAll(this.customer_id);
        stmt = con.createStatement();
        stmt.executeUpdate(sql_stmt);
        con.commit();
        this.session.close(con);
        /*catch(InterruptedException e){
             _as.log(Level.ALL,
                "checkout failed while waiting during checkout "+
                _customer_id);
            throw new SQLException("Problem in checkout");
        }*/
    }


}
