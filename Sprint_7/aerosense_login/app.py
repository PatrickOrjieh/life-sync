#Starter code from sports_registrants example
#Github CoPilot was used for small parts while writing the below code

import re
from flask import Flask, render_template, request, redirect
import os
from flask_mysqldb import MySQL

app = Flask(__name__)

app.config['MYSQL_HOST'] = os.getenv('MYSQL_HOST')
app.config['MYSQL_USER'] = os.getenv('MYSQL_USER')
app.config['MYSQL_PASSWORD'] = os.getenv('MYSQL_PASSWORD')
app.config['MYSQL_DB'] = os.getenv('MYSQL_DB')

mysql = MySQL(app)

@app.route('/')
def index():
    errors = {}
    return render_template('index.html', errors=errors)


@app.route('/register', methods=['POST'])
def register():
    #Server side validation
    name = request.form.get('name')
    email = request.form.get('email')
    phone = request.form.get('phone')
    password = request.form.get('password')

    errors = {}

    if not name:
        errors['name'] = "Missing name"
    if not email:
        errors['email'] = "Missing email"
    if not phone:
        errors['phone'] = "Missing phone"
    if not password:
        errors['password'] = "Missing password"
    
    # Email validation
    if not re.match(r"[^@]+@[^@]+\.com$", email):
        errors['email'] = "Invalid email format"
    
    # Phone validation
    if not phone.isdigit():
        errors['phone'] = "Phone number must contain only digits"
    
    # Password validation
    if len(password) < 8:
        errors['password'] = "Password must be at least 8 characters long"
    if not re.search("[A-Z]", password):
        errors['password'] = "Password must contain at least one capital letter"
    if not re.search("[@#$%^&+=]", password):
        errors['password'] = "Password must contain at least one special character"
    
    if errors:
        return render_template('index.html', errors=errors, name=name, email=email, phone=phone, password=password)
    
    cursor = mysql.connection.cursor()
    cursor.execute("Select UserID from users where Email = %s", 
                   (email,))
    result = cursor.fetchall()
    if len(result) != 0:
        cursor.close()
        errors['email'] = "You have already registered"
        return render_template('index.html', errors=errors, name=name, email=email, phone=phone, password=password)  
    cursor.execute("INSERT INTO users (Name, Email, Phone, Password) VALUES (%s, %s, %s, %s)", 
                   (name, email, phone, password)) 
    mysql.connection.commit()
    cursor.close() 
    return redirect('/success')

@app.route('/login')
def login():
    # Render the login page template
    return render_template('login.html')

@app.route('/login', methods=['POST'])
def login_post():
    email = request.form.get('email')
    password = request.form.get('password')

    cursor = mysql.connection.cursor()
    cursor.execute("SELECT * FROM users WHERE Email = %s AND Password = %s", (email, password))
    user = cursor.fetchone()
    cursor.close()

    if user:
        return redirect('/success', error="")
    else:
            # Invalid credentials, show error message
        error = "Invalid email or password"
        return render_template('login.html', error=error)
    
@app.route('/success')
def success():
    return render_template('success.html')

