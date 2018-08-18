/*
 *  SmartRoomba - RoombaUtilities
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

import com.jgelderloos.smartroomba.roomba.RoombaConstants.SensorPacketGroup;
import java.util.HashMap;
import java.util.Map;

public class RoombaUtilities {

    private Map<SensorPacketGroup,Integer> sensorPackSizeMap;

    public RoombaUtilities() {
        sensorPackSizeMap = new HashMap<>();
        sensorPackSizeMap.put(SensorPacketGroup.P0, 26);
        sensorPackSizeMap.put(SensorPacketGroup.P1, 10);
        sensorPackSizeMap.put(SensorPacketGroup.P2, 6);
        sensorPackSizeMap.put(SensorPacketGroup.P3, 10);
        sensorPackSizeMap.put(SensorPacketGroup.P4, 14);
        sensorPackSizeMap.put(SensorPacketGroup.P5, 12);
        sensorPackSizeMap.put(SensorPacketGroup.P6, 52);
        // The spec says pack 100 is 80 in length but 90 bytes are returned
        sensorPackSizeMap.put(SensorPacketGroup.P100, 93);
        sensorPackSizeMap.put(SensorPacketGroup.P101, 28);
        sensorPackSizeMap.put(SensorPacketGroup.P106, 12);
        sensorPackSizeMap.put(SensorPacketGroup.P107, 9);
    }

    // TODO: null handling
    public int getSensorPacketSize(SensorPacketGroup packet) {
        return sensorPackSizeMap.get(packet);
    }
}
