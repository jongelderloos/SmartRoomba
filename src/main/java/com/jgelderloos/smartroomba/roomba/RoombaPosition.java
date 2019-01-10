package com.jgelderloos.smartroomba.roomba;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;

public class RoombaPosition {
    private Point2D.Double position;
    private double radians;
    private double degrees;
    private LocalDateTime dateTime;

    public RoombaPosition(Point2D.Double position, double radians, double degrees, LocalDateTime dateTime) {
        this.position = position;
        this.radians = radians;
        this.degrees = degrees;
        this.dateTime = dateTime;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
