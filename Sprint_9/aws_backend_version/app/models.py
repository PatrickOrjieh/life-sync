from app import db

class User(db.Model):
    __tablename__ = 'User'
    userID = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)
    email = db.Column(db.String(255), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    firebaseUID = db.Column(db.String(255), nullable=False)
    createdAt = db.Column(db.DateTime)
    updatedAt = db.Column(db.DateTime)

    # Relationship
    hubs = db.relationship('Hub', backref='user', lazy=True)
    settings = db.relationship('UserSetting', backref='user', lazy=True)

class Hub(db.Model):
    __tablename__ = 'Hub'
    hubID = db.Column(db.Integer, primary_key=True)
    modelNumber = db.Column(db.String(255), nullable=False)
    userID = db.Column(db.Integer, db.ForeignKey('User.userID'), nullable=False)
    batteryLevel = db.Column(db.Integer, nullable=False)

    # Relationship
    locations = db.relationship('Location', backref='hub', lazy=True)
    measurements = db.relationship('AirQualityMeasurement', backref='hub', lazy=True)

class UserSetting(db.Model):
    __tablename__ = 'UserSetting'
    settingID = db.Column(db.Integer, primary_key=True)
    userID = db.Column(db.Integer, db.ForeignKey('User.userID'), nullable=False)
    notificationFrequency = db.Column(db.String(255), nullable=False)
    vibration = db.Column(db.Boolean, nullable=False)
    sound = db.Column(db.Boolean, nullable=False)

class AirQualityMeasurement(db.Model):
    __tablename__ = 'AirQualityMeasurement'
    measurementID = db.Column(db.Integer, primary_key=True)
    hubID = db.Column(db.Integer, db.ForeignKey('Hub.hubID'), nullable=False)
    PM1 = db.Column(db.Float)
    PM2_5 = db.Column(db.Float)
    PM10 = db.Column(db.Float)
    VOC = db.Column(db.Float)
    temperature = db.Column(db.Float)
    humidity = db.Column(db.Float)
    gas_resistance = db.Column(db.Float)
    pollenCount = db.Column(db.Integer)
    timestamp = db.Column(db.DateTime, nullable=False)

class Notification(db.Model):
    __tablename__ = 'Notification'
    notificationID = db.Column(db.Integer, primary_key=True)
    userID = db.Column(db.Integer, db.ForeignKey('User.userID'), nullable=False)
    message = db.Column(db.Text, nullable=False)
    isRead = db.Column(db.Boolean, nullable=False, default=False)
    createdAt = db.Column(db.DateTime)

class AsthmaProfile(db.Model):
    __tablename__ = 'AsthmaProfile'
    profileID = db.Column(db.Integer, primary_key=True)
    userID = db.Column(db.Integer, db.ForeignKey('User.userID'), nullable=False)
    personalTrigger = db.Column(db.String(255))
    asthmaCondition = db.Column(db.String(255))
    createdAt = db.Column(db.DateTime)
    updatedAt = db.Column(db.DateTime)

class Location(db.Model):
    __tablename__ = 'Location'
    locationID = db.Column(db.Integer, primary_key=True)
    hubID = db.Column(db.Integer, db.ForeignKey('Hub.hubID'), nullable=False)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    createdAt = db.Column(db.DateTime)


