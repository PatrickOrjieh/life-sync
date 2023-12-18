from flask import jsonify, request
from app import app, db
from app.models import User, AsthmaProfile
import firebase_admin
from firebase_admin import auth

@app.route('/api/asthma-profile', methods=['GET'])
def get_asthma_profile():
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if not user:
            return jsonify({'error': 'User not found'}), 404

        asthma_profile = AsthmaProfile.query.filter_by(userID=user.userID).first()
        if asthma_profile:
            return jsonify({
                'personalTrigger': asthma_profile.personalTrigger,
                'asthmaCondition': asthma_profile.asthmaCondition
            }), 200
        else:
            return jsonify({'error': 'Asthma profile not found'}), 404

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token'}), 401
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/asthma-profile', methods=['POST'])
def update_asthma_profile():
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if not user:
            return jsonify({'error': 'User not found'}), 404

        data = request.get_json()
        personal_trigger = data.get('personalTrigger')
        asthma_condition = data.get('asthmaCondition')

        asthma_profile = AsthmaProfile.query.filter_by(userID=user.userID).first()
        if asthma_profile:
            asthma_profile.personalTrigger = personal_trigger
            asthma_profile.asthmaCondition = asthma_condition
            db.session.commit()
            return jsonify({'message': 'Asthma profile updated successfully'}), 200
        else:
            return jsonify({'error': 'Asthma profile not found'}), 404

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token'}), 401
    except Exception as e:
        return jsonify({'error': str(e)}), 500
