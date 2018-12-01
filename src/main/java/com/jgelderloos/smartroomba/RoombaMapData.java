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
                double innerDistance;
                // TODO: handle negative distances
                if (changeInLeftDistance > changeInRightDistance) {
                    innerDistance = changeInRightDistance;
                } else {
                    innerDistance = changeInLeftDistance;
                }

                double innerRadius = roombaUtilities.getRadius(Math.abs(changeInRadians), innerDistance);
                double centerRadius = innerRadius + (RoombaConstants.WHEELBASE / 2);

                // TODO: this will be used in the total distance traveled
                double changeInCenterDistance = roombaUtilities.getArcDistance(Math.abs(changeInRadians), centerRadius);

                changeInStraightDistance = roombaUtilities.getStraightDistance(Math.abs(changeInRadians), centerRadius);

                radians += changeInRadians;
            }

            // TODO: near side being X or Y may depend on the current angle of roomba, i think the current wat will always be correct...
            // TODO: also need to think about negative radians being passed in here, i think it is ok as is...
            double changeInX = roombaUtilities.getFarSideLength(radians, changeInStraightDistance);
            double changeInY = roombaUtilities.getNearSideLength(radians, changeInStraightDistance);

            position.setLocation(position.getX() + changeInX, position.getY() + changeInY);

            System.out.println("Position updated to: " + position.toString() + ", radians: " + radians + ", degrees: " + Math.toDegrees(radians));
        }
    }
}
