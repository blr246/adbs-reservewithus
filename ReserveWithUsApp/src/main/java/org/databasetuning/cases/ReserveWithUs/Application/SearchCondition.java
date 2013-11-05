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

public class SearchCondition {
    private int price_max = -1;
    private String country = null;
    private String city = null;
    private String room_type = null;
    private Date date_start = null;
    private Date date_stop = null;
    private int numrooms = -1;
    private float distance_to_center = -1;


    // Constructors
    public SearchCondition(){
    }

    @Override
    public String toString() {
        return "price_max"+this.price_max+", country="+this.country+", city="+this.city+", room_type="+
                this.room_type+", date_start="+this.date_start+", date_stop="+this.date_stop+", numrooms="+
                this.numrooms+", distance_to_center="+this.distance_to_center;
    }



    // Getters
    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Date getDate_start() {
        return date_start;
    }

    public Date getDate_stop() {
        return date_stop;
    }

    public int getPrice_max() {
        return price_max;
    }

    public String getRoom_type() {
        return room_type;
    }

    public int getNumrooms() {
        return numrooms;
    }

    public float getDistance_to_center() {
        return distance_to_center;
    }



    // Setters

    public void setDistance_to_center(float distance_to_center) {
        this.distance_to_center = distance_to_center;
    }
    
    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public void setDate_stop(Date date_stop) {
        this.date_stop = date_stop;
    }

    public void setPrice_max(int price_max) {
        this.price_max = price_max;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public void setNumrooms(int numrooms) {
        this.numrooms = numrooms;
    }


    // isNull
    public boolean isCityNull() {
        return this.city == null;
    }

    public boolean isCountryNull() {
        return this.country == null;
    }

    public boolean isDate_startNull() {
        return this.date_start == null;
    }

    public boolean isDate_stopNull() {
        return this.date_stop == null;
    }

    public boolean isPrice_maxNull() {
        return this.price_max == -1;
    }

    public boolean isRoom_typeNull() {
        return this.room_type == null;
    }

    public boolean isNumroomsNull() {
        return this.numrooms == -1;
    }

    public boolean isDistance_to_centerNull() {
        return this.distance_to_center == -1;
    }



}
