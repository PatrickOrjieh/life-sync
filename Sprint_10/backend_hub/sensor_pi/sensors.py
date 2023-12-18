import bme680
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
pnconfig.user_id = "aerosense-modelx-pi"
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
sensor = bme680.BME680(bme680.I2C_ADDR_PRIMARY)
sensor.set_humidity_oversample(bme680.OS_2X)
sensor.set_pressure_oversample(bme680.OS_4X)
sensor.set_temperature_oversample(bme680.OS_8X)
sensor.set_filter(bme680.FILTER_SIZE_3)
sensor.set_gas_status(bme680.ENABLE_GAS_MEAS)
sensor.set_gas_heater_temperature(320)
sensor.set_gas_heater_duration(150)
sensor.select_gas_heater_profile(0)

# Thresholds
TEMP_THRESHOLD = 30
HUMIDITY_THRESHOLD = 57
GAS_RESISTANCE_BASELINE = 10000

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

def check_air_quality(temp, humid, gas_res):
    # Calculate VOC
    voc_level = GAS_RESISTANCE_BASELINE / gas_res
    poor_quality = temp > TEMP_THRESHOLD or humid > HUMIDITY_THRESHOLD or voc_level > 1
    return poor_quality

def my_publish_callback(envelope, status):
    # Handle publish response
    if not status.is_error():
        print("Message published successfully")
    else:
        print("Error in publishing message")

try:
    while True:
        if sensor.get_sensor_data():
            temp = sensor.data.temperature
            humid = sensor.data.humidity
            gas_res = sensor.data.gas_resistance
            voc_level = GAS_RESISTANCE_BASELINE / gas_res

            # Construct the payload
            data = {
                'modelNumber': "ModelX",
                'temperature': temp,
                'humidity': humid,
                'voc': voc_level,
                'gas_resistance': gas_res
            }

            if check_air_quality(temp, humid, gas_res):
                print("Poor air quality detected!")
                print(f"Data: {data}")
                trigger_alert()
                # Publish to PubNub
                pubnub.publish().channel('aerosense_channel').message(data).pn_async(my_publish_callback)

        time.sleep(1)

except KeyboardInterrupt:
    GPIO.cleanup()
    pubnub.stop()
