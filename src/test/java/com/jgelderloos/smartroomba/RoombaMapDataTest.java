/*
 *  SmartRoomba - RoombaMapDataTest
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

package com.jgelderloos.smartroomba;

import com.jgelderloos.smartroomba.roomba.RoombaConstants;
import com.jgelderloos.smartroomba.roomba.SensorData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;

public class RoombaMapDataTest {
    private RoombaMapData roombaMapData;
    private byte[] byteArray;
    private SensorData sensorData;
    private double tolerance = 1;

    private void setEncoderCounts(double innerRadius, double radians, boolean rightIsInner) {
        double outerRadius = innerRadius + RoombaConstants.WHEELBASE;
        double innerArc = innerRadius * Math.abs(radians);
        double outerArc = outerRadius * Math.abs(radians);
        short innerEncoderCounts = (short) Math.floor(innerArc / (Math.PI * RoombaConstants.MILLIMETERS_PER_WHEEL_TURN / RoombaConstants.ENCODER_COUNTS_PER_WHEEL_TURN));
        short outerEncoderCounts = (short) Math.floor(outerArc / (Math.PI * RoombaConstants.MILLIMETERS_PER_WHEEL_TURN / RoombaConstants.ENCODER_COUNTS_PER_WHEEL_TURN));
        byte innerEncoderHi = (byte) ((innerEncoderCounts & 0xFF00) >> 8);
        byte innerEncoderLo = (byte) (innerEncoderCounts & 0xFF);
        byte outerEncoderHi = (byte) ((outerEncoderCounts & 0xFF00) >> 8);
        byte outerEncoderLo = (byte) (outerEncoderCounts & 0xFF);
        if (rightIsInner) {
            byteArray[SensorData.PacketOffsets.LEFT_ENCODER_COUNTS_HI.ordinal()] = outerEncoderHi;
            byteArray[SensorData.PacketOffsets.LEFT_ENCODER_COUNTS_LO.ordinal()] = outerEncoderLo;
            byteArray[SensorData.PacketOffsets.RIGHT_ENCODER_COUNTS_HI.ordinal()] = innerEncoderHi;
            byteArray[SensorData.PacketOffsets.RIGHT_ENCODER_COUNTS_LO.ordinal()] = innerEncoderLo;
        } else {
            byteArray[SensorData.PacketOffsets.LEFT_ENCODER_COUNTS_HI.ordinal()] = innerEncoderHi;
            byteArray[SensorData.PacketOffsets.LEFT_ENCODER_COUNTS_LO.ordinal()] = innerEncoderLo;
            byteArray[SensorData.PacketOffsets.RIGHT_ENCODER_COUNTS_HI.ordinal()] = outerEncoderHi;
            byteArray[SensorData.PacketOffsets.RIGHT_ENCODER_COUNTS_LO.ordinal()] = outerEncoderLo;
        }
    }

    @Before
    public void testSetup() {
        roombaMapData = new RoombaMapData();

        // Setup the initial sensor data and process it. This give us an initial position on 0,0 and 0 degrees.
        byteArray = new byte[SensorData.MAX_SENSOR_BYTES];
        byteArray[SensorData.PacketOffsets.LEFT_ENCODER_COUNTS_HI.ordinal()] = 0;
        byteArray[SensorData.PacketOffsets.LEFT_ENCODER_COUNTS_LO.ordinal()] = 0;
        byteArray[SensorData.PacketOffsets.RIGHT_ENCODER_COUNTS_HI.ordinal()] = 0;
        byteArray[SensorData.PacketOffsets.RIGHT_ENCODER_COUNTS_LO.ordinal()] = 0;
        sensorData = new SensorData(byteArray, SensorData.MAX_SENSOR_BYTES);
        roombaMapData.processSensorData(sensorData);

        Assert.assertEquals(new Point2D.Double(0, 0), roombaMapData.getPosition());
        Assert.assertEquals(0, roombaMapData.getRadians(), 0);
        Assert.assertEquals(0, roombaMapData.getDegrees(), 0);
    }

    /**
     * Turn right with an inner radius of 258mm and outer radius of 516mm for a total of 90 degrees
     */
    @Test
    public void turnRight() {
        double innerRadius = RoombaConstants.WHEELBASE;
        double expectedRadians = Math.PI * -2 / 4;
        setEncoderCounts(innerRadius, expectedRadians, true);
        sensorData = new SensorData(byteArray, SensorData.MAX_SENSOR_BYTES);
        roombaMapData.processSensorData(sensorData);

        Point2D.Double position = roombaMapData.getPosition();
        double radians = roombaMapData.getRadians();
        double degrees = roombaMapData.getDegrees();
        Assert.assertEquals(innerRadius + (RoombaConstants.WHEELBASE / 2), position.x, tolerance);
        Assert.assertEquals(innerRadius + (RoombaConstants.WHEELBASE / 2), position.y, tolerance);
        Assert.assertEquals(expectedRadians, radians, tolerance / 10);
        Assert.assertEquals(Math.toDegrees(expectedRadians), degrees, tolerance);
    }

    /**
     * Turn right with an inner radius of 258mm and outer radius of 516mm for a total of 90 degrees
     */
    @Test
    public void turnRight2Chunks() {
        double innerRadius = RoombaConstants.WHEELBASE;
        double expectedRadians = Math.PI * -2 / 8;
        setEncoderCounts(innerRadius, expectedRadians, true);
        sensorData = new SensorData(byteArray, SensorData.MAX_SENSOR_BYTES);
        roombaMapData.processSensorData(sensorData);

        expectedRadians = Math.PI * -2 / 4;
        setEncoderCounts(innerRadius, expectedRadians, true);
        sensorData = new SensorData(byteArray, SensorData.MAX_SENSOR_BYTES);
        roombaMapData.processSensorData(sensorData);

        Point2D.Double position = roombaMapData.getPosition();
        double radians = roombaMapData.getRadians();
        double degrees = roombaMapData.getDegrees();
        Assert.assertEquals(innerRadius + (RoombaConstants.WHEELBASE / 2), position.x, tolerance);
        Assert.assertEquals(innerRadius + (RoombaConstants.WHEELBASE / 2), position.y, tolerance);
        Assert.assertEquals(expectedRadians, radians, tolerance / 10);
        Assert.assertEquals(Math.toDegrees(expectedRadians), degrees, tolerance);
    }

    /**
     * Turn right with an inner radius of 258mm and outer radius of 516mm for a total of 90 degrees
     */
    @Test
    public void turnLeft() {
        double innerRadius = RoombaConstants.WHEELBASE;
        double expectedRadians = Math.PI * 2 / 4;
        setEncoderCounts(innerRadius, expectedRadians, false);
        sensorData = new SensorData(byteArray, SensorData.MAX_SENSOR_BYTES);
        roombaMapData.processSensorData(sensorData);

        Point2D.Double position = roombaMapData.getPosition();
        double radians = roombaMapData.getRadians();
        double degrees = roombaMapData.getDegrees();
        Assert.assertEquals(-1 * (innerRadius + (RoombaConstants.WHEELBASE / 2)), position.x, tolerance);
        Assert.assertEquals(innerRadius + (RoombaConstants.WHEELBASE / 2), position.y, tolerance);
        Assert.assertEquals(expectedRadians, radians, tolerance / 10);
        Assert.assertEquals(Math.toDegrees(expectedRadians), degrees, tolerance);
    }

}
