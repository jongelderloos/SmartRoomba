# SmartRoomba: Making non WiFi Roombas smart through serial port control.

Credit to the creators of RoombaComm on which the project is based and
forked from.

*RoombaComm: Roomba Control & Communications API, release 0.96
Copyright (c) 2006-9   Tod E. Kurt, tod-at-todbot.com &
                       Paul Bouchier,  bouchier-at-classicnet.net*

### Quick Start for Java on Linux

If you have a Roomba Serial Interface built and the 'roombacomm-java.zip'
library, you can quickly try both out by unzipping this file and running
one of the examples:

% unzip roombacomm.zip
% cd roombacomm
% ./run-it.sh roombacomm.SimpleTest /dev/cu.KeySerial1

Assuming you have a serial device called "/dev/cu.KeySerial1", which you
probably do if you have a Keyspan USA-19HS USB serial adapter.

### Quick Start for Windows

**rxtxSerial.dll Installation**

- You need to copy the rxtxSerial.dll file into the folder containing java.exe
- Identify your Java Runtime Environment's folder. For version 1.6.0, this usually
is c:\Program Files\Java\jre1.6.0_??\. java.exe is usually in the bin folder under the
runtime folder.
example: c:\Program Files\Java\jre1.6.0_01\bin\
- Further help is available on the Wiki at http://www.rxtx.org
- If you don't get rxtxSerial.dll installed in the right place, RoombacommTest will hang when you
press the connect button, command-line apps will hang at startup, and the Windows bluetooth device
icon will flash to connected then back to not connected.

**Bluetooth**

- The serial port implementation is a little different (at least to RXTX)
so use the '-hwhandshake' flag with the programs, if using Bluetooth on
Windows.  See, 'RoomabCommSerial.waitForDSR' for details.
- It may be that bluetooth is problematic when you've been using the device from one computer,
which is still turned on, and you try to use it from another computer. You may have to turn the
other computer off, and/or delete the bluetooth device and re-add it in the Bluetooth devices
window before it will allow opening it.
- Open up the bluetooth Devices window (in the control panel). You can monitor the connected state
there, as well as identify which COM ports are assigned to the bluetooth 1SPP board. The device
should show connected once you've hit the connect button on RoombacommTest, or when any of the
sample programs are active.
- Bluetooth on Windows may not work too well if you select no passcode. Try creating the device
with a passcode of 0000, even if the bluetooth device is set up for no passcode.
- A Philips BGB203 chip in automatic server mode as a bluetooth transceiver, and
Windows XP and Vista, and RXTXlib does not seem reliable - YMMV! Please help if you can.


### Testing with SimpleTest

- Before you jump to running SimpleTest (which actually relies on a lot of SW underneath it)
check that you can actually connect to the COM port you intend to use. Run a terminal emulator
program such as PuTTY, and open up the serial port with the name COMx (whichever port your Roomba
is connected to). Jumper the Tx port to the Rx port. If your USB to Serial adapter has data LEDs,
try hitting keys, and make sure the LEDs blink, and the key you pressed is echoed back. Similarly
for bluetooth. If you can't get a terminal emulator to talk to your interface, SimpleTest has
no hope.
- SimpleTest is a java application which connects to Roomba through a serial port you specify
and sends commands to make it do a few basic things.
- Open a command prompt window and cd to the directory where you unzipped roombacomm
- run the runit.bat file with the name of your com port, e.g.
  > runit COM5.
  Note that COMx is case sensitive - "COM" in Windows COM ports is capitalized
- If you get the dreaded "Couldn't connect to com4" message, you likely either mistyped
the COM port (e.g. didn't capitalize COM), or you have not installed rxtxSerial.dll in the
same directory as the java.exe that you're running.
- If it connects to the COM port, and you have lights on your USB to serial port, they should
start flashing as the program prints the commands it's sending to Roomba. If you have Roomba
connected and powered on, it will move around a little.
- If you're using bluetooth on Windows, you may have to give the -hwhandshake option on
the command line. e.g.
  > runit roombacomm.DriveRealTime COM4 -hwhandshake

