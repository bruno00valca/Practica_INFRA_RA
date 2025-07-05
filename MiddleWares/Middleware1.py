from flask import Flask, request, Response, json, send_from_directory
import paho.mqtt.client as mqtt

from TokenBucket import TokenBucket

app = Flask(__name__)
client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
bucket = TokenBucket(capacity=12, refill_rate_per_second=5)
client.connect("localhost", 1883, 60)

@app.route('/description')
def wadl():
    return send_from_directory('static', 'descriptionm1.wadl', mimetype='application/xml')

@app.route('/data', methods=['POST'])
def get_data():
    if not bucket.allow_request():
        print("Petición rechazada por límite de tokens")
        return Response("Rate limit exceeded", status=429)

    data = request.get_json()
    sensor = {
        "sensor_id": data['sensor_id'],
        "type": data['type'],
        "value": data['value'],
        "timestamp": data['timestamp']
    }

    topic = f"sensores/{data['type'].lower()}/{data['sensor_id']}"
    payload = json.dumps(data)
    result = client.publish(topic, payload)
    print(f"[MQTT] → Topic: {topic} | Data: {payload} | Result: {result.rc}")

    print(sensor)
    return Response(status=200)

app.run(port=4000)