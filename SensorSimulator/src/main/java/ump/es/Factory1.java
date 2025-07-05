package ump.es;

import ump.es.constants.SensorType;
import ump.es.utils.SensorUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;

public class Factory1 implements Runnable{

    private static final String ENDPOINT = SensorUtils.getProp("endpoint");
    private static final int INTERVAL = Integer.parseInt(SensorUtils.getProp("sleepTime"));
    private static final Random rand = new Random();

    public void run(){

        System.out.println("endpoint: " + ENDPOINT);
        while (true) {
            sendSensorData("factory1-temp-001", SensorType.TEMPERATURE, "temp_min", "temp_max");
            sendSensorData("factory1-hum-001", SensorType.HUMIDITY, "hum_min", "hum_max");
            sendSensorData("factory1-co2-001", SensorType.CO2, "co2_min", "co2_max");
            sendSensorData("factory1-voc-001", SensorType.VOC, "voc_min", "voc_max");

            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                System.err.println("Factory1 interrupted");
                return;
            }
        }
    }

    private static void sendSensorData(String sensorId, SensorType type, String keyMin, String keyMax) {
        try {
            double min = Double.parseDouble(SensorUtils.getProp(keyMin));
            double max = Double.parseDouble(SensorUtils.getProp(keyMax));
            double value = min + (max - min) * rand.nextDouble();
            String timestamp = Instant.now().toString();

            String json = String.format(java.util.Locale.US,"""
                {
                  "sensor_id": "%s",
                  "type": "%s",
                  "value": %.2f,
                  "timestamp": "%s"
                }
                """, sensorId, type, value, timestamp);

            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.printf("[%s] Sent %s = %.2f → HTTP %d%n", sensorId, type, value, responseCode);

            conn.disconnect();
        } catch (Exception e) {
            System.err.println("Error sending data: " + e.getMessage());
        }
    }

    public Factory1(){}
}
