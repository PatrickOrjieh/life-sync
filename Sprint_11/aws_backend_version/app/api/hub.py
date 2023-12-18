from flask import jsonify, request, abort
from app import app, db
from app.models import UserSetting, User
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from dotenv import load_dotenv
from firebase_admin import auth, exceptions
import os

# Load environment variables form .env file
load_dotenv()
basedir = os.path.abspath(os.path.dirname(__file__))
load_dotenv(os.path.join(basedir, '/var/www/Aerosense/Aerosense/.env'))

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
    user = User.query.filter_by(firebaseUID=firebase_uid).first()
    return user.userID if user else None

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

        settings = UserSetting.query.filter_by(userID=user_id).first()

        if settings:
            return jsonify({
                "settingID": settings.settingID,
                "userID": settings.userID,
                "notificationFrequency": settings.notificationFrequency,
                "vibration": settings.vibration,
                "sound": settings.sound,
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

        settings = UserSetting.query.filter_by(userID=user_id).first()

        if settings:
            settings.notificationFrequency = notification_frequency
            settings.vibration = vibration
            settings.sound = sound
            db.session.commit()

            message = {
                'userID': user_id,
                'notificationFrequency': notification_frequency,
                'vibration': vibration,
                'sound': sound
            }
            publish_message('user_settings_channel', message)

            return jsonify({"message": "Settings updated successfully"}), 200
        else:
            return jsonify({"error": "Settings not found"}), 404
    except Exception as e:
        abort(500, description=str(e))