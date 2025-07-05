package upm.es.AlertService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import upm.es.AlertService.dto.AlertPayload;

@Service
public class AlertChecker {
    private final SensorDataService sensorDataService;
    private final AlertSenderService alertSenderService;

    public AlertChecker(SensorDataService sensorDataService,
                        AlertSenderService alertSenderService) {
        this.sensorDataService = sensorDataService;
        this.alertSenderService = alertSenderService;
    }

    @Value("${alerts.temp}")
    private double tempThreshold;

    @Scheduled(fixedRate = 2000)
    public void check() {
        System.out.println("Checkeando valores...");
        Double temp = sensorDataService.getLatestValue("TEMPERATURE", "factory1-temp-001");

        if (temp != null && temp > tempThreshold) {
            AlertPayload alert = new AlertPayload("factory1-temp-001", "TEMPERATURE", temp);
            alertSenderService.sendAlert(alert);
        }
    }
    
}
