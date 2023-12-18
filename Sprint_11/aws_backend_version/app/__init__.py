from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from dotenv import load_dotenv
import os

load_dotenv()
basedir = os.path.abspath(os.path.dirname(__file__))
load_dotenv(os.path.join(basedir, '/var/www/Aerosense/Aerosense/.env'))

app = Flask(__name__)

# Configure the SQLAlchemy part of the app instance
app.config['SQLALCHEMY_DATABASE_URI'] = f"mysql+pymysql://{os.getenv('MYSQL_USER')}:{os.getenv('MYSQL_PASSWORD')}@{os.getenv('MYSQL_HOST')}/{os.getenv('MYSQL_DB_NAME')}"
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Print the database URI for debugging
print("SQLAlchemy Database URI:", app.config['SQLALCHEMY_DATABASE_URI'])

# Create the SQLAlchemy db instance
db = SQLAlchemy(app)

# Import the parts of our application
from app.api import user
from app.api import data
from app.api import hub
from app.api import pollen
from app.api import location
from app.api import profile
from app.api import history
from app.api import notification

# Start background tasks if needed
from .background_tasks import start_background_task
start_background_task(app)
