CREATE DATABASE aerosense;

USE aerosense;

CREATE TABLE User (
    userID INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    wristbandID INT
);

CREATE TABLE Wristband (
    wristbandID INT PRIMARY KEY AUTO_INCREMENT,
    model VARCHAR(255),
    batteryLevel INT
);

CREATE TABLE AirQualityMeasurement (
    measurementID INT PRIMARY KEY AUTO_INCREMENT,
    wristbandID INT,
    PM1 DECIMAL(10,2),
    PM2_5 DECIMAL(10,2),
    PM10 DECIMAL(10,2),
    VOC DECIMAL(10,2),
    timestamp DATETIME,
    FOREIGN KEY (wristbandID) REFERENCES Wristband(wristbandID)
);

CREATE TABLE Location (
    locationID INT PRIMARY KEY AUTO_INCREMENT,
    wristbandID INT,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    timestamp DATETIME,
    FOREIGN KEY (wristbandID) REFERENCES Wristband(wristbandID)
);

CREATE TABLE Notification (
    notificationID INT PRIMARY KEY AUTO_INCREMENT,
    measurementID INT,
    message VARCHAR(255),
    timestamp DATETIME,
    FOREIGN KEY (measurementID) REFERENCES AirQualityMeasurement(measurementID)
);


INSERT INTO Wristband (model, batteryLevel) VALUES ('ModelX', 100);