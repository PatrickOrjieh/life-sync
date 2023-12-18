from flask import jsonify, request
from app import app, db
from app.models import User, Hub, AirQualityMeasurement
import firebase_admin
from firebase_admin import auth
from sqlalchemy import desc

@app.route('/api/home', methods=['GET'])
def get_air_quality_data():
    # Retrieve Firebase token from the request headers
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        # Verify the Firebase ID token and get the Firebase UID
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        # Fetch the user from the database using the Firebase UID
        user = User.query.filter_by(firebaseUID=firebase_user_id).first()

        if not user:
            return jsonify({'error': 'User not found in the database'}), 404

        # Fetch the user's associated hub from the database
        hub = Hub.query.filter_by(userID=user.userID).first()

        if not hub:
            return jsonify({'error': 'No hub associated with this user in the database'}), 404

        # Fetch the latest air quality measurement for the user's hub
        result = AirQualityMeasurement.query.filter_by(hubID=hub.hubID).order_by(desc(AirQualityMeasurement.timestamp)).first()

        if result:
            response_data = {
                'pm1': result.PM1,
                'pm25': result.PM2_5,
                'pm10': result.PM10,
                'voc_level': result.VOC,
                'temperature': result.temperature,
                'humidity': result.humidity,
                'gas_resistance': result.gas_resistance,
                'pollenCount': result.pollenCount,
                'last_updated': result.timestamp.strftime('%I:%M%p')
            }
            return jsonify(response_data), 200
        else:
            return jsonify({'message': 'No air quality data available for this hub'}), 404

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500