/*
 *  SmartRoomba - RoombaUtilitiesTest
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

package com.jgelderloos.smartroomba.roomba;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoombaUtilitiesTest {
    private RoombaUtilities roombaUtilities;

    @Before
    public void setup() {
        roombaUtilities = new RoombaUtilities();
    }

    @Test
    public void sameSignPositive() {
        Assert.assertTrue(roombaUtilities.isSameSign(5, 1));
    }

    @Test
    public void sameSignNegative() {
        Assert.assertTrue(roombaUtilities.isSameSign(-5, -1));
    }

    @Test
    public void sameSignZero() {
        Assert.assertTrue(roombaUtilities.isSameSign(0, 5));
    }

    @Test
    public void differentSignZero() {
        Assert.assertFalse(roombaUtilities.isSameSign(0, -1));
    }

    @Test
    public void differentSign() {
        Assert.assertFalse(roombaUtilities.isSameSign(5, -1));
    }

    @Test
    public void forwardNoRollover() {
        int lastEncoderCount = 10;
        int currentEncoderCount = 15;

        int encoderCountChange = roombaUtilities.getChangeInEncoderCounts(lastEncoderCount, currentEncoderCount);

        Assert.assertEquals(currentEncoderCount - lastEncoderCount, encoderCountChange);
    }

    @Test
    public void backwardsNoRollover() {
        int lastEncoderCount = 15;
        int currentEncoderCount = 10;

        int encoderCountChange = roombaUtilities.getChangeInEncoderCounts(lastEncoderCount, currentEncoderCount);

        Assert.assertEquals(currentEncoderCount - lastEncoderCount, encoderCountChange);
    }

    @Test
    public void forwardRollover() {
        int lastEncoderCount = RoombaConstants.MAX_ENCODER_COUNT - 5;
        int currentEncoderCount = 5;

        int encoderCountChange = roombaUtilities.getChangeInEncoderCounts(lastEncoderCount, currentEncoderCount);

        Assert.assertEquals(10, encoderCountChange);
    }

    @Test
    public void backwardsRollover() {
        int lastEncoderCount = 5;
        int currentEncoderCount = RoombaConstants.MAX_ENCODER_COUNT - 5;

        int encoderCountChange = roombaUtilities.getChangeInEncoderCounts(lastEncoderCount, currentEncoderCount);

        Assert.assertEquals(-10, encoderCountChange);
    }

}
