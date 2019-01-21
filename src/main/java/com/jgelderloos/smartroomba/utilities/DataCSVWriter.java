/*
 *  SmartRoomba - DataCSVWriter
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class DataCSVWriter {
    private static final Logger LOGGER = LogManager.getLogger(DataCSVWriter.class);
    private FileWriter fileWriter;
    private boolean isHeaderWritten = false;

    public DataCSVWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public boolean writeData(SensorData data) {
        boolean wasSuccess = false;
        if (fileWriter != null) {
            if (!isHeaderWritten) {
                isHeaderWritten = writeHeader(data);
            }
            try {
                if (isHeaderWritten) {
                    fileWriter.append(data.getDateTime().toString());
                    fileWriter.append(",");
                    fileWriter.append(data.getRawDataAsCSVString());
                    fileWriter.append("\n");
                    wasSuccess = true;
                }
            } catch (IOException e) {
                LOGGER.error("Error while writing CSV data. {}", e);
                close();
            }
        }
        return wasSuccess;
    }

    public boolean close() {
        boolean wasSuccess = false;
        try {
            if (fileWriter != null) {
                fileWriter.close();
                wasSuccess = true;
            }
        } catch (IOException e) {
            LOGGER.error("Error while closing CSV data file. {}", e);
        }
        return wasSuccess;
    }

    private boolean writeHeader(SensorData data) {
        boolean wasSuccess = false;
        try {
            fileWriter.append("TIME,");
            fileWriter.append(data.getDataHeaderAsCSVString());
            fileWriter.append("\n");
            wasSuccess = true;
        } catch (IOException e) {
            LOGGER.error("Error while writing CSV header to file. {}", e);
            close();
        }
        return wasSuccess;
    }

}
