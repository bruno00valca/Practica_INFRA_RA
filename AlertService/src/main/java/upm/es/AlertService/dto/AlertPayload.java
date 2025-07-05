package upm.es.AlertService.dto;

import lombok.Data;

@Data
public class AlertPayload {
    private String sensorId;
    private String type;
    private double value;

    public AlertPayload() {}

    public AlertPayload(String sensorId, String type, double value) {
        this.sensorId = sensorId;
        this.type = type;
        this.value = value;
    }
    
}
