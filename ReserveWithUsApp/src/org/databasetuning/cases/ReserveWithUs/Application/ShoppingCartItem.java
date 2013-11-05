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


import java.util.Date;

/**
 *
 * @author philippe
 */
class ShoppingCartItem {
    private int customer_id = -1;
    private Date date_start = null;
    private Date date_stop = null;
    private int room_type_id = -1;
    private int numtaken = -1;
    private float total_price = -1;


    public ShoppingCartItem(int customer_id, Date date_start, Date date_stop, int room_type_id, int numtaken, float total_price) {
        this.customer_id = customer_id;
        this.date_start = date_start;
        this.date_stop = date_stop;
        this.room_type_id = room_type_id;
        this.numtaken = numtaken;
        this.total_price = total_price;
    }

    public ShoppingCartItem(){}

    public boolean isFullyDefined() {
        return (customer_id != -1) && (date_start != null) &&
               (date_stop != null) && (room_type_id != -1) && (numtaken != -1) &&
               (total_price != -1);
    }

    public boolean isKeyDefined() {
        return (customer_id != -1) && (date_start != null) &&
               (date_stop != null) && (room_type_id != -1);
    }

    public String toString(){
        return this.customer_id + ", "+ this.date_start + ", " + this.date_stop + ", " +
                this.room_type_id+ "," + this.numtaken + "," + this.total_price;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public Date getDate_start() {
        return date_start;
    }

    public Date getDate_stop() {
        return date_stop;
    }

    public int getNumtaken() {
        return numtaken;
    }

    public int getRoom_type_id() {
        return room_type_id;
    }

    public float getTotal_price() {
        return total_price;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public void setDate_stop(Date date_stop) {
        this.date_stop = date_stop;
    }

    public void setNumtaken(int numtaken) {
        this.numtaken = numtaken;
    }

    public void setRoom_type_id(int room_type_id) {
        this.room_type_id = room_type_id;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

}
