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
    return render_template('login.html', errors=errors)

@app.route('/register')
def register():
    # Render the register page template
    return render_template('index.html')

@app.route('/register', methods=['POST', 'GET'])
def register_method():
    #Server side validation
    name = request.form.get('name')
    email = request.form.get('email')
    password = request.form.get('password')
    confirm = request.form.get('confirm')

    errors = {}

    if not name:
        errors['name'] = "Missing name"
    if not email:
        errors['email'] = "Missing email"
    if not password:
        errors['password'] = "Missing password"
    if not confirm:
        errors['confirm'] = "Missing confirm password"
    
    # Email validation
    if not re.match(r"[^@]+@[^@]+\.com$", email):
        errors['email'] = "Invalid email format"
    
    # Password validation
    if len(password) < 8:
        errors['password'] = "Password must be at least 8 characters long"
    if not re.search("[A-Z]", password):
        errors['password'] = "Password must contain at least one capital letter"
    if not re.search("[@!#$%^&+=]", password):
        errors['password'] = "Password must contain at least one special character"
    if not re.search("[0-9]", password):
        errors['password'] = "Password must contain at least one number"

    # Confirm password validation
    if password != confirm:
        errors['confirm'] = "Passwords do not match"
    
    if errors:
        return render_template('index.html', errors=errors, name=name, email=email, password=password, confirm=confirm)
    
    cursor = mysql.connection.cursor()
    cursor.execute("Select UserID from User where Email = %s", 
                   (email,))
    result = cursor.fetchall()
    if len(result) != 0:
        cursor.close()
        errors['email'] = "You have already registered"
        return render_template('index.html', errors=errors, name=name, email=email, password=password, confirm=confirm)  
    cursor.execute("INSERT INTO User (Name, Email, Password) VALUES (%s, %s, %s)", 
                   (name, email, password)) 
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
    cursor.execute("SELECT * FROM User WHERE Email = %s AND Password = %s", (email, password))
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

