package com.jgelderloos.smartroomba;

import com.jgelderloos.smartroomba.roomba.RoombaConstants;
import com.jgelderloos.smartroomba.roomba.RoombaUtilities;
import com.jgelderloos.smartroomba.roomba.SensorData;

import java.awt.geom.Point2D;

public class RoombaMapData {
    private Point2D.Double position = new Point2D.Double(0, 0);
    //private double xPosition = 0;
    //private double yPosition = 0;
    private int lastLeftEncoderCount = 0;
    private int lastRightEncoderCount = 0;
    private boolean processedFirstSensorData = false;
    private RoombaUtilities roombaUtilities = new RoombaUtilities();

    public void processSensorData(SensorData sensorData) {
        short distance = sensorData.getDistance();
        short angle = sensorData.getAngle();
        int currentLeftEncoderCount = sensorData.getLeftEncoderCount();
        int currentRightEncoderCount = sensorData.getRightEncoderCount();

        if (!processedFirstSensorData) {
            processedFirstSensorData = true;
            lastLeftEncoderCount = currentLeftEncoderCount;
            lastRightEncoderCount = currentRightEncoderCount;
        } else {
            int changeInLeftEncoderCounts = currentLeftEncoderCount - lastLeftEncoderCount;
            int changeInRightEncoderCounts = currentRightEncoderCount - lastRightEncoderCount;
            double changeInLeftDistance = roombaUtilities.getMilimetersFromEncoderCounts(changeInLeftEncoderCounts);
            double changeInRightDistance = roombaUtilities.getMilimetersFromEncoderCounts(changeInRightEncoderCounts);

            if ((changeInLeftDistance + changeInRightDistance) / 2 != distance) {
                System.out.println("Detected difference in encoder counts and distance. Left distance: " +
                        changeInLeftDistance + ", right distance: " + changeInRightDistance + ", total distance: " +
                        distance);
            }

            double calculatedAngle = roombaUtilities.getAngleFromWheelDistances(changeInLeftDistance, changeInRightDistance);

            if (angle != calculatedAngle) {
                System.out.println("Detected difference in calculated angle and sensor angle. Calculated angle: " +
                        calculatedAngle + ", sensor angle: " + angle);
            }

            if (calculatedAngle != 0) {
                double innerRadius = roombaUtilities.getRadius(calculatedAngle, changeInRightDistance);

                double centerRadius = innerRadius + (RoombaConstants.WHEELBASE / 2);
                double centerDistance = roombaUtilities.getDistance(angle, centerRadius);

                Point2D.Double centerPosition = new Point2D.Double(position.getX() - centerDistance, position.getY());
                //double xCenter = position.getX() - centerDistance;
                //double yCenter = position.getY();

                Point2D.Double fromCenterDistance = new Point2D.Double(
                        roombaUtilities.getLength(calculatedAngle, centerRadius),
                        roombaUtilities.getHeight(calculatedAngle, centerRadius));
                //double xFromCenter = roombaUtilities.getLength(calculatedAngle, centerRadius);
                //double yFromCenter = roombaUtilities.getHeight(calculatedAngle, centerRadius);

                //double currentXPosition = xCenter + xFromCenter;
                //double currentYPosition = yCenter + yFromCenter;

                //position.setLocation(currentXPosition, currentYPosition);
                position.setLocation(centerPosition.getX() + fromCenterDistance.getX(),
                        centerPosition.getY() + fromCenterDistance.getY());
            } else {
                //yPosition += changeInLeftDistance;
                position.setLocation(position.getX(), position.getY() + changeInLeftDistance);
            }

            System.out.println("Position updated to: " + position.toString());
        }
    }
}
