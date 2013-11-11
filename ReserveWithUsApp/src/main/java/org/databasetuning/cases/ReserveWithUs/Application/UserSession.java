/**
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

import java.sql.*;
import java.net.*;
import java.io.*;

class UserSession implements Runnable {
    private AppServerContext as;
    private DB2Session session;
    Socket s;
    private QueryHandle q;

    public UserSession(AppServerContext as, Socket s) {
        this.as = as;
        this.session = new DB2Session(this.as);
        this.s = s;
        this.q = null;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            try {
                // Set up socket as stream
                out = new PrintWriter(s.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {
                // Request-response loop
                UserCommand command;
                String response;
                while (true) {
                    command = parse_request(in);
                    if (command == null) break;
                    if (q != null && !command.isGet()) this.session.close(q.getCon());
                    response = execute_command(command);
                    out.println(response);
                }
            } catch (IOException e) {
                // Request is not OK
                out.println("IOException: Request is incorrect");
            } catch (SQLException e) {
                out.println("SQL Exception: "+e.getMessage());
            } catch (IllegalArgumentException e) {
                out.println("Illegal Argument Exception: "+e.getMessage());
            } catch (InterruptedException e) {
                out.println("Checkout Wait Interruped: "+e.getMessage());
            }
        }
        // cleanup
        finally {
            try {
                if (q!=null) this.session.close(q.getCon());
                out.println("bye.");
                out.close();
                in.close();
                s.close();
             }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public UserCommand parse_request(BufferedReader in) throws IOException {
        String inputLine;
        UserCommand command;
        if ((inputLine = in.readLine()) != null) {
            if (inputLine.length() == 0) command = null;
            else {
                String[] attvals  = inputLine.split("[&]");
                command = new UserCommand();
                boolean isCommand = true; // first att-val should be a command, rest is parameters
                for (String avs : attvals){
                    if (avs.length() == 0) command = null;
                    else {
                        String[] attval = avs.split("[=]");
                        if (isCommand) {
                            command.addCommand(attval[0], attval[1]);
                            isCommand = false;
                        } else {
                            command.addParameters(attval[0],attval[1]);
                        }
                    }
                }
            }
        } else {
            command = null;
        }
        return command;
    }

    public String execute_command(UserCommand command)
            throws SQLException, IllegalArgumentException, InterruptedException {
        String ret = "no command executed";
        String val_command = command.getCommand();
        if (val_command == null) throw new IllegalArgumentException();
        if (val_command.equals("login")) {
            int val_customer_id = (Integer.valueOf(command.getParameter("customer_id"))).intValue();
            login(val_customer_id);
            ret = "customer "+val_customer_id+" logged in.";
        } else if (val_command.equals("logout")) {
            int val_customer_id = (Integer.valueOf(command.getParameter("customer_id"))).intValue();
            logout(val_customer_id);
            ret = "customer "+val_customer_id+" logged out.";
        } else if (val_command.equals("customers_loggedin")) {
            int nb = customers_loggedin();
            ret = nb+" customers logged in.";
        } else if (val_command.equals("search_hotels")) {
            SearchCondition c = command.build_search_condition();
            this.q = search_hotels(c);
            ret = "query handle initialized for get_hotels";
        } else if (val_command.equals("get_hotels")) {
            if (q == null) return "Query handle not initialized.";
            Hotel [] hotel_array = get_hotels(this.q);
            ret = "";
            for (Hotel h : hotel_array) {
                if (h!=null) ret += h.toString()+"\n";
            }
            if (!q.hasMoreElements()) {
                ret += "done.";
                q = null;
            }
        } else if (val_command.equals("search_rooms")) {
            SearchCondition c = command.build_search_condition();
            this.q = search_rooms(c);
            ret = "query handle initialized for get_rooms.";
        } else if (val_command.equals("get_rooms")) {
            if (q == null) return "Query handle not initialized.";
            Room [] room_array = get_rooms(this.q);
            ret = "";
            for (Room r : room_array) {
                if (r!=null) ret += r.toString()+"\n";
            }
            if (!q.hasMoreElements()) {
                ret += "done.";
                q = null;
            }
        } else if (val_command.equals("add_to_shopping_cart")) {
            ShoppingCartItem sci = command.build_shopping_cart_item();
            add_to_shopping_cart(sci);
            ret = "items added to shopping cart";
        } else if (val_command.equals("checkout")) {
            int val_customer_id = (Integer.valueOf(command.getParameter("customer_id"))).intValue();
            checkout(val_customer_id);
            ret = "checkout completed for customer "+val_customer_id;
        }
        return ret;
    }

    public void login(int customer_id) throws SQLException {
        Connection con = null;
        try {
            con = this.session.open();
            // Check if user is already logged in
            String sql_stmt = DB2SQLStatements.login_check(customer_id);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql_stmt);
            rs.next();
            int exists = rs.getInt(1);
            if (exists != 0) {
                System.out.println("Customer "+customer_id+" already logged in");
            } else {
                // start Transaction
                con.setAutoCommit(false);
                // get value from login_counter
                sql_stmt = DB2SQLStatements.login_select_logcounter();
                rs = stmt.executeQuery(sql_stmt);
                rs.next();
                int value = rs.getInt("value");
                // insert customer_id, login_value into login
                sql_stmt = DB2SQLStatements.login_insert(customer_id, value);
                stmt.executeUpdate(sql_stmt);
                // increment value from login_counter
                value += 1;
                // insert new value into login_counter
                sql_stmt = DB2SQLStatements.login_increment_logcounter(value);
                stmt.executeUpdate(sql_stmt);
                // commit
                con.commit();
            }
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }

    }

    public void logout(int customer_id) throws SQLException {
        Connection con = null;
        try {
            con = this.session.open();
            // Delete customer_id entry from login table
            String sql_stmt = DB2SQLStatements.login_remove(customer_id);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql_stmt);
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }
    }

    public int customers_loggedin() throws SQLException {
        Connection con = null;
        try {
            con = this.session.open();
            String sql_stmt = DB2SQLStatements.login_count();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql_stmt);
            rs.next();
            int nb = rs.getInt(1);
            return nb;
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }
    }

    public QueryHandle search_hotels(SearchCondition s) throws SQLException {
        Connection con = null;
        try {
            con = this.session.open();
            String sql_stmt = DB2SQLStatements.query_hotel(s);
            System.out.println(sql_stmt);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                          ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql_stmt);
            QueryHandle ret = new QueryHandle(con,rs);
            con = null;
            return  ret;
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }
    }

    public QueryHandle search_rooms(SearchCondition s) throws SQLException {
        Connection con = null;
        try {
            con = this.session.open();
            String sql_stmt = DB2SQLStatements.query_room(s);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                          ResultSet.CONCUR_UPDATABLE);
            System.out.println(sql_stmt);
            ResultSet rs = stmt.executeQuery(sql_stmt);
            QueryHandle ret = new QueryHandle(con,rs);
            con = null;
            return ret;
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }
    }

    public Hotel[] get_hotels(QueryHandle u) throws SQLException {
        ResultSet rs = u.getRes();
        int size = u.getRow_batch_size();
        Hotel [] hotel_array = new Hotel[size];
        for (int i = 0; i < size && rs.next(); i++) {
            hotel_array[i] = new Hotel(rs.getInt("hotel_id"), rs.getString("name"), rs.getString("street"),
                    rs.getString("city"), rs.getInt("zip_code"), rs.getString("state"), rs.getString("country"),
                    rs.getInt("rating"), rs.getFloat("distance_to_center"));
        }
        u.setRes(rs);
        if (rs.isClosed()) {
            u.setHasMoreElements(false);
        } else if (rs.isAfterLast()){
            u.setHasMoreElements(false);
        }
        return hotel_array;
    }

    public Room[] get_rooms(QueryHandle u) throws SQLException {
        ResultSet rs = u.getRes();
        int size = u.getRow_batch_size();
        Room [] room_array = new Room[size];
        for (int i = 0; i < size && rs.next(); i++) {
            room_array[i] = new Room(rs.getInt("room_type_id"), rs.getInt("hotel_id"), rs.getString("room_type"),
                    rs.getDate("date_start"), rs.getDate("date_stop"), rs.getInt("numrooms"), rs.getFloat("total_price"));
        }
        u.setRes(rs);
        if (rs.isAfterLast()) u.setHasMoreElements(false);
        return room_array;
    }

    public void add_to_shopping_cart(ShoppingCartItem sci) throws IllegalArgumentException, SQLException {
        Connection con = null;
        try {
            // check customer is logged in
            con = this.session.open();
            String sql_stmt = DB2SQLStatements.login_check(sci.getCustomer_id());
            Statement  stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql_stmt);
            rs.next();
            int isLoggedIn = rs.getInt(1);
            if (isLoggedIn == 0) throw new IllegalArgumentException("Customer not logged in");

            // add item to shopping cart
            ShoppingCart sc = new ShoppingCart(this.as, sci.getCustomer_id());
            sc.add_item(sci);
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }
    }

    public void checkout(int customer_id) throws SQLException, InterruptedException {
        // check customer is logged in
        Connection con = null;
        try {
            con = this.session.open();
            String sql_stmt = DB2SQLStatements.login_check(customer_id);
            Statement  stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql_stmt);
            rs.next();
            int isLoggedIn = rs.getInt(1);
            if (isLoggedIn == 0) throw new IllegalArgumentException("Customer not logged in");

            // checkout
            ShoppingCart sc = new ShoppingCart(this.as, customer_id);
            sc.checkout();
        } finally {
            if (null != con) {
                this.session.close(con);
            }
        }
    }


}
