package com.jgelderloos.smartroomba.utilities;

import com.jgelderloos.smartroomba.roomba.SensorData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;

public class ReplaySensorDataThread implements Runnable {
    private Queue<SensorData> sensorDataQueue;
    private Queue<SensorData> replayDataQueue;

    public ReplaySensorDataThread(Queue<SensorData> sensorDataQueue, Queue<SensorData> replayDataQueue) {
        this.sensorDataQueue = sensorDataQueue;
        this.replayDataQueue = replayDataQueue;
    }

    public void run() {
        LocalDateTime dataStartTime = null;
        LocalDateTime startTime = LocalDateTime.now();
        while (replayDataQueue.size() > 0) {
            SensorData replayData = replayDataQueue.poll();
            if (dataStartTime == null) {
                dataStartTime = replayData.getDateTime();
            }

            boolean dataAdded = false;
            while (!dataAdded) {
                LocalDateTime currentTime = LocalDateTime.now();
                Duration elapsedTime = Duration.between(startTime, currentTime);
                Duration nextDataElapsedTime = Duration.between(dataStartTime, replayData.getDateTime());

                if (elapsedTime.compareTo(nextDataElapsedTime) > 0) {
                    sensorDataQueue.add(replayData);
                    dataAdded = true;
                }
            }
        }
    }
}
