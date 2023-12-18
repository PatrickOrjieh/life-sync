import bme680
import serial
import struct
import time
import RPi.GPIO as GPIO
from pubnub.callbacks import SubscribeCallback
from pubnub.pubnub import PubNub
from pubnub.pnconfiguration import PNConfiguration
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

# Initialize PubNub
pnconfig = PNConfiguration()
pnconfig.subscribe_key = os.getenv('PUBNUB_SUBSCRIBE_KEY')
pnconfig.publish_key = os.getenv('PUBNUB_PUBLISH_KEY')
pnconfig.user_id = "aerosense-model-T5"
pnconfig.cipher_key = os.getenv('PUBNUB_CIPHER_KEY')
pubnub = PubNub(pnconfig)

BUZZER_ACTIVE = True

# GPIO setup
LED_PIN = 16
BUZZER_PIN = 24
GPIO.setmode(GPIO.BCM)
GPIO.setup(LED_PIN, GPIO.OUT)
GPIO.setup(BUZZER_PIN, GPIO.OUT)

# BME680 setup
sensor_bme680 = bme680.BME680(bme680.I2C_ADDR_PRIMARY)
sensor_bme680.set_humidity_oversample(bme680.OS_2X)
sensor_bme680.set_pressure_oversample(bme680.OS_4X)
sensor_bme680.set_temperature_oversample(bme680.OS_8X)
sensor_bme680.set_filter(bme680.FILTER_SIZE_3)
sensor_bme680.set_gas_status(bme680.ENABLE_GAS_MEAS)
sensor_bme680.set_gas_heater_temperature(320)
sensor_bme680.set_gas_heater_duration(150)
sensor_bme680.select_gas_heater_profile(0)

# PMS7003 setup
SERIAL_PORT_PMS7003 = '/dev/ttyUSB0'
ser_pms7003 = serial.Serial(SERIAL_PORT_PMS7003, baudrate=9600, timeout=1)

# GPS setup
SERIAL_PORT_GPS = '/dev/ttyS0'
ser_gps = serial.Serial(SERIAL_PORT_GPS, baudrate=9600, timeout=1)

# Thresholds
TEMP_THRESHOLD = 30
HUMIDITY_THRESHOLD = 57
GAS_THRESHOLD  = 10000
PM_2_5_THRESHOLD = 12
PM_10_THRESHOLD = 54

# Function to parse PMS7003 sensor data
def parse_pms7003_data(data):
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

def get_gps_data(serial_port, timeout=60):
    start_time = time.time()
    while True:
        elapsed_time = time.time() - start_time
        if elapsed_time > timeout:
            print("GPS timeout reached. No valid data.")
            return None, None
        gps_data = serial_port.readline().decode('utf-8').strip()
        if gps_data:
            gps_parsed = parse_gps_data(gps_data)
            if gps_parsed:
                print(f"GPS Data Acquired: Latitude: {gps_parsed[0]}, Longitude: {gps_parsed[1]}")
                return gps_parsed
        time.sleep(0.5) 

# PubNub callback class
class MySubscribeCallback(SubscribeCallback):
    def message(self, pubnub, message):
        global BUZZER_ACTIVE
        if 'sound' in message.message:
            BUZZER_ACTIVE = message.message['sound']

# Register PubNub listener
pubnub.add_listener(MySubscribeCallback())
pubnub.subscribe().channels('user_settings_channel').execute()

def beep(repeat):
    if BUZZER_ACTIVE:
        for _ in range(repeat):
            for pulse in range(60):
                GPIO.output(BUZZER_PIN, True)
                time.sleep(0.001)
                GPIO.output(BUZZER_PIN, False)
                time.sleep(0.001)
            time.sleep(0.02)

def trigger_alert():
    beep(5)
    # Blink LED 5 times
    for _ in range(5):
        GPIO.output(LED_PIN, GPIO.HIGH)
        time.sleep(0.5)
        GPIO.output(LED_PIN, GPIO.LOW)
        time.sleep(0.5)

def check_air_quality(temp, humidity, gas_resistance, pm2_5, pm10):
    if (temp > TEMP_THRESHOLD or humidity > HUMIDITY_THRESHOLD or
        gas_resistance < GAS_THRESHOLD or pm2_5 > PM_2_5_THRESHOLD or
        pm10 > PM_10_THRESHOLD):
        return True
    return False

def my_publish_callback(envelope, status):
    # Handle publish response
    if not status.is_error():
        print("Message published successfully")
    else:
        print("Error in publishing message")

def main():
    try:
        while True:
            # Initialize the variables with default values
            temp = None
            humidity = None
            gas_resistance = None
            voc_level = None
            pm2_5 = None
            pm10 = None
            lat = None
            lon = None

            # Attempt to read from BME680 sensor
            if sensor_bme680.get_sensor_data():
                temp = sensor_bme680.data.temperature
                humidity = sensor_bme680.data.humidity
                gas_resistance = sensor_bme680.data.gas_resistance
                voc_level = GAS_THRESHOLD / gas_resistance

            # Read from PMS7003 sensor
            data = ser_pms7003.read(32)
            if data:
                pms_data = parse_pms7003_data(data)
                if pms_data:
                    pm2_5, pm10 = pms_data[1], pms_data[2]

            # Check air quality before attempting to read GPS
            if None not in [temp, humidity, gas_resistance, pm2_5, pm10]:
                if check_air_quality(temp, humidity, gas_resistance, pm2_5, pm10):
                    print("Poor air quality detected, waiting for GPS data.")
                    lat, lon = get_gps_data(ser_gps)

                    if lat is not None and lon is not None:
                        print(f"GPS Data: Latitude: {lat}, Longitude: {lon}")
                        # Construct data payload
                        data = {
                            'modelNumber': "T5",
                            'temperature': temp,
                            'humidity': humidity,
                            'gas_resistance': gas_resistance,
                            'voc': voc_level,
                            'pm2_5': pm2_5,
                            'pm10': pm10,
                            'latitude': lat,
                            'longitude': lon
                        }
                    else:
                        print("No GPS data received within the timeout period.")

                    print(f"Data: {data}")
                    trigger_alert()
                    # Publish to PubNub
                    pubnub.publish().channel('aerosense_channel').message(data).pn_async(my_publish_callback)
            else:
                print("Waiting for valid sensor data...")

            time.sleep(1)

    except KeyboardInterrupt:
        ser_pms7003.close()
        ser_gps.close()
        GPIO.cleanup()

if __name__ == "__main__":
    main()