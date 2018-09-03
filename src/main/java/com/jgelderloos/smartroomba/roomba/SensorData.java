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
    private static final int MAX_SENSOR_BYTES = 93; // 93 bytes returned when requesting all packets

	private static final int BUMPER_RIGHT_MASK = 0x01;
	private static final int BUMPER_LEFT_MASK = 0x02;
    private static final int WHEEL_DROP_RIGHT_MASK = 0x04;
    private static final int WHEEL_DROP_LEFT_MASK = 0x08;
	private static final int CLIFF_MASK = 0x01;
    private static final int VIRTUAL_WALL_MASK = 0x01;
    private static final int OVER_CURRENT_LEFT_WHEEL_MASK = 0x10;
    private static final int OVER_CURRENT_RIGHT_WHEEL_MASK = 0x08;
    private static final int OVER_CURRENT_MAIN_BRUSH_MASK = 0x04;
    private static final int OVER_CURRENT_SIDE_BRUSH_MASK = 0x01;

    private static final int DOCK_BUTTON_MASK = 0x04;
	private static final int SPOT_BUTTON_MASK = 0x02;
	private static final int CLEAN_BUTTON_MASK = 0x01;

    private static final int LIGHT_BUMPER_RIGHT_MASK = 0x20;
    private static final int LIGHT_BUMPER_FRONT_RIGHT_MASK = 0x10;
    private static final int LIGHT_BUMPER_CENTER_RIGHT_MASK = 0x08;
    private static final int LIGHT_BUMPER_CENTER_LEFT_MASK = 0x04;
    private static final int LIGHT_BUMPER_FRONT_LEFT_MASK = 0x02;
    private static final int LIGHT_BUMPER_LEFT_MASK = 0x01;

    // Sensor packet indicies
    public enum PacketOffsets {
        BUMPS_WHEEL_DROPS,
        WALL,
        CLIFF_LEFT,
        CLIFF_FRONT_LEFT,
        CLIFF_FRONT_RIGHT,
        CLIFF_RIGHT,
        VIRTUAL_WALL,
        WHEEL_OVER_CURRENTS,
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

    private byte getBumpsAndWheelDrops() {
        return sensorData[PacketOffsets.BUMPS_WHEEL_DROPS.ordinal()];
    }

    public boolean isBumpLeft() {
        return (getBumpsAndWheelDrops() & BUMPER_LEFT_MASK) != 0;
    }

    public boolean isBumpRight() {
        return (getBumpsAndWheelDrops() & BUMPER_RIGHT_MASK) != 0;
    }

    public boolean isWheelDropLeft() {
        return (getBumpsAndWheelDrops() & WHEEL_DROP_LEFT_MASK) != 0;
    }

    public boolean isWheelDropRight() {
        return (getBumpsAndWheelDrops() & WHEEL_DROP_RIGHT_MASK) != 0;
    }

    private byte getCliffLeft() {
        return sensorData[PacketOffsets.CLIFF_LEFT.ordinal()];
    }

    public boolean isCliffLeft() {
        return (getCliffLeft() & CLIFF_MASK) != 0;
    }

    private byte getCliffFrontLeft() {
        return sensorData[PacketOffsets.CLIFF_FRONT_LEFT.ordinal()];
    }

    public boolean isCliffFrontLeft() {
        return (getCliffFrontLeft() & CLIFF_MASK) != 0;
    }

    private byte getCliffFrontRight() {
        return sensorData[PacketOffsets.CLIFF_FRONT_RIGHT.ordinal()];
    }

    public boolean isCliffFrontRight() {
        return (getCliffFrontRight() & CLIFF_MASK) != 0;
    }

    private byte getCliffRight() {
        return sensorData[PacketOffsets.CLIFF_RIGHT.ordinal()];
    }

    public boolean isCliffRight() {
        return (getCliffRight() & CLIFF_MASK) != 0;
    }

    private byte getVirtualWall() {
        return sensorData[PacketOffsets.VIRTUAL_WALL.ordinal()];
    }

    public boolean isVirtualWall() {
        return (getVirtualWall() & VIRTUAL_WALL_MASK) != 0;
    }

    private byte getWheelOverCurrents() {
        return sensorData[PacketOffsets.WHEEL_OVER_CURRENTS.ordinal()];
    }

    public boolean isOverCurrentLeftWheel() {
        return (getWheelOverCurrents() & OVER_CURRENT_LEFT_WHEEL_MASK) != 0;
    }

    public boolean isOverCurrentRightWheel() {
        return (getWheelOverCurrents() & OVER_CURRENT_RIGHT_WHEEL_MASK) != 0;
    }

    public boolean isOverCurrentMainBrush() {
        return (getWheelOverCurrents() & OVER_CURRENT_MAIN_BRUSH_MASK) != 0;
    }

    public boolean isOverCurrentSideBrush() {
        return (getWheelOverCurrents() & OVER_CURRENT_SIDE_BRUSH_MASK) != 0;
    }

    private byte getButtons() {
        return (sensorData[PacketOffsets.BUTTONS.ordinal()]);
    }

    public boolean isCleanButton() {
        return (getButtons() & CLEAN_BUTTON_MASK) != 0;
    }

    public boolean isSpotButton() {
        return (getButtons() & SPOT_BUTTON_MASK) != 0;
    }

    public boolean isDockButton() {
        return (getButtons() & DOCK_BUTTON_MASK) != 0;
    }

    public short getDistance() {
        return (short) ((sensorData[PacketOffsets.DISTANCE_HI.ordinal()] << 8) | sensorData[PacketOffsets.DISTANCE_LO.ordinal()]);
    }

    public short getAngle() {
        return (short) ((sensorData[PacketOffsets.ANGLE_HI.ordinal()] << 8) | sensorData[PacketOffsets.ANGLE_LO.ordinal()]);
    }

    public short getLeftEncoderCount() {
        return (short) ((sensorData[PacketOffsets.LEFT_ENCODER_COUNTS_HI.ordinal()] << 8) | sensorData[PacketOffsets.LEFT_ENCODER_COUNTS_LO.ordinal()]);
    }

    public short getRightEncoderCount() {
        return (short) ((sensorData[PacketOffsets.RIGHT_ENCODER_COUNTS_HI.ordinal()] << 8) | sensorData[PacketOffsets.RIGHT_ENCODER_COUNTS_LO.ordinal()]);
    }

    private byte getLightBumper() {
        return sensorData[PacketOffsets.LIGHT_BUMPER.ordinal()];
    }

    public boolean isLightBumperRight() {
        return (getLightBumper() & LIGHT_BUMPER_RIGHT_MASK) != 0;
    }

    public boolean isLightBumperFrontRight() {
        return (getLightBumper() & LIGHT_BUMPER_FRONT_RIGHT_MASK) != 0;
    }

    public boolean isLightBumperCenterRight() {
        return (getLightBumper() & LIGHT_BUMPER_CENTER_RIGHT_MASK) != 0;
    }

    public boolean isLightBumperCenterLeft() {
        return (getLightBumper() & LIGHT_BUMPER_CENTER_LEFT_MASK) != 0;
    }

    public boolean isLightBumperFrontLeft() {
        return (getLightBumper() & LIGHT_BUMPER_FRONT_LEFT_MASK) != 0;
    }

    public boolean isLightBumperLeft() {
        return (getLightBumper() & LIGHT_BUMPER_LEFT_MASK) != 0;
    }

}
