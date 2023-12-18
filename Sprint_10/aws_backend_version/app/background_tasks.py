from threading import Thread
from flask import current_app
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from pubnub.callbacks import SubscribeCallback
from pubnub.enums import PNStatusCategory
import os
import mysql.connector
from dotenv import load_dotenv
import json

load_dotenv()

# PubNub Configuration
# pnconfig = PNConfiguration()
# pnconfig.subscribe_key = os.getenv('PUBNUB_SUBSCRIBE_KEY')
# pnconfig.publish_key = os.getenv('PUBNUB_PUBLISH_KEY')
# pnconfig.user_id = os.getenv('PUBNUB_USER_ID')
# pnconfig.cipher_key = os.getenv('PUBNUB_CIPHER_KEY')
# pubnub = PubNub(pnconfig)

class MySubscribeCallback(SubscribeCallback):
    def message(self, pubnub, message):
        # Decrypt and process message here
        decrypted_message = message.message
        # print(f"Received message: {decrypted_message}")
        store_data(decrypted_message)

    def status(self, pubnub, status):
        if status.category == PNStatusCategory.PNConnectedCategory:
            print("Connected to PubNub")

def store_data(data):
    # Connect to the database
    try:
        connection = mysql.connector.connect(
            host=os.getenv('MYSQL_HOST'),
            user=os.getenv('MYSQL_USER'),
            password=os.getenv('MYSQL_PASSWORD'),
            database=os.getenv('MYSQL_DB_NAME')
        )
        cursor = connection.cursor(buffered=True)  # Use buffered cursor
        print("Connected to database")
    except Exception as e:
        print(f"Error connecting to database: {e}")
        return

    try:
        # Using the modelNumber from the data, get the hubID
        cursor.execute('SELECT hubID FROM Hub WHERE modelNumber = %s', (data['modelNumber'],))
        result = cursor.fetchone()
        if result:
            hubID = result[0]
            # Insert data into the database
            query = """
                INSERT INTO AirQualityMeasurement (
                    hubID, PM1, PM2_5, PM10, VOC, temperature, humidity, gas_resistance, pollenCount
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            values = (
                hubID, 1, 2.5, 10, data['voc'], data['temperature'],
                data['humidity'], data['gas_resistance'], 2
            )
            cursor.execute(query, values)
            connection.commit()
        else:
            print("Hub not found")
    except mysql.connector.Error as err:
        print(f"Error: {err}")
    finally:
        cursor.close()
        connection.close()

def start_pubnub_listener(app):
    with app.app_context():
        print("Initializing PubNub Listener")
        # Initialize PubNub inside the context
        pnconfig = PNConfiguration()
        pnconfig.subscribe_key = os.getenv('PUBNUB_SUBSCRIBE_KEY')
        pnconfig.publish_key = os.getenv('PUBNUB_PUBLISH_KEY')
        pnconfig.user_id = os.getenv('PUBNUB_USER_ID')
        pnconfig.cipher_key = os.getenv('PUBNUB_CIPHER_KEY')
        pubnub = PubNub(pnconfig)
        
        try:
            pubnub.add_listener(MySubscribeCallback())
            pubnub.subscribe().channels('aerosense_channel').execute()
            # print("PubNub listener started")
        except Exception as e:
            print(f"Error in PubNub listener: {e}")

def start_background_task(app):
    # print("Starting background task")
    thread = Thread(target=start_pubnub_listener, args=(app,))
    thread.daemon = True
    thread.start()
