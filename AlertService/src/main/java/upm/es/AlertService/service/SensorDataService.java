package upm.es.AlertService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

@Service
public class SensorDataService {
    
    private final InfluxDBClient influxDBClient;
    private final String bucket;
    private final String org;

    public SensorDataService(@Value("${influx.url}") String url,
                             @Value("${influx.token}") String token,
                             @Value("${influx.org}") String org,
                             @Value("${influx.bucket}") String bucket) {
        this.influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray());
        this.org = org;
        this.bucket = bucket;
    }

    public Double getLatestValue(String measurement, String sensorId) {
        System.out.println("obteniendo el último de los valores...");
       String flux = String.format("""
            from(bucket: "%s")
            |> range(start: -5m)
            |> filter(fn: (r) => r["sensor_id"] == "%s")
            |> filter(fn: (r) => r["_field"] == "value")
            |> aggregateWindow(every: 1m, fn: mean, createEmpty: false)
            |> yield(name: "mean")
            """, bucket, sensorId);
            
        System.out.println("Lanzando consulta a InfluxDB...");
        List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, org);
        System.out.println("Número de tablas obtenidas: " + tables.size());
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Object value = record.getValue();
                if (value instanceof Number) {
                    System.out.println("valor optenido de inlfux: " + value);
                    return ((Number) value).doubleValue();
                }
            }
        }
        return null;
    }
}
