from flask import jsonify, request
from app import app, mysql
import firebase_admin
from firebase_admin import auth
from datetime import datetime, timedelta

# Define thresholds for all parameters
TEMP_THRESHOLD = {"Good": 18, "Moderate": 25, "Bad": 30}
HUMIDITY_THRESHOLD = {"Good": 30, "Moderate": 50, "Bad": 70}
GAS_RESISTANCE_THRESHOLD = {"Good": 4000, "Moderate": 6000, "Bad": 10000}
PM_2_5_THRESHOLD = {"Good": 12, "Moderate": 35.4, "Bad": 55.4}
PM_10_THRESHOLD = {"Good": 54, "Moderate": 154, "Bad": 254}

def calculate_air_quality_score(data):
    # calculate air quality score
    temp_score = score_parameter(data['temperature'], TEMP_THRESHOLD)
    humidity_score = score_parameter(data['humidity'], HUMIDITY_THRESHOLD)
    gas_score = score_parameter(data['gas_resistance'], GAS_RESISTANCE_THRESHOLD, reverse=True)
    pm2_5_score = score_parameter(data['pm2_5'], PM_2_5_THRESHOLD)
    pm10_score = score_parameter(data['pm10'], PM_10_THRESHOLD)

    # Calculate overall air quality score
    total_score = temp_score + humidity_score + gas_score + pm2_5_score + pm10_score
    air_quality_percentage = total_score / 5
    return air_quality_percentage

def score_parameter(value, thresholds, reverse=False):
    # Higher or lower values are better based on reverse flag
    if reverse:
        if value >= thresholds["Good"]:
            return 100
        elif value >= thresholds["Moderate"]:
            return 60
        else:
            return 40
    else:
        if value <= thresholds["Good"]:
            return 100
        elif value <= thresholds["Moderate"]:
            return 60
        else:
            return 40

@app.route('/api/history', methods=['GET'])
def get_air_quality_history():
    token = request.headers.get('X-Access-Token')
    week = request.args.get('week', 'current')

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
        cursor.execute('SELECT hubID FROM Hub WHERE userID = %s', (user_id,))
        hub_result = cursor.fetchone()

        if not hub_result:
            return jsonify({'error': 'No hub associated with this user'}), 404

        hub_id = hub_result[0]
        today = datetime.now().date()
        start_of_week = today - timedelta(days=today.weekday() + 8) if week == 'last' else today - timedelta(days=today.weekday() + 1)

        cursor.execute('''SELECT DATE(timestamp) as date, AVG(temperature) as avg_temp, AVG(humidity) as avg_humidity, 
                          AVG(gas_resistance) as avg_gas, AVG(PM2_5) as avg_pm2_5, AVG(PM10) as avg_pm10
                          FROM AirQualityMeasurement
                          WHERE hubID = %s AND DATE(timestamp) >= %s
                          GROUP BY DATE(timestamp)''', (hub_id, start_of_week))
        week_data = cursor.fetchall()

        weekly_scores = []
        total_pm2_5 = 0
        total_voc = 0
        for row in week_data:
            date_object = row[0]
            day_of_week = date_object.strftime("%A")
            daily_data = {'date': date_object, 'temperature': row[1], 'humidity': row[2],
                          'gas_resistance': row[3], 'pm2_5': row[4], 'pm10': row[5]}
            score = calculate_air_quality_score(daily_data)
            weekly_scores.append({'day': day_of_week, 'air_quality_score': score})  # Use 'day' instead of 'date'
            total_pm2_5 += row[4]
            total_voc += row[3]

        overall_avg_score = sum([day['air_quality_score'] for day in weekly_scores]) / len(weekly_scores)
        overall_avg_pm2_5 = total_pm2_5 / len(weekly_scores)
        overall_avg_voc = 10000/(total_voc / len(weekly_scores))

        response_data = {
            'weekly_scores': weekly_scores,
            'overall_avg_score': overall_avg_score,
            'overall_avg_pm2_5': overall_avg_pm2_5,
            'overall_avg_voc': overall_avg_voc
        }

        return jsonify(response_data), 200

    except firebase_admin.exceptions.FirebaseError:
        return jsonify({'error': 'Invalid Firebase ID token provided'}), 401
    except Exception as e:
        return jsonify({'error': 'An unexpected error occurred: ' + str(e)}), 500
