/*
 *  SmartRoomba - DataCSVReader
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataCSVReader {
    private static final Logger LOGGER = LogManager.getLogger(DataCSVReader.class);
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
            } catch (IOException e) {
                LOGGER.error("Error while reading CSV data. {}", e);
            }
        }
        return fileLines;
    }
}
