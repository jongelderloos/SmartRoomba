/*
 *  SmartRoomba - RoombaViewer
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
import com.jgelderloos.smartroomba.roomba.RoombaInfo;
import com.jgelderloos.smartroomba.roomba.RoombaUtilities;
import com.jgelderloos.smartroomba.roombacomm.RoombaCommPlaybackMode;
import com.jgelderloos.smartroomba.utilities.DataCSVWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoombaViewer {

    public static void main(String[] args) {
        RoombaUtilities roombaUtilities = new RoombaUtilities();
        List<RoombaInfo> roombaInfoList = new ArrayList<>();
        MainPanel panel = new MainPanel(roombaInfoList);
        //SwingUtilities.invokeLater(RoombaViewer::createAndShowGUI);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(panel);
            }
        });

        String comport = "J:\\JonStuff\\Projects\\SmartRoomba\\data\\ForwardBumpTurnLeft.csv";
        ConcurrentLinkedQueue<RoombaInfo> roombaInfoQueue = new ConcurrentLinkedQueue<>();
        SmartRoomba smartRoomba = new SmartRoomba(new RoombaCommPlaybackMode(), comport, 100, false, false, new DataCSVWriter(null), roombaInfoQueue);
        Thread smartRoombaThread = new Thread(smartRoomba);
        smartRoombaThread.start();

        int retry = 0;
        while (retry < 10) {
            RoombaInfo roombaInfo = roombaInfoQueue.poll();
            if (roombaInfo != null) {
                panel.addRoombaInfo(roombaInfo);
                //roombaInfoList.add(roombaInfo);
                roombaUtilities.sleep(100, "waiting for data in RoombaViewer");
            } else {
                roombaUtilities.sleep(500, "waiting for data in RoombaViewer");
                retry++;
            }
        }

    }

    private static void createAndShowGUI(MainPanel panel) {

        System.out.println("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());
        JFrame frame = new JFrame("Smart Roomba");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}

// TODO: add graphic for bumps or other sensor inputs
// TODO: add buttons to step forward and backwards for current roomba (like the arrow keys)
// TODO: add info text for sensor data number
// TODO: add saving a set of sensor data along with positions.
class MainPanel extends JPanel {
    private int millisPerPixel;
    private int roombaPxDiameter;
    private int roombaPxHalfDiameter;
    private int millisGridSpacing;
    private int pixelsPerGrid;
    private Point origin;
    private Point startMove;
    private Point endMove;
    private Point moved = null;
    private List<RoombaInfo> roombaInfoList;
    private int currentRoombaInfoIndex;
    private int[] zoomLevelMillisPerPixel = {1, 2, 3, 4, 5, 10 ,20, 30};
    private int currentZoomLevel = 2;
    private int[] zoomLevelMillisGridSpacing = {25, 50, 75, 100, 200, 300, 600, 1200};
    private NumberFormat formatter;
    private NumberFormat scaleFormatter;

    public MainPanel(List<RoombaInfo> roombaInfoList) {
        this.setFocusable(true);
        this.roombaInfoList = roombaInfoList;
        if (!this.roombaInfoList.isEmpty()) {
            currentRoombaInfoIndex = this.roombaInfoList.size() - 1;
        } else {
            currentRoombaInfoIndex = 0;
        }

        formatter = new DecimalFormat("#0.00");
        scaleFormatter = new DecimalFormat("#0.00");
        setBorder(BorderFactory.createLineBorder(Color.black));

        origin = new Point(getPreferredSize().width / 2, getPreferredSize().height / 2);
        updateForZoomLevel(zoomLevelMillisPerPixel[currentZoomLevel], zoomLevelMillisGridSpacing[currentZoomLevel]);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                startMove = event.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                endMove = event.getPoint();
                moved = new Point(endMove.x - startMove.x, endMove.y - startMove.y);
                origin.translate(moved.x, moved.y);
                startMove = endMove;
                repaint();
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                int change = event.getWheelRotation();
                incrementZoomLevel(change);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                if (keyCode == 37) {
                    incrementCurrentRoombaInfo(-1);
                } else if (keyCode == 39) {
                    incrementCurrentRoombaInfo(1);
                }
            }
        });
    }

    public void updateForZoomLevel(int millisPerPixel, int millisGridSpacing) {
        this.millisPerPixel = millisPerPixel;
        this.millisGridSpacing = millisGridSpacing;
        pixelsPerGrid = millisGridSpacing / this.millisPerPixel;
        roombaPxDiameter = (int)RoombaConstants.WHEELBASE / this.millisPerPixel;
        roombaPxHalfDiameter = roombaPxDiameter / 2;
        repaint();
    }

    public void incrementZoomLevel(int change) {
        currentZoomLevel += change;
        if (currentZoomLevel < 0) {
            currentZoomLevel = 0;
        } else if (currentZoomLevel > zoomLevelMillisPerPixel.length -1) {
            currentZoomLevel = zoomLevelMillisPerPixel.length - 1;
        }
        updateForZoomLevel(zoomLevelMillisPerPixel[currentZoomLevel], zoomLevelMillisGridSpacing[currentZoomLevel]);
    }

    public void addRoombaInfo(RoombaInfo roombaInfo) {
        roombaInfoList.add(roombaInfo);
        currentRoombaInfoIndex = roombaInfoList.size() - 1;
        repaint();
    }

    public void incrementCurrentRoombaInfo(int change) {
        currentRoombaInfoIndex += change;
        if (currentRoombaInfoIndex < 0) {
            currentRoombaInfoIndex = 0;
        } else if (currentRoombaInfoIndex > roombaInfoList.size() -1) {
            currentRoombaInfoIndex = roombaInfoList.size() - 1;
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800,800);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        paintGrid(graphics);
        paintRoombas(graphics);
        paintInfoText(graphics);
        paintScaleText(graphics);
    }

    private void paintInfoText(Graphics graphics) {
        int positionX = 5;
        int positionY = 5;
        int angleX = 5;
        int angleY = positionY + graphics.getFontMetrics().getHeight();
        String positionString = "Roomba position: " + formatter.format(roombaInfoList.get(currentRoombaInfoIndex).getPosition().getPosition().x)
                + ", " + formatter.format(roombaInfoList.get(currentRoombaInfoIndex).getPosition().getPosition().y);
        String angleString = "Roomba angle: " + formatter.format(roombaInfoList.get(currentRoombaInfoIndex).getPosition().getDegrees()) + "\u00b0";
        graphics.setColor(Color.WHITE);
        graphics.fillRect(positionX, positionY, graphics.getFontMetrics().stringWidth(positionString) + 4, graphics.getFontMetrics().getHeight());
        graphics.fillRect(angleX, angleY, graphics.getFontMetrics().stringWidth(angleString) + 4, graphics.getFontMetrics().getHeight());
        graphics.setColor(Color.BLACK);
        graphics.drawString(positionString, positionX + 2, positionY + graphics.getFontMetrics().getHeight() - 3);
        graphics.drawString(angleString, angleX + 2, angleY + graphics.getFontMetrics().getHeight() - 3);
    }

    private void paintScaleText(Graphics graphics) {
        int scaleX = 5;
        int scaleGap = -3;
        int scaleBarHeight = 10;
        int scaleHorizontalPad = 30;
        int scaleVerticalPad = 6;
        int scaleY = this.getSize().height - (((graphics.getFontMetrics().getHeight() - 3) * 4) + (2 * scaleBarHeight) + scaleVerticalPad + 5);
        String topScaleString = "Millimeters";
        String bottomScaleString = "Meters";
        graphics.setColor(Color.WHITE);
        graphics.fillRect(scaleX, scaleY, (5 * pixelsPerGrid) + scaleHorizontalPad, ((graphics.getFontMetrics().getHeight() - 3) * 4) + (2 * scaleBarHeight) + scaleVerticalPad);
        graphics.setColor(Color.BLACK);
        graphics.drawString(topScaleString, scaleX + (((5 * pixelsPerGrid) + scaleHorizontalPad - graphics.getFontMetrics().stringWidth(topScaleString)) / 2),
                scaleY + graphics.getFontMetrics().getHeight() - 3);
        int millisPerGrid = pixelsPerGrid * millisPerPixel;
        for (int i = 0; i < 6; i++) {
            String valueString = String.valueOf(millisPerGrid * i);
            graphics.drawString(valueString, scaleX + (scaleHorizontalPad / 2) - (graphics.getFontMetrics().stringWidth(valueString) / 2) + (i * pixelsPerGrid), scaleY + (graphics.getFontMetrics().getHeight() * 2) - 2);
            graphics.fillRect(scaleX + (i * pixelsPerGrid) + (scaleHorizontalPad / 2), scaleY + scaleGap + (graphics.getFontMetrics().getHeight() * 2) + (scaleVerticalPad / 2), 1, scaleBarHeight);
        }
        graphics.fillRect(scaleX + (scaleHorizontalPad / 2), scaleY + scaleGap + (graphics.getFontMetrics().getHeight() * 2) + (scaleBarHeight + (scaleVerticalPad) / 2), (5 * pixelsPerGrid) + 1, 1);
        int maxValue = 5 * millisPerGrid;
        int increment = 1000;
        while (increment > maxValue) {
            if (increment == 250) {
                increment = 100;
            } else {
                increment = increment / 2;
            }
        }
        for (int i = 0; i <= maxValue; i += increment) {
            double value = (double)i / 1000;
            String valueString;
            if (value == Math.floor(value)) {
                valueString = String.valueOf((int)value);
            } else {
                valueString = scaleFormatter.format((double)i / 1000);
            }
            graphics.drawString(valueString, scaleX + (scaleHorizontalPad / 2) - (graphics.getFontMetrics().stringWidth(valueString) / 2) + (i / millisPerPixel), scaleY + (graphics.getFontMetrics().getHeight() * 3) + scaleBarHeight + 5);
            graphics.fillRect(scaleX + (i / millisPerPixel) + (scaleHorizontalPad / 2), scaleY + scaleGap + (graphics.getFontMetrics().getHeight() * 2) + (scaleVerticalPad / 2) + scaleBarHeight, 1, scaleBarHeight);
        }
        graphics.drawString(bottomScaleString, scaleX + (((5 * pixelsPerGrid) + scaleHorizontalPad - graphics.getFontMetrics().stringWidth(bottomScaleString)) / 2),
                scaleY + scaleGap + (graphics.getFontMetrics().getHeight() * 5) - 3);
    }

    private void paintRoombas(Graphics graphics) {
        for (RoombaInfo roombaInfo : roombaInfoList) {
            paintRoombaShadow(graphics, roombaInfo);
        }
        RoombaInfo previousRoombaInfo = null;
        for (RoombaInfo roombaInfo : roombaInfoList) {
            paintRoombaPath(graphics, roombaInfo, previousRoombaInfo);
            previousRoombaInfo = roombaInfo;
        }
        for (RoombaInfo roombaInfo : roombaInfoList) {
            paintObstacles(graphics, roombaInfo);
        }
        paintFocusedRoomba(graphics, roombaInfoList.get(currentRoombaInfoIndex));
    }

    private void paintRoombaShadow(Graphics graphics, RoombaInfo roombaInfo) {
        graphics.setColor(Color.LIGHT_GRAY);
        // Get the x and y position of the center of the roomba in pixels.
        int xPos = (int)(roombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x - roombaPxHalfDiameter;
        int yPos = (int)(-1 * roombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y - roombaPxHalfDiameter;
        // Draw the roomba
        graphics.fillOval(xPos, yPos, roombaPxDiameter, roombaPxDiameter);
        graphics.setColor(Color.BLACK);
    }

    private void paintRoombaPath(Graphics graphics, RoombaInfo roombaInfo, RoombaInfo previousRoombaInfo) {
         if (previousRoombaInfo != null) {
            graphics.setColor(Color.BLUE);
            int xPos = (int)(roombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x;
            int yPos = (int)(-1 * roombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y;
            int xPrevPos = (int)(previousRoombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x;
            int yPrevPos = (int)(-1 * previousRoombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y;
            graphics.drawLine(xPos, yPos, xPrevPos, yPrevPos);
            graphics.setColor(Color.BLACK);
        }
    }

    private void paintObstacles(Graphics graphics, RoombaInfo roombaInfo) {
        if (roombaInfo.getSensorData().isBumpLeft() || roombaInfo.getSensorData().isBumpRight()) {
            int xPos = (int)(roombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x - roombaPxHalfDiameter;
            int yPos = (int)(-1 * roombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y - roombaPxHalfDiameter;
            graphics.setColor(Color.RED);
            if (roombaInfo.getSensorData().isBumpLeft()) {
                graphics.drawArc(xPos, yPos, roombaPxDiameter, roombaPxDiameter, (int) roombaInfo.getPosition().getDegrees() + 90, (int) roombaInfo.getPosition().getDegrees() + 90);
            }
            if (roombaInfo.getSensorData().isBumpRight()) {
                graphics.drawArc(xPos, yPos, roombaPxDiameter, roombaPxDiameter, (int) roombaInfo.getPosition().getDegrees(), (int) roombaInfo.getPosition().getDegrees() + 90);
            }
        }
        if (roombaInfo.getSensorData().isLightBumperFrontRight()) {
            graphics.setColor(Color.MAGENTA);
            int xPos = (int)(roombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x;
            int yPos = (int)(-1 * roombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y;
            graphics.fillOval(xPos - (int)(roombaPxHalfDiameter * Math.sin(roombaInfo.getPosition().getRadians() - (Math.PI / 2))),
                    yPos - (int)(roombaPxHalfDiameter * Math.cos(roombaInfo.getPosition().getRadians() - (Math.PI / 2))), 6, 6);
        }
    }

    private void paintFocusedRoomba(Graphics graphics, RoombaInfo roombaInfo) {
        graphics.setColor(Color.BLACK);
        int xPos = (int)(roombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x - roombaPxHalfDiameter;
        int yPos = (int)(-1 * roombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y - roombaPxHalfDiameter;
        // Draw the roomba
        graphics.fillOval(xPos, yPos, roombaPxDiameter, roombaPxDiameter);
        graphics.setColor(Color.RED);
        // Draw a dot and line on the roomba to show which direction it is pointing
        graphics.fillOval(xPos + roombaPxHalfDiameter - (int) (Math.sin(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter),
                yPos + roombaPxHalfDiameter - (int) (Math.cos(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter), 5, 5);
        graphics.drawLine(xPos + roombaPxHalfDiameter, yPos + roombaPxHalfDiameter,
                xPos + roombaPxHalfDiameter - (int) (Math.sin(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter),
                yPos + roombaPxHalfDiameter - (int) (Math.cos(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter));
        paintObstacles(graphics, roombaInfo);
        graphics.setColor(Color.BLACK);
    }

    private void paintGrid(Graphics graphics) {
        Dimension dimension = this.getSize();

        if (origin.x >= 0 && origin.x <= dimension.width && origin.y >= 0 && origin.y <= dimension.height) {
            graphics.fillOval(origin.x - 3, origin.y - 3, 6, 6);
        }

        if (origin.x >= 0 && origin.x <= dimension.width) {
            graphics.fillRect(origin.x - 1, origin.y - (dimension.height / 2), 2, dimension.height);
        }

        if (origin.y >= 0 && origin.y <= dimension.height) {
            graphics.fillRect(origin.x - (dimension.width / 2), origin.y - 1, dimension.width, 2);
        }

        int gridXOffset = origin.x % pixelsPerGrid;
        int gridYOffset = origin.y % pixelsPerGrid;
        for (int x = 0; x <= dimension.width; x += pixelsPerGrid) {
            graphics.drawLine(x + gridXOffset, 0, x + gridXOffset, dimension.height);
        }

        for (int y = 0; y <= dimension.height; y += pixelsPerGrid) {
            graphics.drawLine(0, y + gridYOffset, dimension.width, y + gridYOffset);
        }
    }
}
