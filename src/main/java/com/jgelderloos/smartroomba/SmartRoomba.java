/*
 *  SmartRoomba - SmartRoomba
 *
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

package com.jgelderloos.smartroomba;

import com.jgelderloos.smartroomba.roomba.RoombaConstants.OpCodes;
import com.jgelderloos.smartroomba.roomba.RoombaInfo;
import com.jgelderloos.smartroomba.roomba.RoombaMapData;
import com.jgelderloos.smartroomba.roomba.RoombaUtilities;
import com.jgelderloos.smartroomba.roomba.SensorData;
import com.jgelderloos.smartroomba.roombacomm.RoombaComm;
import com.jgelderloos.smartroomba.roombacomm.RoombaCommSerial;
import com.jgelderloos.smartroomba.utilities.DataCSV;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;

import static com.jgelderloos.smartroomba.roomba.RoombaConstants.SensorPacketGroup.P100;

public class SmartRoomba implements Runnable {
    private RoombaComm roombaComm;
    private String comPort;
    private int pauseTime;
    private DataCSV dataCSV;
    private Queue<RoombaInfo> roombaInfoQueue;
    private RoombaUtilities roombaUtilities;
    private RoombaMapData roombaMapData;

    public SmartRoomba(RoombaComm roombaComm, String comPort, int pauseTime, boolean debug, boolean hwHandshake,
           DataCSV dataCSV, Queue<RoombaInfo> roombaInfoQueue) {
        this.roombaComm = roombaComm;
        this.comPort = comPort;
        this.pauseTime = pauseTime;
        this.dataCSV = dataCSV;
        this.roombaInfoQueue = roombaInfoQueue;
        this.roombaUtilities = new RoombaUtilities();
        this.roombaMapData = new RoombaMapData();

        roombaComm.debug = debug;

        if (roombaComm instanceof RoombaCommSerial) {
            RoombaCommSerial serial = (RoombaCommSerial) roombaComm;
            serial.setWaitForDSR(hwHandshake);
            // TODO: should this be for all RoombaComms?
            serial.setProtocol("OI");
        }

    }

    public void run() {
        if (!roombaComm.connect(comPort)) {
            System.out.println("Couldn't connect to "+ comPort);
            return;
        }

        System.out.println("Roomba startup");
        //roombaComm.startup();
        roombaComm.send(OpCodes.START.getId());

        System.out.println("Press return to exit.");
        int dataCount = 1;
        LocalDateTime lastSensorUpdate = LocalDateTime.now();
        boolean running = true;
        while (running) {
            try {
                if (System.in.available() != 0) {
                    System.out.println("key pressed");
                    running = false;
                }
            } catch (IOException ioe) {
                System.out.println("IOException while reading keyboard input");
            }

            // TODO: use Stream instead of always requesting packets
            //boolean rc =  roombaComm.updateSensors();
            byte[] sensorCmd = {(byte)OpCodes.SENSORS.getId(), (byte)P100.getId()};
            roombaComm.setReadRequestLength(roombaUtilities.getSensorPacketSize(P100));
            roombaComm.send(sensorCmd);

            // TODO: do we need an end packet for the recorded sensor data so we stop the replay?

            boolean dataAvailable = true;
            while (dataAvailable) {
                SensorData sensorData = roombaComm.getSensorDataQueue().poll();
                // If a valid dataCsv has been supplied then it will record
                if (sensorData != null) {
                    lastSensorUpdate = LocalDateTime.now();
                    System.out.println("Sensor Data: " + dataCount + " " + lastSensorUpdate.toString());
                    System.out.println(sensorData.getRawDataAsCSVString());
                    processData(sensorData);
                    dataCSV.writeData(sensorData);
                    dataCount++;
                } else {
                    dataAvailable = false;
                }
            }

            long timeWithoutSensor = Duration.between(lastSensorUpdate, LocalDateTime.now()).toMillis();

            long sensorCheckInterval = 5000;
            if (timeWithoutSensor > sensorCheckInterval) {
                lastSensorUpdate = LocalDateTime.now();
                System.out.println("No sensor data in over " + sensorCheckInterval/1000 + " seconds. Make sure the Roomba is on.");
            }

            //roombaComm.pause(pauseTime);
            try {
                Thread.sleep(pauseTime);
            } catch (Exception e) {
                System.out.println("Exception sleeping while wait for DSR");
            }

        }
        System.out.println("Disconnecting");
        dataCSV.close();
        roombaComm.disconnect();

        System.out.println("Done");
    }

    private void processData(SensorData sensorData) {
        if (!isSafeToContinue(sensorData)) {
            roombaComm.send(OpCodes.START.getId());
            System.out.println("Unsafe condition detected by sensors. Stopping Roomba");
        } else {
            RoombaInfo roombaInfo = roombaMapData.processSensorData(sensorData);
            roombaInfoQueue.add(roombaInfo);
        }
    }

    private boolean isSafeToContinue(SensorData sensorData) {
        return !sensorData.isCliffLeft() && !sensorData.isCliffRight() && !sensorData.isCliffFrontLeft() && !sensorData.isCliffFrontRight() &&
                !sensorData.isWheelDropLeft() && !sensorData.isWheelDropRight() && !sensorData.isOverCurrentLeftWheel() &&
                !sensorData.isOverCurrentRightWheel() && !sensorData.isOverCurrentMainBrush() && !sensorData.isOverCurrentSideBrush();
    }
}
