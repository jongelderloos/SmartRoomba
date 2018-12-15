/*
 *  SmartRoomba - RoombaConstants
 *
 *  Copyright (c) 2006 Tod E. Kurt, tod@todbot.com, ThingM
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

public class RoombaConstants {
    /** distance between wheels on the roomba, in millimeters */
    public static final double WHEELBASE = 258f;
    /** mm/deg is circumference distance divided by 360 degrees */
    public static final double MILLIMETERS_PER_DEGREE = WHEELBASE * Math.PI / 360f;
    /** mm/rad is a circumference distance divied by two pi */
    public static final double MILLIMETERS_PER_RADIAN = WHEELBASE / 2f;
    public static final double MILLIMETERS_PER_WHEEL_TURN = 72f;
    public static final double ENCODER_COUNTS_PER_WHEEL_TURN = 508.8f;


    public enum SensorPacketGroup {
        P0(0),
        P1(1),
        P2(2),
        P3(3),
        P4(4),
        P5(5),
        P6(6),
        P100(100),
        P101(101),
        P106(106),
        P107(107);

        private int id;

        SensorPacketGroup(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum OpCodes {
        START(128),
        BAUD(129),
        SENSORS(142);

        private int id;

        OpCodes(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
