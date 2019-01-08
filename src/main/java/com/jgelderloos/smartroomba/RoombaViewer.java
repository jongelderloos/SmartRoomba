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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoombaViewer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoombaViewer::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());
        JFrame frame = new JFrame("Smart Roomba");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new MainPanel());
        frame.pack();
        frame.setVisible(true);
    }
}

class MainPanel extends JPanel {
    private Point origin;
    private int gridSpacing;
    private Point startMove;
    private Point endMove;
    private Point moved = null;

    public MainPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        origin = new Point(getPreferredSize().width / 2, getPreferredSize().height / 2);
        gridSpacing = 20;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                startMove = event.getPoint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
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

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400,500);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

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

        int gridXOffset = origin.x % gridSpacing;
        int gridYOffset = origin.y % gridSpacing;
        for (int x = 0; x <= dimension.width; x += gridSpacing) {
            graphics.drawLine(x + gridXOffset, 0, x + gridXOffset, dimension.height);
        }

        for (int y = 0; y <= dimension.height; y += gridSpacing) {
            graphics.drawLine(0, y + gridYOffset, dimension.width, y + gridYOffset);
        }

    }
}
