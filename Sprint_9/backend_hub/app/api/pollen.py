import requests
from flask import Flask, jsonify, request
from app import app

METEOBLUE_API_KEY = 'Ic74wxGJ03U6Ey12'

@app.route('/api/pollen', methods=['GET'])
def get_pollen_data():
    latitude = request.args.get('lat', '47.558399')
    longitude = request.args.get('lon', '7.57327')
    asl = request.args.get('asl', '279')

    api_endpoint = 'https://my.meteoblue.com/dataset/query?apikey=' + METEOBLUE_API_KEY

    request_body = {
        "units": {
            "temperature": "C",
            "velocity": "km/h",
            "length": "metric",
            "energy": "watts"
        },
        "geometry": {
            "type": "MultiPoint",
            "coordinates": [[float(longitude), float(latitude), float(asl)]],
            "locationNames": ["Custom Location"]
        },
        "format": "json",
        "timeIntervals": [
            "2023-01-01T+00:00/2023-12-31T+00:00"
        ],
        "queries": [{
            "domain": "NEMSGLOBAL",
            "gapFillDomain": None,
            "timeResolution": "hourly",
            "codes": [{
                "code": 157,
                "level": "180-0 mb above gnd"
            }]
        }]
    }

    response = requests.post(api_endpoint, json=request_body)

    if response.status_code == 200:
        # Process the response
        data = response.json()
        return jsonify(data), 200
    else:
        # Handle errors
        return jsonify({"error": "Failed to fetch data", "status_code": response.status_code}), response.status_code