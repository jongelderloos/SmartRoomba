package com.jgelderloos.smartroomba;

import com.jgelderloos.smartroomba.roomba.SensorData;
import com.jgelderloos.smartroomba.roombacomm.RoombaCommSerial;

import java.io.IOException;

public class SmartRoomba {
    private RoombaCommSerial roombaComm;
    private String comPort;
    private int pauseTime;

    public SmartRoomba(RoombaCommSerial roombaComm, String comPort, int pauseTime, boolean debug, boolean hwHandshake) {
        this.roombaComm = roombaComm;
        this.comPort = comPort;
        this.pauseTime = pauseTime;

        roombaComm.debug = debug;
        roombaComm.setWaitForDSR(hwHandshake);
        roombaComm.setProtocol("OI");

    }

    public void run() {
        if (!roombaComm.connect(comPort)) {
            System.out.println("Couldn't connect to "+ comPort);
            System.exit(1);
        }

        System.out.println("Roomba startup");
        roombaComm.startup();

        System.out.println("Press return to exit.");
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
            boolean rc =  roombaComm.updateSensors();
            if (!rc) {
                System.out.println("No Roomba. :(  Is it turned on?");
                continue;
            }

            boolean dataAvailable = true;
            while (dataAvailable) {
                SensorData sensorData = roombaComm.sensorDataQueue.poll();
                // If a valid dataCsv has been supplied then it will record
                if (sensorData != null) {
                    roombaComm.dataCsv.writeData(sensorData);
                    System.out.println(sensorData.getRawDataAsCSVString());
                } else {
                    dataAvailable = false;
                }
            }

            roombaComm.pause(pauseTime);
        }
        System.out.println("Disconnecting");
        roombaComm.dataCsv.close();
        roombaComm.disconnect();

        System.out.println("Done");
    }

}
