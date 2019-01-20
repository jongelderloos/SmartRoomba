package com.jgelderloos.smartroomba.utilities;

import com.jgelderloos.smartroomba.roomba.SensorData;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.Mockito.mock;

public class ReplaySensorDataThreadTest {
    private ReplaySensorDataThread replaySensorDataThread;

    @Test
    public void nullSensorDataQueue() {
        Queue<SensorData> replayDataQueue = new ConcurrentLinkedQueue<>();
        replayDataQueue.add(mock(SensorData.class));
        replayDataQueue.add(mock(SensorData.class));
        replaySensorDataThread = new ReplaySensorDataThread(null, replayDataQueue);

        replaySensorDataThread.run();

        Assert.assertFalse(replayDataQueue.isEmpty());
    }

    @Test
    public void nullReplayDataQueue() {
        Queue<SensorData> sensorDataQueue = new ConcurrentLinkedQueue<>();
        replaySensorDataThread = new ReplaySensorDataThread(sensorDataQueue, null);

        replaySensorDataThread.run();

        Assert.assertTrue(sensorDataQueue.isEmpty());
    }

    @Test
    public void normal() {
        Queue<SensorData> sensorDataQueue = new ConcurrentLinkedQueue<>();
        Queue<SensorData> replayDataQueue = new ConcurrentLinkedQueue<>();
        SensorData sensorData1 = new SensorData(new byte[2], 2, LocalDateTime.now());
        SensorData sensorData2 = new SensorData(new byte[2], 2, LocalDateTime.now().plusNanos(5));
        replayDataQueue.add(sensorData1);
        replayDataQueue.add(sensorData2);
        replaySensorDataThread = new ReplaySensorDataThread(sensorDataQueue, replayDataQueue);

        replaySensorDataThread.run();

        Assert.assertEquals(2, sensorDataQueue.size());
        Assert.assertEquals(sensorData1, sensorDataQueue.poll());
        Assert.assertEquals(sensorData2, sensorDataQueue.poll());
    }

}
