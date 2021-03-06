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

import java.util.Date;

public class Room {
    private int room_type_id;
    private int hotel_id;
    private String room_type;
    private Date date_start;
    private Date date_stop;
    private int numrooms;
    private float total_price;

    // Constructors
    public Room() {
    }

    public Room(int room_type_id, int hotel_id, String room_type, Date date_start, Date date_stop, int numrooms, float total_price) {
        this.room_type_id = room_type_id;
        this.hotel_id = hotel_id;
        this.room_type = room_type;
        this.date_start = date_start;
        this.date_stop = date_stop;
        this.numrooms = numrooms;
        this.total_price = total_price;
    }

    @Override
    public String toString() {
        return this.room_type_id+"|"+this.hotel_id+"|"+this.room_type_id+"|"+this.date_start+
                "|"+this.date_stop+"|"+this.numrooms+"|"+this.total_price;
    }



    // Getters
    public Date getDate_start() {
        return date_start;
    }

    public Date getDate_stop() {
        return date_stop;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public int getNumrooms() {
        return numrooms;
    }

    public String getRoom_type() {
        return room_type;
    }

    public int getRoom_type_id() {
        return room_type_id;
    }

    public float getTotal_price() {
        return total_price;
    }

    // Setters
    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public void setDate_stop(Date date_stop) {
        this.date_stop = date_stop;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public void setNumrooms(int numrooms) {
        this.numrooms = numrooms;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public void setRoom_type_id(int room_type_id) {
        this.room_type_id = room_type_id;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

    

}
