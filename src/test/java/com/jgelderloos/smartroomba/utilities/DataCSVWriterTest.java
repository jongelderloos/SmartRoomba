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

public class DataCSVWriterTest {
    private DataCSVWriter dataCSVWriter;

    @Test
    public void nullFileWriter() {
        dataCSVWriter = new DataCSVWriter(null);

        boolean result = dataCSVWriter.writeData(mock(SensorData.class));
        Assert.assertFalse(result);

        result = dataCSVWriter.close();
        Assert.assertFalse(result);
    }

    @Test
    public void fileWriter() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        SensorData sensorData = mock(SensorData.class);
        doReturn(fileWriter).when(fileWriter).append(any(CharSequence.class));
        doReturn(LocalDateTime.now()).when(sensorData).getDateTime();

        dataCSVWriter = new DataCSVWriter(fileWriter);

        boolean result = dataCSVWriter.writeData(sensorData);
        Assert.assertTrue(result);
        result = dataCSVWriter.writeData(sensorData);
        Assert.assertTrue(result);
    }

    @Test
    public void fileWriterExceptionInWriteHeader() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        SensorData sensorData = mock(SensorData.class);
        doThrow(new IOException()).when(fileWriter).append(any(CharSequence.class));

        dataCSVWriter = new DataCSVWriter(fileWriter);

        boolean result = dataCSVWriter.writeData(sensorData);
        Assert.assertFalse(result);
    }

    @Test
    public void fileWriterExceptionInWriteData() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        SensorData sensorData = mock(SensorData.class);
        doReturn(LocalDateTime.now()).when(sensorData).getDateTime();
        doThrow(new IOException()).when(fileWriter).append(",");

        dataCSVWriter = new DataCSVWriter(fileWriter);

        boolean result = dataCSVWriter.writeData(sensorData);
        Assert.assertFalse(result);
    }

    @Test
    public void fileWriterExceptionInClose() throws IOException {
        FileWriter fileWriter = mock(FileWriter.class);
        doThrow(new IOException()).when(fileWriter).close();

        dataCSVWriter = new DataCSVWriter(fileWriter);

        boolean result = dataCSVWriter.close();
        Assert.assertFalse(result);
    }

}
