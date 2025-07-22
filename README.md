# Mental Health Assessment App

The **Mental Health Assessment App** is a full-stack application designed to assess mental health, predict potential disorders, and help users locate nearby psychiatrists. It integrates an Android frontend, a Flask backend, and a machine learning pipeline trained on a Kaggle dataset. This README provides an overview, setup instructions, and key details to get started.

## Project Overview
The app consists of three main components:
1. **Android App**: A Java-based mobile app built in Android Studio, featuring a 28-question quiz, disorder prediction, and psychiatrist search via Google Maps.
2. **Flask Backend**: A Python-based server exposing a `/predict` endpoint to deliver mental health disorder predictions using a pre-trained model.
3. **Machine Learning Pipeline**: A Jupyter Notebook (`model_training.ipynb`) for data preprocessing, model training, evaluation, and deployment preparation.

## Features
- **Quiz Interface**: Users answer 28 questions (e.g., age, mental health symptoms) to receive a predicted disorder (e.g., Depression, Anxiety).
- **Prediction**: The Flask backend uses a trained Logistic Regression model to predict disorders based on user inputs.
- **Psychiatrist Search**: Locate nearby psychiatrists using Google Maps with current location or manual address input.
- **Interpretability**: SHAP and LIME analyses in the notebook provide insights into model predictions.
- **Deployment**: Flask server is exposed via `ngrok` for mobile app communication.


**Note**: The Kaggle dataset (`combined_dataset.csv`), model (`mental_disorder_model.pkl`), and encoder (`encoder.pkl`) are excluded. Users must source the dataset and generate the model/encoder via the notebook.

## Prerequisites
- **Python 3.8+**: For Flask and Jupyter Notebook.
- **Android Studio**: For running the Android app (tested with Android 7.0+).
- **ngrok**: For exposing the Flask server.
- **Kaggle Account**: To download the dataset (e.g., mental health survey dataset).
- **Dependencies**:
  - Flask: `flask`, `flask-cors`, `joblib`, `numpy`, `scikit-learn`.
  - Notebook: `pandas`, `scikit-learn`, `matplotlib`, `seaborn`, `shap`, `lime`.
  - Android: Volley, Google Play Services (see `app/build.gradle`).

## Setup Instructions

### 1. Machine Learning Pipeline
1. **Download Dataset**:
   - Obtain a mental health dataset from Kaggle (e.g., a CSV with 28 features like `age`, `feeling_nervous`, and a `Disorder` column).
   - Place it in the project root as `combined_dataset.csv`.
2. **Install Dependencies**:
   ```bash
   pip install pandas scikit-learn matplotlib seaborn shap lime jupyter
   ```
3. **Run the Notebook**:
   - Open `model_training.ipynb` in Jupyter.
   - Update the dataset path if needed (e.g., `./combined_dataset.csv`).
   - Execute all cells to:
     - Preprocess data (clean, encode labels).
     - Train Random Forest, Logistic Regression, and Decision Tree models.
     - Evaluate models with metrics (accuracy, precision, etc.) and visualizations (confusion matrix, precision-recall curves).
     - Generate SHAP and LIME interpretability analyses.
     - Save `mental_disorder_model.pkl` and `encoder.pkl` to `flask_api/`.
   - **Output**: Model and encoder files for the Flask backend.

### 2. Flask Backend
1. **Set Up Environment**:
   ```bash
   cd flask_api
   pip install -r requirements.txt
   ```
2. **Ensure Model Files**:
   - Verify `mental_disorder_model.pkl` and `encoder.pkl` are in `flask_api/`.
3. **Run Flask Server**:
   ```bash
   python app.py
   ```
   - The server runs on `http://0.0.0.0:5000`.
4. **Expose with ngrok**:
   - Install ngrok and run:
     ```bash
     ngrok http 5000
     ```
   - Copy the public URL (e.g., `https://xxxx.ngrok-free.app`).
5. **Test Endpoint**:
   - Send a POST request to `/predict` with JSON data (28 features, e.g., `{"age": 25, "feeling_nervous": 1, ...}`).
   - Response: `{"prediction": "Depression"}`.

### 3. Android App
1. **Open Project**:
   - Open `mentalhealthapp/` in Android Studio.
2. **Update ngrok URL**:
   - In `QuizActivity.java`, replace the placeholder URL in `flask_url` with your ngrok URL (e.g., `https://xxxx.ngrok-free.app/predict`).
3. **Build and Run**:
   - Sync the project with Gradle.
   - Run on an emulator or device (Android 7.0+).
4. **Permissions**:
   - Grant `INTERNET` and `ACCESS_FINE_LOCATION` permissions when prompted.
5. **Usage**:
   - Start the app, read the disclaimer, and take the quiz.
   - Submit answers to receive a prediction.
   - Find psychiatrists using current location or a manual address.

## Usage
1. **Quiz**: Answer 28 questions (age + 27 yes/no questions) in the app.
2. **Prediction**: The app sends responses to the Flask server, which returns a predicted disorder.
3. **Psychiatrist Search**: Choose current location or enter an address to find nearby psychiatrists on Google Maps.
4. **Interpretability**: Review SHAP and LIME analyses in the notebook for feature importance.

## Sensitive Data
- **Dataset**: `combined_dataset.csv` is excluded; download from Kaggle.
- **Model/Encoder**: `mental_disorder_model.pkl`, `encoder.pkl` are generated by the notebook.
- **ngrok URL**: Update in `QuizActivity.java` and secure with authentication if needed.
- **User Data**: Quiz responses are sent securely via HTTPS but not stored.

## Limitations
- **Dataset**: Must be sourced from Kaggle.
- **Security**: Flask allows all CORS origins; ngrok URL lacks authentication.
- **Class Imbalance**: SMOTE imported but unused, potentially affecting model performance.

## Future Improvements
- Add input validation in the notebook and Flask API.
- Implement authentication for the Flask server.
- Use unique filenames for visualizations.
- Apply SMOTE for class imbalance in the dataset.

