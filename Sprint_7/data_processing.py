# Sample code to calculate for each pollutant type (PM10, PM2.5, VOC), an AQI value will be computed

def calculate_aqi_pm10(concentration):
    # The breakpoints and AQI values
    breakpoints = [(0, 20), (20, 40), (40, 50), (50, float('inf'))]
    aqi_values = [(0, 50), (51, 100), (101, 150), (150, float('inf'))]
    
    for (cl, ch), (il, ih) in zip(breakpoints, aqi_values):
        if cl <= concentration <= ch:
            # Using linear interpolation formula: I = [(I_high - I_low) / (C_high - C_low)] * (C - C_low) + I_low
            aqi = ((ih - il) / (ch - cl)) * (concentration - cl) + il
            return aqi
        
def calculate_aqi_pm2_5(concentration):
    # The breakpoints and AQI values
    breakpoints = [(0, 10), (10, 20), (20, 25), (25, float('inf'))]
    aqi_values = [(0, 50), (51, 100), (101, 150), (150, float('inf'))]
    
    for (cl, ch), (il, ih) in zip(breakpoints, aqi_values):
        if cl <= concentration <= ch:
            # Using linear interpolation formula: I = [(I_high - I_low) / (C_high - C_low)] * (C - C_low) + I_low
            aqi = ((ih - il) / (ch - cl)) * (concentration - cl) + il
            return aqi
        
def calculate_aqi_voc(concentration):
    # The breakpoints and AQI values
    breakpoints = [(0, 200), (200, 500), (500, 1000), (1000, float('inf'))]
    aqi_values = [(0, 50), (51, 100), (101, 150), (150, float('inf'))]
    
    for (cl, ch), (il, ih) in zip(breakpoints, aqi_values):
        if cl <= concentration <= ch:
            # Using linear interpolation formula: I = [(I_high - I_low) / (C_high - C_low)] * (C - C_low) + I_low
            aqi = ((ih - il) / (ch - cl)) * (concentration - cl) + il
            return aqi
        
def calculate_aqi(concentration_pm10, concentration_pm2_5, concentration_voc):
    # Calculate the AQI value for PM10
    aqi_pm10 = calculate_aqi_pm10(concentration_pm10)
    # Calculate the AQI value for PM2.5
    aqi_pm2_5 = calculate_aqi_pm2_5(concentration_pm2_5)
    # Calculate the AQI value for VOC
    aqi_voc = calculate_aqi_voc(concentration_voc)
    
    # Determine the overall AQI value
    aqi = max(aqi_pm10, aqi_pm2_5, aqi_voc)
    
    return aqi

# after the AQI value is calculated, based on the worst AQI value, the overall AQI value will be determined
# the overall AQI value will be used to determine the air quality category#
# Air Quality Percentage = (1 - AQI/500) x 100 
def calculate_aq_percentage(aqi):
    aq_percentage = (1 - aqi/500) * 100
    if aq_percentage >= 85:
        print("Good")
    elif aq_percentage >= 70:
        print("Fair")
    elif aq_percentage >= 55:
        print("Poor")
    else:
        print("Very Poor")
    return aq_percentage


# Testing the function
concentration_pm10 = 22
aqi_pm10 = calculate_aqi_pm10(concentration_pm10)
print("PM10 AQI value: ", aqi_pm10)
concentration_pm2_5 = 12
aqi_pm2_5 = calculate_aqi_pm2_5(concentration_pm2_5)
print("PM2.5 AQI value: ", aqi_pm2_5)
concentration_voc = 300
aqi_voc = calculate_aqi_voc(concentration_voc)
print("VOC AQI value: ", aqi_voc)
aqi = calculate_aqi(concentration_pm10, concentration_pm2_5, concentration_voc)
print("Overall AQI value: ", aqi)
aq_percentage = calculate_aq_percentage(aqi)
print("Air Quality Percentage: ", aq_percentage)
