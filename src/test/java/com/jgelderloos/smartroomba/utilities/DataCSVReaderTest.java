package com.jgelderloos.smartroomba.utilities;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class DataCSVReaderTest {
    private DataCSVReader dataCSVReader;

    @Test
    public void testDataCSVReader_UnInitialized() {
        dataCSVReader = new DataCSVReader();

        List<String> readData = dataCSVReader.readData();
        Assert.assertTrue(readData.isEmpty());
    }

    @Test
    public void testDataCSVReader_GoodRead() throws IOException {
        BufferedReader bufferedReader = mock(BufferedReader.class);
        doReturn("StringData1").doReturn("StringData2").doReturn(null).when(bufferedReader).readLine();

        dataCSVReader = new DataCSVReader(bufferedReader);

        List<String> readData = dataCSVReader.readData();
        Assert.assertEquals(2, readData.size());
        Assert.assertEquals("StringData1", readData.get(0));
        Assert.assertEquals("StringData2", readData.get(1));
    }

    @Test
    public void testDataCSVReader_IOException() throws IOException {
        BufferedReader bufferedReader = mock(BufferedReader.class);
        doReturn("StringData1").doReturn("StringData2").doThrow(new IOException()).when(bufferedReader).readLine();

        dataCSVReader = new DataCSVReader(bufferedReader);

        List<String> readData = dataCSVReader.readData();
        Assert.assertEquals(2, readData.size());
        Assert.assertEquals("StringData1", readData.get(0));
        Assert.assertEquals("StringData2", readData.get(1));
    }

}
