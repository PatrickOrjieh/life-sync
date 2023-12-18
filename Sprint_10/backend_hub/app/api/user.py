from flask import request, jsonify
from app import app, mysql
import firebase_admin
from firebase_admin import auth, credentials
from werkzeug.security import generate_password_hash
import re

cred = credentials.Certificate("./aerosense.json")
firebase_admin.initialize_app(cred)

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/api/register', methods=['POST'])
def register_user():
    data = request.get_json()
    token = data.get('firebaseToken')
    model_number = data.get('modelNumber')

    if not token or not model_number:
        return jsonify({"error": "Missing required fields"}), 400

    try:
        # Verify the Firebase ID token and get the user's info
        decoded_token = auth.verify_id_token(token)
        uid = decoded_token['uid']
        email = decoded_token['email']
        name = decoded_token.get('name', email)  # Use email as name if name not provided

        # Check if user already exists
        cursor = mysql.connection.cursor()
        cursor.execute('SELECT * FROM User WHERE firebaseUID = %s', (uid,))
        if cursor.fetchone():
            return jsonify({"error": "User already exists"}), 400

        # Insert new user into MySQL database
        cursor.execute('INSERT INTO User (name, email, firebaseUID) VALUES (%s, %s, %s)', (name, email, uid))
        mysql.connection.commit()
        user_id = cursor.lastrowid

        # Insert the hub associated with the user
        cursor.execute('INSERT INTO Hub (modelNumber, userID, batteryLevel) VALUES (%s, %s, %s)', (model_number, user_id, 100))
        mysql.connection.commit()
        cursor.close()

        # Insert default user settings
        cursor = mysql.connection.cursor()
        cursor.execute('INSERT INTO UserSetting (userID, notificationFrequency, vibration, sound) VALUES (%s, %s, %s, %s)', (user_id, "only when critical", True, True))
        mysql.connection.commit()
        cursor.close()

        return jsonify({"message": "User registered successfully", "userID": user_id}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 400


#there is no need for this route because the firebase user id is already stored in the database
#and firebase handles the login but I will leave it here 
@app.route('/api/login', methods=['POST'])
def login_user():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    # Firebase user login
    try:
        user = auth.get_user_by_email(email)
        firebase_user_id = user.uid
    except Exception as e:
        return jsonify({'error': str(e)}), 400

    # Check if user exists in MySQL database
    cursor = mysql.connection.cursor()
    cursor.execute('SELECT * FROM User WHERE firebaseUID = %s', (firebase_user_id,))
    user = cursor.fetchone()
    cursor.close()

    if user is None:
        return jsonify({'error': 'User does not exist'}), 400

    return jsonify({"message": "User logged in successfully", "firebaseUID": firebase_user_id}), 200

#there is no need for this route because the firebase on the client side handles the logout
@app.route('/api/logout', methods=['POST'])
def logout_user():
    # Instruct the client to clear the Firebase token
    return jsonify({"message": "Logout successful. Please clear the client-side token."}), 200
