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

import java.util.HashMap;

class UserCommand {
    private HashMap<String,String> m;

    public UserCommand() {
        m = new HashMap<String,String>();
    }

    public void addCommand(String att, String val)
            throws IllegalArgumentException {
        if (!att.equals("command")) throw new IllegalArgumentException("First attribute should be command");
        if (!val.equals("login")&&!val.equals("logout")&&!val.equals("customers_loggedin")
                &&!val.equals("search_hotels")&&!val.equals("get_hotels")&&!val.equals("search_rooms")
                &&!val.equals("get_rooms")&&!val.equals("add_to_shopping_cart")&&!val.equals("checkout"))
            throw new IllegalArgumentException("command should be login, logout, customers_loggedin," +
                    "search_hotels, get_hotels, search_rooms, get_rooms, add_to_shopping_cart, checkout");
        m.put(att,val);
    }

    public void addParameters(String att, String val)
            throws IllegalArgumentException {
        m.put(att,val);
   }

   public String getCommand() {
       return this.m.get("command");
   }

   public boolean isGet() {
       boolean isGet = (this.m.get("command").equals("get_rooms")) || (this.m.get("command").equals("get_hotels"));
       return isGet;
   }

   public String getParameter(String param)
            throws IllegalArgumentException {
       String rep = this.m.get(param);
       if (rep == null) throw new IllegalArgumentException("Illegal parameter: expected "+param);
       return rep;
       
   }

   public SearchCondition build_search_condition() {
        SearchCondition cond = new SearchCondition();
        if (this.m.get("city") != null) {
            cond.setCity((String)m.get("city"));
        }
        if (this.m.get("country") != null) {
            cond.setCountry((String)m.get("country"));
        }
        if (this.m.get("room_type") != null) {
            cond.setRoom_type((String) m.get("room_type"));
        }
        if (this.m.get("price_max") != null) {
            cond.setPrice_max(Integer.valueOf((String)m.get("price_max")));
        }
        if (this.m.get("numrooms") != null) {
            cond.setNumrooms(Integer.valueOf((String)m.get("numrooms")));
        }
        if (this.m.get("distance_to_center") != null) {
            cond.setDistance_to_center(Float.valueOf((String)m.get("distance_to_center")).intValue());
        }
        if (this.m.get("date_start")!= null) {
            cond.setDate_start(java.sql.Date.valueOf((String)m.get("date_start")));
        }
        if (this.m.get("date_stop")!= null) {
            cond.setDate_stop(java.sql.Date.valueOf((String)m.get("date_stop")));
        }
        return cond;
    }

    public ShoppingCartItem build_shopping_cart_item() {
        ShoppingCartItem sci = new ShoppingCartItem();
        if (this.m.get("customer_id") != null) {
            sci.setCustomer_id(Integer.valueOf(m.get("customer_id")));
        }
        if (this.m.get("date_start") != null) {
            sci.setDate_start(java.sql.Date.valueOf(m.get("date_start")));
        }
        if (this.m.get("date_stop") != null) {
            sci.setDate_stop(java.sql.Date.valueOf(m.get("date_stop")));
        }
        if (this.m.get("numtaken") != null) {
            sci.setNumtaken(Integer.valueOf(m.get("numtaken")));
        }
        if (this.m.get("room_type_id") != null) {
            sci.setRoom_type_id(Integer.valueOf(m.get("room_type_id")));
        }
        if (this.m.get("total_price") != null) {
            sci.setTotal_price(Float.valueOf(m.get("total_price")));
        }
        return sci;
    }


}
