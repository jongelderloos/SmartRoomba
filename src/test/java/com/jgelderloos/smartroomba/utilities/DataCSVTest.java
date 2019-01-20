package com.jgelderloos.smartroomba.utilities;

import com.jgelderloos.smartroomba.roomba.SensorData;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class DataCSVTest {
    private DataCSV dataCSV;

    @Test
    public void nullFileWriter() {
        dataCSV = new DataCSV(null);

        boolean result = dataCSV.writeData(mock(SensorData.class));
        Assert.assertFalse(result);

        result = dataCSV.close();
        Assert.assertFalse(result);
    }

    @Test
    public void fileWriter() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        SensorData sensorData = mock(SensorData.class);
        doReturn(fileWriter).when(fileWriter).append(any(CharSequence.class));
        doReturn(LocalDateTime.now()).when(sensorData).getDateTime();

        dataCSV = new DataCSV(fileWriter);

        boolean result = dataCSV.writeData(sensorData);
        Assert.assertTrue(result);
        result = dataCSV.writeData(sensorData);
        Assert.assertTrue(result);
    }

    @Test
    public void fileWriterExceptionInWriteHeader() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        SensorData sensorData = mock(SensorData.class);
        doThrow(new IOException()).when(fileWriter).append(any(CharSequence.class));

        dataCSV = new DataCSV(fileWriter);

        boolean result = dataCSV.writeData(sensorData);
        Assert.assertFalse(result);
    }

    @Test
    public void fileWriterExceptionInWriteData() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        SensorData sensorData = mock(SensorData.class);
        doReturn(LocalDateTime.now()).when(sensorData).getDateTime();
        doThrow(new IOException()).when(fileWriter).append(",");

        dataCSV = new DataCSV(fileWriter);

        boolean result = dataCSV.writeData(sensorData);
        Assert.assertFalse(result);
    }

    @Test
    public void fileWriterExceptionInClose() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        doThrow(new IOException()).when(fileWriter).close();

        dataCSV = new DataCSV(fileWriter);

        boolean result = dataCSV.close();
        Assert.assertFalse(result);
    }

}
