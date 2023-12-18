import bme680
import time

print("Starting BME680 Sensor Readings")

try:
    # Create sensor object
    sensor = bme680.BME680(bme680.I2C_ADDR_PRIMARY)
except IOError:
    sensor = bme680.BME680(bme680.I2C_ADDR_SECONDARY)

# Set up the sensor for pressure, humidity, temperature, and gas
sensor.set_humidity_oversample(bme680.OS_2X)
sensor.set_pressure_oversample(bme680.OS_4X)
sensor.set_temperature_oversample(bme680.OS_8X)
sensor.set_filter(bme680.FILTER_SIZE_3)
sensor.set_gas_status(bme680.ENABLE_GAS_MEAS)

# Set the heat range, duration, and profile for gas sensor
sensor.set_gas_heater_temperature(320)
sensor.set_gas_heater_duration(150)
sensor.select_gas_heater_profile(0)

try:
    while True:
        if sensor.get_sensor_data():
            temperature = sensor.data.temperature
            humidity = sensor.data.humidity
            pressure = sensor.data.pressure
            # Check if the gas sensor is heat stable
            if sensor.data.heat_stable:
                gas = sensor.data.gas_resistance
                print(f"Temperature: {temperature} C, Humidity: {humidity} %RH, Pressure: {pressure} hPa, Gas: {gas} Ohms")
            else:
                print(f"Temperature: {temperature} C, Humidity: {humidity} %RH, Pressure: {pressure} hPa")
        time.sleep(1)

except KeyboardInterrupt:
    pass