import serial
import struct
import time

# Serial port configuration
SERIAL_PORT = '/dev/ttyUSB0'
BAUD_RATE = 9600

# Function to parse data from PMS7003
def parse_sensor_data(data):
    if len(data) == 32 and data[0] == 0x42 and data[1] == 0x4d:
        frame = struct.unpack('>HHHHHHHHHHHHHH', data[4:])
        pm1_0_cf1 = frame[0]
        pm2_5_cf1 = frame[1]
        pm10_cf1 = frame[2]
        pm1_0_atm = frame[3]
        pm2_5_atm = frame[4]
        pm10_atm = frame[5]
        return pm1_0_cf1, pm2_5_cf1, pm10_cf1, pm1_0_atm, pm2_5_atm, pm10_atm
    else:
        return None

def main():
    print("Starting PMS7003 Sensor Readings")

    # Initialize serial connection
    ser = serial.Serial(SERIAL_PORT, baudrate=BAUD_RATE, timeout=1)

    try:
        while True:
            data = ser.read(32)  # Read 32 bytes of data
            pm_data = parse_sensor_data(data)

            if pm_data:
                print("PM 1.0 (CF=1): {} ug/m3, PM 2.5 (CF=1): {} ug/m3, PM 10 (CF=1): {} ug/m3".format(*pm_data[:3]))
                print("PM 1.0 (ATM): {} ug/m3, PM 2.5 (ATM): {} ug/m3, PM 10 (ATM): {} ug/m3".format(*pm_data[3:]))
            else:
                print("Failed to read data")

            time.sleep(1)

    except KeyboardInterrupt:
        print("Stopping sensor reading")
        ser.close()

if __name__ == "__main__":
    main()
