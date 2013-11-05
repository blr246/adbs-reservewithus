/**
 * Copyright (c) 2010, Ph.Bonnet and D.Shasha
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this 
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors may 
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


public class Hotel {
    private int hotel_id;
    private String name;
    private String street;
    private String city;
    private int zip_code;
    private String state;
    private String country;
    private int rating;
    private float distance_to_center;

    public Hotel() {
    }

    public Hotel(int hotel_id, String name, String street, String city, int zip_code, String state, String country, int rating, float distance_to_center) {
        this.hotel_id = hotel_id;
        this.name = name;
        this.street = street;
        this.city = city;
        this.zip_code = zip_code;
        this.state = state;
        this.country = country;
        this.rating = rating;
        this.distance_to_center = distance_to_center;
    }

    @Override
    public String toString() {
        return this.hotel_id+"|"+this.name+"|"+this.street+"|"+this.city+"|"+this.zip_code+"|"+
                this.state+"|"+this.country+"|"+this.rating+"|"+this.distance_to_center;
    }



    // Getters
    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public float getDistance_to_center() {
        return distance_to_center;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }

    public int getZip_code() {
        return zip_code;
    }

    // Setters
    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDistance_to_center(float distance_to_center) {
        this.distance_to_center = distance_to_center;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZip_code(int zip_code) {
        this.zip_code = zip_code;
    }




}
