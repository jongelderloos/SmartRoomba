/*
 *  Tribble
 *
 *  Copyright (c) 2006 Tod E. Kurt, tod@todbot.com, ThingM
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


package com.jgelderloos.smartroomba.examples;

import com.jgelderloos.smartroomba.roombacomm.RoombaCommSerial;

import java.io.*;

/**
 *  Make Tribble noises

 * <p>
 *  Run it with something like: <pre>
 *   java roombacomm.Tribble /dev/cu.KeySerial1 [protocol]<br>
 *   Where: protocol (optional) is SCI or OI
 *  </pre>
 *
 */
public class Tribble {

    static RoombaCommSerial roombacomm;
    
    public static void main(String[] args) {
    	new Tribble(args);
    }
    
    Tribble(String[] args)
    {
        if( args.length == 0 ) {
            System.out.println("Tribble <serialportname> [protocol]\nWhere: protocol (optional) is SCI or OI");
            System.exit(0);
        }
        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        
        roombacomm = new RoombaCommSerial();
        for( int i=1; i < args.length; i++ ) {
        	if (args[i].equals("SCI") || (args[1].equals("OI"))) {
        		roombacomm.setProtocol(args[i]);
        	}
        }
         
        if( ! roombacomm.connect( portname ) ) {
            System.err.println("Couldn't connect to "+portname);
            System.exit(1);
        }
        
        System.err.println("Roomba startup");
        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(100);
        
        createTribblePurrSong();
        
        System.out.println("Press return to exit.");
        boolean done = false;
        while( !done ) { 
            
            purr();
            
            if( Math.random() < 0.1 )
                bark();
            
            roombacomm.pause(1500 + (int)(Math.random()*500) );           
            done = keyIsPressed();            
        }
        
        roombacomm.disconnect();
        System.exit(0);
    }
    
    public static void purr() {
        System.out.println("purr");
        roombacomm.playSong( 5 );
        for( int i=0; i<5; i++ ) {
            roombacomm.spinLeftAt( 75 );
            roombacomm.pause( 100 );
            roombacomm.spinRightAt( 75 );
            roombacomm.pause( 100 );
            roombacomm.stop();
        }
    }
    
    public static void createTribblePurrSong() {
        int song[] = 
            { 68,4, 67,4, 66,4, 65,4,
              64,4, 63,4, 62,4, 61,4,
              60,4, 59,4, 60,4, 61,4,
            };
        roombacomm.createSong( 5, song );
    }
    
    public static void bark() {
        System.out.println("bark");
        roombacomm.vacuum(true);
        roombacomm.playNote( 50, 5 );
        roombacomm.pause(150);
        roombacomm.vacuum(false);
    }
    
    
    /** check for keypress, return true if so */
    public static boolean keyIsPressed() {
        boolean press = false;
        try { 
            if( System.in.available() != 0 ) {
                System.out.println("key pressed");
                press = true;
            }
        } catch( IOException ioe ) { }
        return press;
    }
    
}
