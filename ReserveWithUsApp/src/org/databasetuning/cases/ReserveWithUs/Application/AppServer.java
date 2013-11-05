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
 * CONTAINS
 * Runtime Modules of
 * IBM Data Server Driver for JDBC and SQLJ V3.57
 * (c) Copyright IBM Corporation 2009
 * All Rights Reserved
 *
 */ 

package org.databasetuning.cases.ReserveWithUs.Application;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.Properties;

public class AppServer {

   public static void main(String[] args) {
       // set up server
       AppServerContext as;
       ServerSocket serverSocket;
       ExecutorService threadPool;
       boolean listening = true;
       int port = 0;
       
        try {
            as = new AppServerContext();
            Properties prop = new Properties();
            InputStream is = AppServer.class.getResourceAsStream("/ReserveWithUsApp.properties");
            prop.load(is);
            as.setDb(prop.getProperty("db_url"));
            as.setUserName(prop.getProperty("user"));
            as.setPassword(prop.getProperty("password"));

            port = Integer.valueOf(prop.getProperty("port")).intValue();
            serverSocket = new ServerSocket(port);

            threadPool = Executors.newFixedThreadPool(Integer.valueOf(prop.getProperty("pool_size")).intValue());

            // Processing loop
            while (listening) {
                // starts a user session to handle the connection
                threadPool.execute(new UserSession(as, serverSocket.accept()));
            }

            serverSocket.close();

        } catch (IOException e) {
            System.out.println("Could not listen on port: "+port);
            System.exit(-1);
        } catch (NullPointerException e){
            System.out.println("Properties file has not been set. You should follow the following steps: (1) Copy ReserveWithUsApp/properties/ReserveWithUsApp.properties"+
                    " to ReserveWithUsApp/dist/ . (2) set the properties to reflect your system setup. (3) Update the ReserveWithUsApp.jar file"+
                    " jar uf ReserveWithUsApp.jar ReserveWithUsApp.properties");
            System.exit(-1);
        }


   }


}
