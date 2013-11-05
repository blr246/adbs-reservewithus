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

public class QueryHandle {
    private Connection con = null;
    private ResultSet res = null;
    private int row_batch_size;
    private boolean hasMoreElements;

    public QueryHandle() {
    }
    
    protected QueryHandle(Connection con, ResultSet res) {
        this.row_batch_size = 10;
        this.con = con;
        this.res = res;
        this.hasMoreElements = true;
    }

    protected void closeCon() throws SQLException {
        if (con!=null) con.close();
    }

    protected Connection getCon() {
        return con;
    }

    protected ResultSet getRes() {
        return res;
    }

    public int getRow_batch_size() {
        return row_batch_size;
    }

    public boolean hasMoreElements() {
        return hasMoreElements;
    }

    public void setHasMoreElements(boolean hasMoreElements) {
        this.hasMoreElements = hasMoreElements;
    }

    protected void setCon(Connection con) {
        this.con = con;
    }

    protected void setRes(ResultSet res) {
        this.res = res;
    }

    public void setRow_batch_size(int row_batch_size) {
        this.row_batch_size = row_batch_size;
    }

    




}
