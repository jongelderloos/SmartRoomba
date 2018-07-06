package com.jgelderloos.smartroomba.roombacomm;

import org.junit.Test;
import static org.junit.Assert.*;

public class RoombaCommSerialTest {
    @Test public void basicTest() {
        RoombaCommSerial roombaSerial = new RoombaCommSerial();
        assertNotNull("Serial should have an interface defined", roombaSerial.getProtocol());
    }
}
