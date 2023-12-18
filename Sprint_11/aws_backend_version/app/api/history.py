from flask import jsonify, request
from app import app, db
from app.models import User, Hub, AirQualityMeasurement
import firebase_admin
from firebase_admin import auth
from datetime import datetime, timedelta
from sqlalchemy import func, cast, DATE

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
        
def get_start_end_of_week(week='current'):
    today = datetime.now().date()
    weekday = today.weekday()

    # Adjust to make Monday the first day of the week
    start_of_week = today - timedelta(days=weekday)
    end_of_week = start_of_week + timedelta(days=6)

    # Adjust for the last week
    if week == 'last':
        start_of_week -= timedelta(days=7)
        end_of_week -= timedelta(days=7)

    return start_of_week, end_of_week


@app.route('/api/history', methods=['GET'])
def get_air_quality_history():
    token = request.headers.get('X-Access-Token')
    week = request.args.get('week', 'current')
    start_of_week, end_of_week = get_start_end_of_week(week)

    if not token:
        return jsonify({'error': 'Firebase ID token is required'}), 401

    try:
        decoded_token = auth.verify_id_token(token)
        firebase_user_id = decoded_token['uid']

        user = User.query.filter_by(firebaseUID=firebase_user_id).first()
        if not user:
            return jsonify({'error': 'User not found in the database'}), 404

        hub = Hub.query.filter_by(userID=user.userID).first()
        if not hub:
            return jsonify({'error': 'No hub associated with this user'}), 404

        week_data = db.session.query(
            cast(AirQualityMeasurement.timestamp, DATE).label('date'),
            func.avg(AirQualityMeasurement.temperature).label('avg_temp'),
            func.avg(AirQualityMeasurement.humidity).label('avg_humidity'),
            func.avg(AirQualityMeasurement.gas_resistance).label('avg_gas'),
            func.avg(AirQualityMeasurement.PM2_5).label('avg_pm2_5'),
            func.avg(AirQualityMeasurement.PM10).label('avg_pm10')
        ).filter(
            AirQualityMeasurement.hubID == hub.hubID,
            cast(AirQualityMeasurement.timestamp, DATE) >= start_of_week,
            cast(AirQualityMeasurement.timestamp, DATE) <= end_of_week
        ).group_by('date').all()

        weekly_scores = []
        total_pm2_5 = 0
        total_voc = 0
        for row in week_data:
            date_object = row.date
            day_of_week = date_object.strftime("%A")  # Format date to day name
            daily_data = {'date': date_object, 'temperature': row.avg_temp, 'humidity': row.avg_humidity,
                        'gas_resistance': row.avg_gas, 'pm2_5': row.avg_pm2_5, 'pm10': row.avg_pm10}
            score = calculate_air_quality_score(daily_data)
            weekly_scores.append({'day': day_of_week, 'air_quality_score': score})  # Use 'day' instead of 'date'
            total_pm2_5 += row.avg_pm2_5
            total_voc += row.avg_gas

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
