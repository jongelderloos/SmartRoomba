/*
 *  SmartRoomba - SensorData
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

package com.jgelderloos.smartroomba.roomba;

import java.time.LocalDateTime;

public class SensorData {
    public static final int MAX_SENSOR_BYTES = 93; // 93 bytes returned when requesting all packets

    // Sensor packet indicies
    public enum PacketOffsets {
        BUMPS_WHEEL_DROPS,
        WALL,
        CLIFF_LEFT,
        CLIFF_FRONT_LEFT,
        CLIFF_FRONT_RIGHT,
        CLIFF_RIGHT,
        VIRTUAL_WALL,
        WHEEL_OVERCURRENTS,
        DIRT_DETECT,
        UNUSED_1,
        INFRARED_CHARACTER_OMNI,
        BUTTONS,
        DISTANCE_HI,
        DISTANCE_LO,
        ANGLE_HI,
        ANGLE_LO,
        CHARGING_STATE,
        VOLTAGE_HI,
        VOLTAGE_LO,
        CURRENT_HI,
        CURRENT_LO,
        TEMPERATURE,
        BATTERY_CHARGE_HI,
        BATTERY_CHARGE_LO,
        BATTERY_CAPACITY_HI,
        BATTERY_CAPACITY_LO,
        WALL_SIGNAL_HI,
        WALL_SIGNAL_LO,
        CLIFF_LEFT_SIGNAL_HI,
        CLIFF_LEFT_SIGNAL_LO,
        CLIFF_FRONT_LEFT_SIGNAL_HI,
        CLIFF_FRONT_LEFT_SIGNAL_LO,
        CLIFF_FRONT_RIGHT_SIGNAL_HI,
        CLIFF_FRONT_RIGHT_SIGNAL_LO,
        CLIFF_RIGHT_SIGNAL_HI,
        CLIFF_RIGHT_SIGNAL_LO,
        UNUSED_2,
        UNUSED_3,
        UNUSED_4,
        CHARGING_SOURCES_AVAILABLE,
        OI_MODE,
        SONG_NUMBER,
        SONG_PLAYING,
        NUMBER_OF_STREAM_PACKETS,
        REQUESTED_VELOCITY_HI,
        REQUESTED_VELOCITY_LO,
        REQUESTED_RADIUS_HI,
        REQUESTED_RADIUS_LO,
        REQUESTED_RIGHT_VELOCITY_HI,
        REQUESTED_RIGHT_VELOCITY_LO,
        REQUESTED_LEFT_VELOCITY_HI,
        REQUESTED_LEFT_VELOCITY_LO,
        LEFT_ENCODER_COUNTS_HI,
        LEFT_ENCODER_COUNTS_LO,
        RIGHT_ENCODER_COUNTS_HI,
        RIGHT_ENCODER_COUNTS_LO,
        LIGHT_BUMPER,
        LIGHT_BUMP_LEFT_SIGNAL_HI,
        LIGHT_BUMP_LEFT_SIGNAL_LO,
        LIGHT_BUMP_FRONT_LEFT_SIGNAL_HI,
        LIGHT_BUMP_FRONT_LEFT_SIGNAL_LO,
        LIGHT_BUMP_CENTER_LEFT_SIGNAL_HI,
        LIGHT_BUMP_CENTER_LEFT_SIGNAL_LO,
        LIGHT_BUMP_CENTER_RIGHT_SIGNAL_HI,
        LIGHT_BUMP_CENTER_RIGHT_SIGNAL_LO,
        LIGHT_BUMP_FRONT_RIGHT_SIGNAL_HI,
        LIGHT_BUMP_FRONT_RIGHT_SIGNAL_LO,
        LIGHT_BUMP_RIGHT_SIGNAL_HI,
        LIGHT_BUMP_RIGHT_SIGNAL_LO,
        INFRARED_CHARACTER_LEFT,
        INFRARED_CHARACTER_RIGHT,
        LEFT_MOTOR_CURRENT_HI,
        LEFT_MOTOR_CURRENT_LO,
        RIGHT_MOTOR_CURRENT_HI,
        RIGHT_MOTOR_CURRENT_LO,
        MAIN_BRUSH_MOTOR_CURRENT_HI,
        MAIN_BRUSH_MOTOR_CURRENT_LO,
        SIDE_BRUSH_MOTOR_CURRENT_HI,
        SIDE_BRUSH_MOTOR_CURRENT_LO,
        STASIS,
        UNKNOWN1,
        UNKNOWN2,
        UNKNOWN3,
        UNKNOWN4,
        UNKNOWN5,
        UNKNOWN6,
        UNKNOWN7,
        UNKNOWN8,
        UNKNOWN9,
        UNKNOWN10,
        UNKNOWN11,
        UNKNOWN12,
        UNKNOWN13
    }

    private byte[] sensorData;
    private LocalDateTime dateTime;

    public SensorData(byte[] data, int dataLength) {
        dateTime = LocalDateTime.now();
        sensorData = new byte[MAX_SENSOR_BYTES];
        System.arraycopy(data, 0, sensorData, 0, dataLength);
    }

    public byte[] getRawSensorData() {
        return sensorData;
    }

    public String getRawDataAsCSVString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (byte b : sensorData) {
            stringBuilder.append("0x");
            stringBuilder.append(String.format("%02X", b));
            if (i++ != sensorData.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDataHeaderAsCSVString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (PacketOffsets packet: PacketOffsets.values()) {
            stringBuilder.append(packet.toString());
            if (i++ != PacketOffsets.values().length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
