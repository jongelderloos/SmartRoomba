/*
 *  TextHttpServer
 *
 *  Copyright (c) 2005 Tod E. Kurt, tod@todbot.com
 *  Copyright (c) 2018 Jon Gelderloos
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General
 *  Public License along with this library; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA  02111-1307  USA
 *
 */

package com.jgelderloos.smartroomba.net;

import java.net.*;
import java.io.*;

public class TextHttpServer {
    
    int port = 6767;
    /*
    String cmds[] =
    {
        "reset",     // zero args
        "stop",      // zero args
        "goforward", // one optional arg
        "gobackward", // one optional arg
        "spinleft",  // one optional arg
        "spinright", // one optional arg
        "beep",  // two args
    };
    */

    // the shutdown command received
    private boolean shutdown = false;
    
    public static void main(String[] args) {
        TextHttpServer server = new TextHttpServer();
        server.await();
    }
    
    
    public void await() {
        System.out.println("awaiting connections on port "+port+"...");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 1, null);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Loop waiting for a request
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                socket = serverSocket.accept(); // this blocks

                input = socket.getInputStream();
                output = socket.getOutputStream();

                StringBuffer request = parseRequest( input );
                String uristr = parseUri( request.toString() );
                System.out.println("uristr: "+uristr);

                URI uri = new URI( uristr );
                System.out.println("path:"+uri.getPath()+", query:"+uri.getQuery());
                
                // Close the socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                //System.exit(1);
            }
        }

    }



    public StringBuffer parseRequest(InputStream input) {
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = input.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j=0; j<i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.print(request.toString());
        
        return request;
    }

    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }

    
}
