from flask import jsonify, request, abort
from app import app, mysql
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from dotenv import load_dotenv
from firebase_admin import auth, exceptions
import os

# Load environment variables form .env file
load_dotenv()
basedir = os.path.abspath(os.path.dirname(__file__))
load_dotenv(os.path.join(basedir, 'backend_hub\.env'))


# Configure PubNub
pnconfig = PNConfiguration()
pnconfig.subscribe_key = os.getenv('PUBNUB_SUBSCRIBE_KEY')
pnconfig.publish_key = os.getenv('PUBNUB_PUBLISH_KEY')
pnconfig.user_id = os.getenv('PUBNUB_USER_ID')
pnconfig.cipher_key = os.getenv('PUBNUB_CIPHER_KEY')
pubnub = PubNub(pnconfig)

def publish_message(channel, message):
    pubnub.publish().channel(channel).message(message).sync()

def get_user_id_from_firebase_uid(firebase_uid):
    cursor = mysql.connection.cursor()
    cursor.execute('SELECT userID FROM User WHERE firebaseUID = %s', (firebase_uid,))
    result = cursor.fetchone()
    cursor.close()
    return result[0] if result else None

def get_firebase_uid_from_token():
    token = request.headers.get('X-Access-Token')
    if not token:
        abort(401, description="No authentication token provided.")

    try:
        decoded_token = auth.verify_id_token(token)
        return decoded_token['uid']
    except exceptions.FirebaseError:
        abort(401, description="Invalid or expired authentication token.")

@app.route('/api/settings', methods=['GET'])
def get_user_settings():
    try:
        firebase_uid = get_firebase_uid_from_token()
        user_id = get_user_id_from_firebase_uid(firebase_uid)

        if not user_id:
            abort(404, description="User not found.")

        cursor = mysql.connection.cursor()
        cursor.execute('SELECT * FROM UserSetting WHERE userID = %s', (user_id,))
        settings = cursor.fetchone()
        cursor.close()

        if settings:
            return jsonify({
                "settingID": settings[0],
                "userID": settings[1],
                "notificationFrequency": settings[2],
                "vibration": settings[3],
                "sound": settings[4],
            }), 200
        else:
            return jsonify({"error": "Settings not found"}), 404
    except Exception as e:
        abort(500, description=str(e))

@app.route('/api/settings', methods=['POST'])
def update_user_settings():
    try:
        firebase_uid = get_firebase_uid_from_token()
        user_id = get_user_id_from_firebase_uid(firebase_uid)

        if not user_id:
            abort(404, description="User not found.")

        data = request.get_json()
        notification_frequency = data.get('notificationFrequency')
        vibration = data.get('vibration')
        sound = data.get('sound')

        cursor = mysql.connection.cursor()
        cursor.execute('''
            UPDATE UserSetting 
            SET notificationFrequency = %s, vibration = %s, sound = %s 
            WHERE userID = %s
            ''', (notification_frequency, vibration, sound, user_id))
        mysql.connection.commit()

        message = {
            'userID': user_id,
            'notificationFrequency': notification_frequency,
            'vibration': vibration,
            'sound': sound
        }
        publish_message('user_settings_channel', message)

        cursor.close()
        return jsonify({"message": "Settings updated successfully"}), 200
    except Exception as e:
        abort(500, description=str(e))