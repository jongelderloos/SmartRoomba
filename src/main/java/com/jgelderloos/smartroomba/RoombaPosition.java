package com.jgelderloos.smartroomba;

import java.awt.geom.Point2D;

public class RoombaPosition {
    private Point2D.Double position;
    private double radians;
    private double degrees;

    public RoombaPosition(Point2D.Double position, double radians, double degrees) {
        this.position = position;
        this.radians = radians;
        this.degrees = degrees;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public double getRadians() {
        return radians;
    }

    public void setRadians(double radians) {
        this.radians = radians;
    }

    public double getDegrees() {
        return degrees;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }
}
