/*
 *  SmartRoomba - ReplaySensorDataThread
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
