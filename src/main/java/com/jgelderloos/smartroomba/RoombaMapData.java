/*
 *  SmartRoomba - RoombaMapData
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
import com.jgelderloos.smartroomba.roomba.RoombaUtilities;
import com.jgelderloos.smartroomba.roomba.SensorData;

import java.awt.geom.Point2D;

public class RoombaMapData {
    private Point2D.Double position = new Point2D.Double(0, 0);
    private double radians = 0;
    private int lastLeftEncoderCount = 0;
    private int lastRightEncoderCount = 0;
    private boolean processedFirstSensorData = false;
    private RoombaUtilities roombaUtilities = new RoombaUtilities();

    public void processSensorData(SensorData sensorData) {
        short distance = sensorData.getDistance();
        short sensorAngle = sensorData.getAngle();
        int currentLeftEncoderCount = sensorData.getLeftEncoderCount();
        int currentRightEncoderCount = sensorData.getRightEncoderCount();

        if (!processedFirstSensorData) {
            processedFirstSensorData = true;
            lastLeftEncoderCount = currentLeftEncoderCount;
            lastRightEncoderCount = currentRightEncoderCount;
        } else {
            int changeInLeftEncoderCounts = currentLeftEncoderCount - lastLeftEncoderCount;
            lastLeftEncoderCount = currentLeftEncoderCount;
            int changeInRightEncoderCounts = currentRightEncoderCount - lastRightEncoderCount;
            lastRightEncoderCount = currentRightEncoderCount;
            double changeInLeftDistance = roombaUtilities.getMilimetersFromEncoderCounts(changeInLeftEncoderCounts);
            double changeInRightDistance = roombaUtilities.getMilimetersFromEncoderCounts(changeInRightEncoderCounts);

            if ((changeInLeftDistance + changeInRightDistance) / 2 != distance) {
                System.out.println("Detected difference in encoder counts and distance. Left distance: " +
                        changeInLeftDistance + ", right distance: " + changeInRightDistance + ", total distance: " +
                        distance);
            }

            double changeInRadians = roombaUtilities.getRadiansFromWheelDistance(changeInLeftDistance, changeInRightDistance);

            if (sensorAngle != changeInRadians) {
                System.out.println("Detected difference in calculated angle and sensor angle. Calculated change in angle: " +
                        changeInRadians + ", sensor angle: " + sensorAngle);
            }

            double changeInStraightDistance = changeInLeftDistance;
            if (changeInRadians != 0) {

                // The smaller distance is the inside of the the turn
                double outerDistance;
                // TODO: handle negative distances
                if (changeInLeftDistance > changeInRightDistance) {
                    if (changeInLeftDistance > 0 && changeInRightDistance > 0) {
                        outerDistance = changeInLeftDistance;
                    } else {
                        outerDistance = changeInRightDistance;
                    }
                } else {
                    if (changeInLeftDistance > 0 && changeInRightDistance > 0) {
                        outerDistance = changeInRightDistance;
                    } else {
                        outerDistance = changeInLeftDistance;
                    }
                }

                double outerRadius = roombaUtilities.getRadius(Math.abs(changeInRadians), Math.abs(outerDistance));
                double centerRadius = outerRadius - (RoombaConstants.WHEELBASE / 2);

                // TODO: this will be used in the total distance traveled
                double changeInCenterDistance = roombaUtilities.getArcDistance(Math.abs(changeInRadians), centerRadius);

                changeInStraightDistance = roombaUtilities.getStraightDistance(Math.abs(changeInRadians), centerRadius);

                // TODO: not sure this is needed for anything
                double turnRadians = roombaUtilities.getTurnRadians(changeInRadians, changeInStraightDistance, centerRadius);

                // Get the point on the turn circle prior to the turn (radians)
                Point2D.Double beforeTurnPoint = roombaUtilities.getPointOnCircle(radians, centerRadius, changeInRadians < 0);

                // Get the point on the turn circle after the turn (radians + changeInRadians)
                Point2D.Double afterTurnPoint = roombaUtilities.getPointOnCircle(radians + changeInRadians, centerRadius, changeInRadians < 0);

                // Find the difference
                Point2D.Double positionChange = new Point2D.Double(afterTurnPoint.x - beforeTurnPoint.x, afterTurnPoint.y - beforeTurnPoint.y);

                radians += changeInRadians;
                position.setLocation(position.getX() + positionChange.x, position.getY() + positionChange.y);
            } else {
                double changeInX = roombaUtilities.getFarSideLength(radians, changeInStraightDistance);
                double changeInY = roombaUtilities.getNearSideLength(radians, changeInStraightDistance);
                position.setLocation(position.getX() + changeInX, position.getY() + changeInY);
            }
            System.out.println("Position updated to: " + position.toString() + ", radians: " + radians + ", degrees: " + Math.toDegrees(radians));
        }
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public double getRadians() {
        return radians;
    }

    public double getDegrees() {
        return Math.toDegrees(radians);
    }
}
