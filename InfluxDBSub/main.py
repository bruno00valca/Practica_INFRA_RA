import os
import json

from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS
import paho.mqtt.client as mqtt


# Configuración de InfluxDB
INFLUX_URL = "http://localhost:8086"
INFLUXDB_TOKEN = os.environ.get("INFLUXDB_TOKEN")
INFLUX_ORG = "UPM"
INFLUX_BUCKET = "RA"

# Inicializa cliente InfluxDB
influx_client = InfluxDBClient(url=INFLUX_URL, token=INFLUXDB_TOKEN, org=INFLUX_ORG)
write_api = influx_client.write_api(write_options=SYNCHRONOUS)

# Función para guardar datos en InfluxDB
def save_influx(json_data):
    try:
        data = json.loads(json_data)

        point = (
            Point("mediciones")
            .tag("sensor_id", data["sensor_id"])
            .tag("type", data["type"].lower())
            .field("value", float(data["value"]))
            .time(data["timestamp"], WritePrecision.NS)
        )

        write_api.write(bucket=INFLUX_BUCKET, org=INFLUX_ORG, record=point)
        print(f"[InfluxDB] {data['sensor_id']} → {data['type']} = {data['value']}")
    except Exception as e:
        print(f"[ERROR] No se pudo guardar en InfluxDB: {e}")

# Funciones del cliente MQTT
def on_connect(client, userdata, flags, reason_code, properties):

    print(f"Conectado con mqtt con código de respuesta: {reason_code}")



def on_message(client, userdata, msg):
    payload = msg.payload.decode()
    print(f"[MQTT] {msg.topic} → {payload}")
    save_influx(payload)

# Inicializa el cliente MQTT
mqtt_client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
mqtt_client.connect("localhost", 1883)

mqtt_client.subscribe("sensores/#")
mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message

mqtt_client.loop_forever()
