from flask import jsonify, request
from app import app, db
from app.models import Location
import firebase_admin
from firebase_admin import auth
from datetime import datetime

@app.route('/api/locations', methods=['GET'])
def get_all_hub_locations():
    # Retrieve the Firebase token from the request headers
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        # Verify the Firebase ID token
        auth.verify_id_token(token)

        # Fetch location data for all hubs for the current day
        today_date = datetime.now().date()
        locations = Location.query.filter(Location.createdAt >= today_date).all()

        response_data = [{'latitude': loc.latitude, 'longitude': loc.longitude} for loc in locations]
        return jsonify(response_data), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500
