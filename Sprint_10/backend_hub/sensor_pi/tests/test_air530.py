#I couldn't get the data from the gps module
#tried reading documentation and different methods but it didn't work

import serial
import time

def read_gps(serial_port):
    ser = serial.Serial(serial_port, 9600, timeout=0)
    ser.flush()

    while True:
        gps_data = ser.readline().decode('utf-8')
        if gps_data.startswith('$GPGGA'):
            return gps_data
        time.sleep(0.1)

if __name__ == "__main__":
    serial_port = "/dev/ttyAMA0"  #also chnaged it to ttyS0
    while True:
        print(read_gps(serial_port))
        time.sleep(5)