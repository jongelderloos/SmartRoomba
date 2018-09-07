package com.jgelderloos.smartroomba.roombacomm;

import com.jgelderloos.smartroomba.roomba.SensorData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Queue;

public class PlaybackThread extends Thread {
    private boolean running = false;
    private Queue<SensorData> sensorDataQueue;
    private Queue<SensorData> fileDataQuete;
    private LocalDateTime threadStartTime;
    private LocalDateTime dataStartTime;

    public PlaybackThread(Queue<SensorData> sensorDataQueue, Queue<SensorData> fildDataQueue) {
        this.sensorDataQueue = sensorDataQueue;
        this.fileDataQuete = fildDataQueue;
    }

    public void run() {
        running = true;
        threadStartTime = LocalDateTime.now();

        while (fileDataQuete.peek() != null) {
            SensorData currentData = fileDataQuete.peek();
            if (dataStartTime == null) {
                dataStartTime = currentData.getDateTime();
            }

            Duration threadRunTime = Duration.between(threadStartTime, LocalDateTime.now());
            Duration nextDataTime = Duration.between(dataStartTime, currentData.getDateTime());

            if (nextDataTime.compareTo(threadRunTime) == 1) {
                currentData = fileDataQuete.poll();
                sensorDataQueue.add(currentData);
            }
        }
    }
}
