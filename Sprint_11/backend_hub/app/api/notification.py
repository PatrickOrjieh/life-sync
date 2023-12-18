from flask import jsonify, request
from app import app, mysql
import firebase_admin
from firebase_admin import auth

@app.route('/api/get_notifications', methods=['GET'])
def get_notifications():
    token = request.headers.get('X-Access-Token')

    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        cursor = mysql.connection.cursor()
        cursor.execute('SELECT userID FROM User WHERE firebaseUID = %s', (firebase_user_id,))
        user_result = cursor.fetchone()

        if not user_result:
            return jsonify({'error': 'User not found in the database'}), 404

        user_id = user_result[0]
        cursor.execute('SELECT createdAt, heading, message FROM Notification WHERE userID = %s', (user_id,))
        notifications = cursor.fetchall()

        formatted_notifications = [
            {
                'time': str(notification[0]),
                'header': notification[1],
                'message': notification[2]
            }
            for notification in notifications
        ]

        return jsonify(formatted_notifications), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500
