from threading import Thread
from flask import current_app
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from pubnub.callbacks import SubscribeCallback
from pubnub.enums import PNStatusCategory
import os
import mysql.connector
from dotenv import load_dotenv
from datetime import datetime
import json
import requests

load_dotenv()
class MySubscribeCallback(SubscribeCallback):
    def message(self, pubnub, message):
        # Decrypt and process message here
        decrypted_message = message.message
        # print(f"Received message: {decrypted_message}")
        store_data(decrypted_message)

    def status(self, pubnub, status):
        if status.category == PNStatusCategory.PNConnectedCategory:
            print("Connected to PubNub")

def send_fcm_notification(token, title, body):
    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + os.getenv('FCM_SERVER_KEY')
    }
    payload = {
        'notification': {
            'title': title,
            'body': body
        },
        'to': token
    }
    response = requests.post('https://fcm.googleapis.com/fcm/send', headers=headers, json=payload)

    # Debugging information
    print("FCM Response Status Code:", response.status_code)
    print("FCM Response Content:", response.content)

    try:
        return response.json()
    except json.JSONDecodeError:
        print("Error parsing JSON response from FCM server.")
        return None
    
def insert_notification(cursor, userID, heading, message):
    try:
        insert_query = """
            INSERT INTO Notification (userID, heading, message)
            VALUES (%s, %s, %s)
        """
        cursor.execute(insert_query, (userID, heading, message))
    except mysql.connector.Error as err:
        print(f"Error inserting notification: {err}")

# Define thresholds for all parameters
TEMP_THRESHOLD = {"Good": 18, "Moderate": 25, "Bad": 30}
HUMIDITY_THRESHOLD = {"Good": 30, "Moderate": 50, "Bad": 70}
GAS_RESISTANCE_THRESHOLD = {"Good": 4000, "Moderate": 6000, "Bad": 10000}
PM_2_5_THRESHOLD = {"Good": 12, "Moderate": 35.4, "Bad": 55.4}
PM_10_THRESHOLD = {"Good": 54, "Moderate": 154, "Bad": 254}

def calculate_air_quality_score(data):
    # calculate air quality score
    temp_score = score_parameter(data['temperature'], TEMP_THRESHOLD)
    humidity_score = score_parameter(data['humidity'], HUMIDITY_THRESHOLD)
    gas_score = score_parameter(data['gas_resistance'], GAS_RESISTANCE_THRESHOLD, reverse=True)
    pm2_5_score = score_parameter(data['pm2_5'], PM_2_5_THRESHOLD)
    pm10_score = score_parameter(data['pm10'], PM_10_THRESHOLD)

    # Calculate overall air quality score
    total_score = temp_score + humidity_score + gas_score + pm2_5_score + pm10_score
    air_quality_percentage = total_score / 5
    return air_quality_percentage

def score_parameter(value, thresholds, reverse=False):
    # Higher or lower values are better based on reverse flag
    if reverse:
        if value >= thresholds["Good"]:
            return 100
        elif value >= thresholds["Moderate"]:
            return 60
        else:
            return 40
    else:
        if value <= thresholds["Good"]:
            return 100
        elif value <= thresholds["Moderate"]:
            return 60
        else:
            return 40
        
def determine_air_quality_category(percentage):
    if percentage >= 85:
        return "Good"
    elif percentage >= 60:
        return "Moderate"
    elif percentage >= 40:
        return "Bad"
    else:
        return "Very Poor"

def generate_notification(data, category):
    if category == "Good":
        heading = "Good Air Quality"
        message = "The air quality is good. Enjoy the fresh air!"
    elif category == "Moderate":
        heading = "Moderate Air Quality"
        message = "The air quality is moderate. Take necessary precautions if you have respiratory issues."
    elif category == "Bad":
        heading = "Poor Air Quality Alert"
        message = "The air quality is currently poor. Please take necessary precautions."
    elif category == "Very Poor":
        heading = "Severe Air Quality Alert"
        message = "The air quality is very poor! Immediate action is required to protect your health."
    else:
        return None, None

    # Additional logic to tailor the message based on specific parameters
    if data['temperature'] > TEMP_THRESHOLD["Bad"]:
        message += " High temperatures can aggravate respiratory issues."
    if data['humidity'] > HUMIDITY_THRESHOLD["Bad"]:
        message += " High humidity levels can worsen symptoms."
    if data['pm2_5'] > PM_2_5_THRESHOLD["Bad"]:
        message += " Elevated PM2.5 levels detected."
    if data['pm10'] > PM_10_THRESHOLD["Bad"]:
        message += " Elevated PM10 levels detected."

    return heading, message

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

        # Using the modelNumber from the data, get the hubID
        cursor.execute('SELECT hubID FROM Hub WHERE modelNumber = %s', (data['modelNumber'],))
        result = cursor.fetchone()

        if result:
            hubID = result[0]

            # Extract timestamp from data if it exists, otherwise use current time
            timestamp = data.get('timestamp', datetime.now().strftime('%Y-%m-%d %H:%M:%S'))

            # Check if a record with the same hubID and timestamp already exists
            cursor.execute("SELECT COUNT(*) FROM AirQualityMeasurement WHERE hubID = %s AND timestamp = %s", (hubID, timestamp))
            if cursor.fetchone()[0] == 0:
                # Insert data into the database
                query = """
                    INSERT INTO AirQualityMeasurement (
                        hubID, PM1, PM2_5, PM10, VOC, temperature, humidity, gas_resistance, pollenCount, timestamp
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                """
                values = (
                    hubID, data['pm1'], data['pm2_5'], data['pm10'], data['voc'], data['temperature'],
                    data['humidity'], data['gas_resistance'], 2, timestamp
                )
                cursor.execute(query, values)
                connection.commit()
                print("Data inserted into database")

                # Calculate air quality score and determine category
                air_quality_percentage = calculate_air_quality_score(data)
                air_quality_category = determine_air_quality_category(air_quality_percentage)

                # Generate and send notification 
                if air_quality_category in ["Good", "Moderate", "Bad", "Very Poor"]:
                    cursor.execute('SELECT userID FROM Hub WHERE hubID = %s', (hubID,))
                    user_result = cursor.fetchone()
                    if user_result:
                        userID = user_result[0]
                        cursor.execute('SELECT fcmToken FROM User WHERE userID = %s', (userID,))
                        fcm_result = cursor.fetchone()
                        if fcm_result:
                            fcm_token = fcm_result[0]
                            heading, message = generate_notification(data, air_quality_category)
                            if heading and message:
                                send_fcm_notification(fcm_token, heading, message)
                                print("Notification sent to user")

                                # Insert notification into the database
                                insert_notification(cursor, userID, heading, message)
                                connection.commit()
                                print("Notification inserted into database")
                            else:
                                print("No notification generated due to insufficient data.")
                        else:
                            print("FCM token not found for user.")
                    else:
                        print("User not found for the given hubID.")
            else:
                print("Duplicate data. Skipping insertion.")
        else:
            print("Hub not found")

    except mysql.connector.Error as err:
        print(f"Error: {err}")
    finally:
        # Close the cursor and connection
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
