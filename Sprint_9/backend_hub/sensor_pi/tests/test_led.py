import RPi.GPIO as GPIO
import time

LED_PIN = 16

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(LED_PIN, GPIO.OUT)

try:
    while True:
        GPIO.output(LED_PIN, GPIO.HIGH)  # Turn on LED
        time.sleep(1)                    # Wait for 1 second
        GPIO.output(LED_PIN, GPIO.LOW)   # Turn off LED
        time.sleep(1)                    # Wait for 1 second
except KeyboardInterrupt:
    GPIO.cleanup()  # Clean up GPIO settings
