package com.jgelderloos.smartroomba.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataCSVReader {
    private BufferedReader bufferedReader;

    public DataCSVReader() {
        this.bufferedReader = null;
    }

    public DataCSVReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public List<String> readData() {
        List<String> fileLines = new ArrayList<>();
        if (bufferedReader != null) {
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileLines.add(line);
                }
            } catch (IOException ex) {
                System.out.println("IOException while reading CSV data");
            }
        }
        return fileLines;
    }
}
