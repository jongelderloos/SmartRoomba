package com.jgelderloos.smartroomba;

import com.jgelderloos.smartroomba.roomba.RoombaConstants;
import com.jgelderloos.smartroomba.roomba.RoombaUtilities;
import com.jgelderloos.smartroomba.roomba.SensorData;

public class RoombaMapData {
    private double xPosition = 0;
    private double yPosition = 0;
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

                double xCenter = xPosition - centerDistance;
                double yCenter = yPosition;

                double xFromCenter = roombaUtilities.getLength(calculatedAngle, centerRadius);
                double yFromCenter = roombaUtilities.getHeight(calculatedAngle, centerRadius);

                double currentXPosition = xCenter + xFromCenter;
                double currentYPositoin = yCenter + yFromCenter;

                xPosition = currentXPosition;
                yPosition = currentYPositoin;

            } else {
                yPosition += changeInLeftDistance;
            }
        }
    }
}
