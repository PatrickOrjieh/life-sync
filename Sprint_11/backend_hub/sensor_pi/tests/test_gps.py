import serial
import time

# Function to parse GNGGA or GPRMC sentences for latitude and longitude
def parse_gps_data(data):
    parts = data.split(',')
    
    if data.startswith('$GNGGA') or data.startswith('$GPRMC'):
        latitude = parts[2]
        longitude = parts[4]
        
        if latitude and longitude:
            # Convert to decimal degrees
            lat_deg = float(latitude[:2])
            lat_min = float(latitude[2:])
            lon_deg = float(longitude[:3])
            lon_min = float(longitude[3:])

            lat = lat_deg + (lat_min/60.0)
            lon = lon_deg + (lon_min/60.0)

            if parts[3] == 'S':
                lat = -lat
            if parts[5] == 'W':
                lon = -lon

            return lat, lon
    return None

# Configure serial connection
SERIAL_PORT = '/dev/ttyS0'
BAUD_RATE = 9600

def main():
    ser = serial.Serial(SERIAL_PORT, baudrate=BAUD_RATE, timeout=1)
    print("Starting GPS Data Readings")

    try:
        while True:
            data = ser.readline().decode('utf-8').strip()
            if data:
                gps_data = parse_gps_data(data)
                if gps_data:
                    lat, lon = gps_data
                    print(f"Latitude: {lat}, Longitude: {lon}")
            time.sleep(0.5)

    except KeyboardInterrupt:
        print("Stopping GPS Data Reading")
        ser.close()

if __name__ == "__main__":
    main()