from flask import jsonify, request
from app import app, db
from app.models import Notification, User
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

        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if not user:
            return jsonify({'error': 'User not found in the database'}), 404

        notifications = Notification.query.filter_by(userID=user.userID).all()

        formatted_notifications = [
            {
                'time': notification.createdAt.strftime("%Y-%m-%d %H:%M:%S"),
                'header': notification.heading,
                'message': notification.message
            }
            for notification in notifications
        ]

        return jsonify(formatted_notifications), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500
