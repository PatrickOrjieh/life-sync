from flask import jsonify, request
from app import app, mysql
import firebase_admin
from firebase_admin import auth

@app.route('/api/asthma-profile', methods=['GET'])
def get_asthma_profile():
    # Retrieve the Firebase token from the request headers
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        # Verify the Firebase ID token and get the user's information
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        # Use the Firebase UID to find the associated user ID in the database
        cursor = mysql.connection.cursor()
        cursor.execute('SELECT userID FROM User WHERE firebaseUID = %s', (firebase_user_id,))
        user_result = cursor.fetchone()

        if not user_result:
            return jsonify({'error': 'User not found in the database'}), 404

        user_id = user_result[0]

        # Fetch the asthma profile associated with the user ID
        cursor.execute('SELECT * FROM AsthmaProfile WHERE userID = %s', (user_id,))
        profile_result = cursor.fetchone()
        cursor.close()

        if profile_result:
            profile_data = dict(zip([column[0] for column in cursor.description], profile_result))
            return jsonify(profile_data), 200
        else:
            return jsonify({'message': 'Asthma profile not found.'}), 404

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500

@app.route('/api/asthma-profile', methods=['POST'])
def update_asthma_profile():
    # Retrieve the Firebase token from the request headers
    token = request.headers.get('X-Access-Token')
    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        # Verify the Firebase ID token and get the user's information
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        # Use the Firebase UID to find the associated user ID in the database
        cursor = mysql.connection.cursor()
        cursor.execute('SELECT userID FROM User WHERE firebaseUID = %s', (firebase_user_id,))
        user_result = cursor.fetchone()

        if not user_result:
            return jsonify({'error': 'User not found in the database'}), 404

        user_id = user_result[0]

        # Get profile data from the request
        data = request.get_json()
        personal_trigger = data.get('personalTrigger')
        asthma_condition = data.get('asthmaCondition')

        # Check if profile exists and update or create accordingly
        cursor.execute('SELECT profileID FROM AsthmaProfile WHERE userID = %s', (user_id,))
        profile_result = cursor.fetchone()

        if profile_result:
            # Update existing profile
            cursor.execute('''UPDATE AsthmaProfile 
                              SET personalTrigger = %s, asthmaCondition = %s
                              WHERE userID = %s''',
                           (personal_trigger, asthma_condition, user_id))
        else:
            # Create new profile
            cursor.execute('''INSERT INTO AsthmaProfile (userID, personalTrigger, asthmaCondition) 
                              VALUES (%s, %s, %s)''',
                           (user_id, personal_trigger, asthma_condition))

        mysql.connection.commit()
        cursor.close()
        return jsonify({'message': 'Asthma profile updated successfully'}), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500
