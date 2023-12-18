from flask import Flask, request, jsonify
import os
import re
from flask_mysqldb import MySQL
from werkzeug.security import generate_password_hash, check_password_hash

from dotenv import load_dotenv

load_dotenv('.env')

app = Flask(__name__)

app.config['MYSQL_HOST'] = os.getenv('MYSQL_HOST')
app.config['MYSQL_USER'] = os.getenv('MYSQL_USER')
app.config['MYSQL_PASSWORD'] = os.getenv('MYSQL_PASSWORD')
app.config['MYSQL_DB'] = os.getenv('MYSQL_DB')

mysql = MySQL(app)

@app.route('/')
def index():
    return 'Hello World!'

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()

    #basic input validation
    if not all(key in data for key in ('name', 'email', 'password', 'confirmPassword', 'model')):
        return jsonify({'error': 'Missing parameters'}), 400
    
    if data['password'] != data['confirmPassword']:
        return jsonify({'error': 'Passwords do not match'}), 400

    # validate email format
    if not re.match(r"[^@]+@[^@]+\.[^@]+", data['email']):
        return jsonify({'error': 'Invalid email format'}), 400

    # validate password length
    if len(data['password']) < 8:
        return jsonify({'error': 'Password must be at least 8 characters long'}), 400

    # hash the password
    hashed_password = generate_password_hash(data['password'], method='scrypt')

    cursor = mysql.connection.cursor()

    # check if email exists
    cursor.execute("SELECT email FROM User WHERE email = %s", (data['email'],))
    if cursor.fetchone():
        cursor.close()
        return jsonify({'error': 'Email already exists'}), 409

    # Look for the wristband based on provided model
    cursor.execute("SELECT wristbandID FROM Wristband WHERE model = %s", (data['model'],))
    wristband = cursor.fetchone()
    if wristband is None:
        cursor.close()
        return jsonify({'error': 'Wristband model not found'}), 404
    
    try:
        sql = "INSERT INTO User (name, email, password, wristbandID) VALUES (%s, %s, %s, %s)"
        cursor.execute(sql, (data['name'], data['email'], hashed_password, wristband['wristbandID']))
        mysql.connection.commit()
    except Exception as e:
        cursor.close()
        return jsonify({'error': 'An error occurred: ' + str(e)}), 400
    
    cursor.close()
    return jsonify({'message': 'Registration successful'}), 201
