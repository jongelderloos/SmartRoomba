package com.jgelderloos.smartroomba;

import com.jgelderloos.smartroomba.roomba.RoombaConstants.OpCodes;
import com.jgelderloos.smartroomba.roomba.RoombaUtilities;
import com.jgelderloos.smartroomba.roomba.SensorData;
import com.jgelderloos.smartroomba.roombacomm.RoombaComm;
import com.jgelderloos.smartroomba.roombacomm.RoombaCommSerial;
import com.jgelderloos.smartroomba.utilities.DataCSV;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.jgelderloos.smartroomba.roomba.RoombaConstants.SensorPacketGroup.P100;

public class SmartRoomba {
    private RoombaComm roombaComm;
    private String comPort;
    private int pauseTime;
    private DataCSV dataCSV;
    private RoombaUtilities roombaUtilities;
    private RoombaMapData roombaMapData;

    public SmartRoomba(RoombaComm roombaComm, String comPort, int pauseTime, boolean debug, boolean hwHandshake,
           DataCSV dataCSV) {
        this.roombaComm = roombaComm;
        this.comPort = comPort;
        this.pauseTime = pauseTime;
        this.dataCSV = dataCSV;
        roombaUtilities = new RoombaUtilities();
        roombaMapData = new RoombaMapData();

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
            System.exit(1);
        }

        System.out.println("Roomba startup");
        //roombaComm.startup();
        roombaComm.send(OpCodes.START.getId());

        System.out.println("Press return to exit.");
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

            // TODO: print a message like this if we are not receiving any sensor data
            /*
            if (!rc) {
                System.out.println("No Roomba. :(  Is it turned on?");
                continue;
            }
            */

            boolean dataAvailable = true;
            while (dataAvailable) {
                SensorData sensorData = roombaComm.getSensorDataQueue().poll();
                // If a valid dataCsv has been supplied then it will record
                if (sensorData != null) {
                    lastSensorUpdate = LocalDateTime.now();
                    processData(sensorData);
                    dataCSV.writeData(sensorData);
                    System.out.println(sensorData.getRawDataAsCSVString());
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
        } else {
            roombaMapData.processSensorData(sensorData);
        }
    }

    private boolean isSafeToContinue(SensorData sensorData) {
        return sensorData.isCliffLeft() || sensorData.isCliffRight() || sensorData.isCliffFrontLeft() || sensorData.isCliffFrontRight() ||
                sensorData.isWheelDropLeft() || sensorData.isWheelDropRight() || sensorData.isOverCurrentLeftWheel() ||
                sensorData.isOverCurrentRightWheel() || sensorData.isOverCurrentMainBrush() || sensorData.isOverCurrentSideBrush();
    }
}
