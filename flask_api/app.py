from flask import Flask, request, jsonify
import joblib
import numpy as np
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # This allows all domains to access your Flask server

#use ngrok http 5000
# Load model and encoder
model = joblib.load("mental_disorder_model.pkl")
encoder = joblib.load("encoder.pkl")

classifier = model.best_estimator_                # Get the best model from GridSearchCV

@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json()
    print("Received data:", data)
    features = [
     "age", "feeling_nervous", "panic", "breathing_rapidly", "sweating", "trouble_in_concentration", "having_trouble_in_sleeping", "having_trouble_with_work", "hopelessness", "anger", "over_react", "change_in_eating", "suicidal_thought", "feeling_tired", "close_friend", "social_media_addiction", "weight_gain", "introvert", "popping_up_stressful_memory", "having_nightmares", "avoids_people_or_activities", "feeling_negative", "trouble_concentrating", "blamming_yourself", "hallucinations", "repetitive_behaviour", "seasonally", "increased_energy"
]

    input_list = [int(data[feature]) for feature in features]  # Already 0/1 in Android
    input_array = np.array(input_list).reshape(1, -1)

    prediction = classifier.predict(input_array)[0]
    predicted_label = encoder.inverse_transform([prediction])[0]  # Decode class if needed

    return jsonify({"prediction": predicted_label})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
