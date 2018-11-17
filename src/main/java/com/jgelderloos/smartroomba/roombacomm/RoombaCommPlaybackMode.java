package com.jgelderloos.smartroomba.roombacomm;

import com.jgelderloos.smartroomba.roomba.SensorData;
import com.jgelderloos.smartroomba.utilities.DataCSVReader;
import com.jgelderloos.smartroomba.utilities.ReplaySensorDataThread;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoombaCommPlaybackMode extends RoombaComm {
    private Queue<SensorData> sensorDataQueue;
    private Queue<SensorData> fileDataQueue;
    private DataCSVReader dataCSVReader;
    private ReplaySensorDataThread replaySensorDataThread;

    public RoombaCommPlaybackMode() {
        super();
        sensorDataQueue = new ConcurrentLinkedQueue<>();
        fileDataQueue = new ConcurrentLinkedQueue<>();
        dataCSVReader = new DataCSVReader();
        replaySensorDataThread = new ReplaySensorDataThread(sensorDataQueue, fileDataQueue);
    }

    public String[] listPorts() {
        String[] portString = new String[1];
        portString[0] = "No ports for RoombaCommPlaybackMode";
        return portString;
    }

    public Queue<SensorData> getSensorDataQueue() {
        return sensorDataQueue;
    }

    public boolean connect(String portId) {
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader(portId);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException. Could not find file: " + portId);
        }

        dataCSVReader = new DataCSVReader(bufferedReader);
        List<String> fileData = dataCSVReader.readData();
        boolean isFirstLine = true;
        for (String currentLine : fileData) {
            // Skip the file header on the first line
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                LocalDateTime dateTime = null;
                String[] splitLine = currentLine.split(",");
                byte[] byteArray = new byte[splitLine.length - 1];
                boolean isFirstByte = true;
                int dataByteNumber = 0;
                for (String dataByte : splitLine) {
                    if (isFirstByte) {
                        isFirstByte = false;

                        String dateTimeString = splitLine[0];
                        dateTime = LocalDateTime.parse(dateTimeString);
                    } else {
                        byte currentByte = (byte)(int) Integer.decode(dataByte);
                        byteArray[dataByteNumber] = currentByte;
                        dataByteNumber++;
                    }

                }
                SensorData sensorData = new SensorData(byteArray, splitLine.length - 1, dateTime);
                fileDataQueue.add(sensorData);
            }
        }

        Thread replayThread = new Thread(replaySensorDataThread);
        replayThread.start();
        return true;
    }

    public void disconnect() {
    }

    public boolean send(byte[] bytes) {
        return true;
    }

    public boolean send(int b) {
        return true;
    }

    public boolean updateSensors() {
        return false;
    }

    public void setReadRequestLength(int readRequestLength) {
    }


}
