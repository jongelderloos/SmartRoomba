package com.jgelderloos.smartroomba.roombacomm;

import com.jgelderloos.smartroomba.roomba.SensorData;
import com.jgelderloos.smartroomba.utilities.DataCSVReader;
import com.jgelderloos.smartroomba.utilities.ReplaySensorDataThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoombaCommPlaybackMode extends RoombaComm {
    private static final Logger LOGGER = LogManager.getLogger(RoombaCommSerial.class);
    private Queue<SensorData> sensorDataQueue;
    private Queue<SensorData> fileDataQueue;
    private DataCSVReader dataCSVReader;
    private Thread replaySensorDataThread;

    public RoombaCommPlaybackMode() {
        super();
        sensorDataQueue = new ConcurrentLinkedQueue<>();
        fileDataQueue = new ConcurrentLinkedQueue<>();
        dataCSVReader = new DataCSVReader();
        replaySensorDataThread = new Thread(new ReplaySensorDataThread(sensorDataQueue, fileDataQueue));
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
        List<String> fileData = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(portId))) {
            dataCSVReader = new DataCSVReader(bufferedReader);
            fileData = dataCSVReader.readData();
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file. {}", portId, e);
        } catch (IOException e) {
            LOGGER.error("Could not close file. {}", portId, e);
        }

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

        replaySensorDataThread.start();
        return true;
    }

    public void disconnect() {
        replaySensorDataThread.interrupt();
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

    @Override
    public void setReadRequestLength(int readRequestLength) {
        // This is not applicable when replaying from a file as everything that was recorded will be returned
    }


}
