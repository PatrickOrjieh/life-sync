from flask import request, jsonify
from app import app, db
from app.models import User, Hub, UserSetting, AsthmaProfile
from werkzeug.security import generate_password_hash
import firebase_admin
from firebase_admin import auth, credentials
import re

# Initialize Firebase
cred = credentials.Certificate("/var/www/Aerosense/Aerosense/aerosense.json")
firebase_admin.initialize_app(cred)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/api/register', methods=['POST'])
def register_user():
    data = request.get_json()
    token = data.get('firebaseToken')
    model_number = data.get('modelNumber')
    fcm_token = data.get('fcmToken')

    if not token or not model_number:
        return jsonify({"error": "Missing required fields"}), 400

    try:
        # Verify the Firebase ID token and get the user's info
        decoded_token = auth.verify_id_token(token)
        uid = decoded_token['uid']
        email = decoded_token['email']
        name = decoded_token.get('name', email)

        # Check if user already exists
        existing_user = User.query.filter_by(firebaseUID=uid).first()
        if existing_user:
            return jsonify({"error": "User already exists"}), 400

        # Create new user
        new_user = User(name=name, email=email, firebaseUID=uid, fcmToken=fcm_token)
        db.session.add(new_user)
        db.session.commit()

        # Create a new hub associated with the user
        new_hub = Hub(modelNumber=model_number, userID=new_user.userID, batteryLevel=100)
        db.session.add(new_hub)
        db.session.commit()

        #insert into user_settings the default settings
        new_user_settings = UserSetting(userID=new_user.userID, notificationFrequency="only when critical", vibration=True, sound=True)
        db.session.add(new_user_settings)
        db.session.commit()

        # Insert default asthma profile
        new_asthma_profile = AsthmaProfile(userID=new_user.userID, personalTrigger="Fumes", asthmaCondition="Mild intermittent asthma")
        db.session.add(new_asthma_profile)
        db.session.commit()

        return jsonify({"message": "User registered successfully", "userID": new_user.userID}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/login2', methods=['POST'])
def login_user2():
    data = request.get_json()
    email = data.get('email', '').strip()

    if not email:
        return jsonify({"error": "Email is required"}), 400
    if not re.match(r"[^@]+@[^@]+\.[^@]+", email):
        return jsonify({"error": "Invalid email format"}), 400

    try:
        user_record = auth.get_user_by_email(email)
        firebase_user_id = user_record.uid

        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if user is None:
            return jsonify({'error': 'User does not exist'}), 400

        return jsonify({"message": "User logged in successfully", "firebaseUID": firebase_user_id}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 400
    
@app.route('/api/login', methods=['POST'])
def login_user():
    data = request.get_json()
    id_token = data.get('idToken')

    if not id_token:
        return jsonify({'error': 'ID token is required'}), 400

    try:
        # Verify the Firebase ID token
        decoded_token = auth.verify_id_token(id_token)
        firebase_user_id = decoded_token['uid']

        # Retrieve user data from database
        user = User.query.filter_by(firebaseUID=firebase_user_id).first()

        if user is None:
            return jsonify({'error': 'User does not exist'}), 400

        return jsonify({"message": "User logged in successfully", "firebaseUID": firebase_user_id}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 400
