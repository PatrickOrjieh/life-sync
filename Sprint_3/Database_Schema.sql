CREATE TABLE Users (
   UserID INT PRIMARY KEY,
   Name VARCHAR(100),
   Email VARCHAR(100),
   Phone VARCHAR(20)
);

CREATE TABLE Wristbands (
   WristbandID INT PRIMARY KEY,
   UserID INT REFERENCES Users(UserID),
   BatteryStatus INT
);

CREATE TABLE AirQualityMeasurements (
   MeasurementID INT PRIMARY KEY,
   WristbandID INT REFERENCES Wristbands(WristbandID),
   PM10 DECIMAL,
   PM2_5 DECIMAL,
   PM1 DECIMAL,
   PM0_3 DECIMAL,
   VOCs DECIMAL,
   Timestamp DATETIME
);

CREATE TABLE Locations (
   LocationID INT PRIMARY KEY,
   WristbandID INT REFERENCES Wristbands(WristbandID),
   Longitude DECIMAL,
   Latitude DECIMAL,
   Timestamp DATETIME
);

CREATE TABLE Notifications (
   NotificationID INT PRIMARY KEY,
   MeasurementID INT REFERENCES AirQualityMeasurements(MeasurementID),
   Type VARCHAR(50),
   Severity VARCHAR(10),
   Timestamp DATETIME
);
