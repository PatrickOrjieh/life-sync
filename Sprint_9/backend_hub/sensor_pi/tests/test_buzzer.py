import RPi.GPIO as GPIO
import time

BUZZER_PIN = 24

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(BUZZER_PIN, GPIO.OUT)

def beep(duration):
    GPIO.output(BUZZER_PIN, GPIO.HIGH)
    time.sleep(duration)
    GPIO.output(BUZZER_PIN, GPIO.LOW)
    time.sleep(duration)

try:
    while True:
        beep(0.5)
except KeyboardInterrupt:
    GPIO.cleanup()
