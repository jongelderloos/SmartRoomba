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
import com.jgelderloos.smartroomba.utilities.DataCSV;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        SmartRoomba smartRoomba = new SmartRoomba(new RoombaCommPlaybackMode(), comport, 100, false, false, new DataCSV(null), roombaInfoQueue);
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

    public MainPanel(List<RoombaInfo> roombaInfoList) {
        this.roombaInfoList = roombaInfoList;

        setBorder(BorderFactory.createLineBorder(Color.black));
        origin = new Point(getPreferredSize().width / 2, getPreferredSize().height / 2);
        millisPerPixel = 5;
        millisGridSpacing = 100;
        pixelsPerGrid = millisGridSpacing / millisPerPixel;
        roombaPxDiameter = (int)RoombaConstants.WHEELBASE / millisPerPixel;
        roombaPxHalfDiameter = roombaPxDiameter / 2;

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
    }

    public void addRoombaInfo(RoombaInfo roombaInfo) {
        roombaInfoList.add(roombaInfo);
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
        paintRoomba(graphics);
    }

    private void paintRoomba(Graphics graphics) {
        for (RoombaInfo roombaInfo : roombaInfoList) {
            // Get the x and y position of the center of the roomba in pixels.
            int xPos = (int)(roombaInfo.getPosition().getPosition().x / millisPerPixel) + origin.x - roombaPxHalfDiameter;
            int yPos = (int)(-1 * roombaInfo.getPosition().getPosition().y / millisPerPixel) + origin.y - roombaPxHalfDiameter;
            // Draw the roomba
            graphics.fillOval(xPos, yPos, roombaPxDiameter, roombaPxDiameter);
            graphics.setColor(Color.RED);
            // Draw a dot and line on the roomba to show which direction it is pointing
            graphics.fillOval(xPos + roombaPxHalfDiameter - (int)(Math.sin(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter),
                    yPos + roombaPxHalfDiameter - (int)(Math.cos(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter), 5, 5);
            graphics.drawLine(xPos + roombaPxHalfDiameter, yPos + roombaPxHalfDiameter,
                    xPos + roombaPxHalfDiameter - (int)(Math.sin(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter),
                    yPos + roombaPxHalfDiameter - (int)(Math.cos(roombaInfo.getPosition().getRadians()) * roombaPxHalfDiameter));
            graphics.setColor(Color.BLACK);
        }
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
