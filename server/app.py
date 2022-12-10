from flask import Flask
from flask import request
import pickle
import numpy as np

model = pickle.load(open('finalized_model.sav', 'rb'))
app = Flask(__name__)


@app.route("/calculate_risk_score", methods=['POST'])
def calculate_risk_score():

    data = {
        "ip": 0 if request.form.get('ip') is None else request.form.get('ip'),
        "device": 0 if request.form.get('device') is None else request.form.get('device'),
        "platform": 0 if request.form.get('platform') is None else request.form.get('platform'),
        "browser": 0 if request.form.get('browser') is None else request.form.get('browser'),
        "language": 0 if request.form.get('lang') is None else request.form.get('lang')
    }
    score = predict(data)
    return {
        "risk-score": int(score)
    }


def predict(data):

    x_data = np.array([[data["ip"], data["device"], data['platform'], data['browser'], data['language']]])
    return model.predict(x_data)[0]

