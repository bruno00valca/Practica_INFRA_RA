package upm.es.AlertService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import upm.es.AlertService.dto.AlertPayload;

@Service
public class AlertSenderService {
    
   private final RestTemplate restTemplate = new RestTemplate();

    @Value("${webhook.url}")
    private String webhookUrl;

    public void sendAlert(AlertPayload alert) {
        try {
            restTemplate.postForEntity(webhookUrl, alert, Void.class);
        } catch (Exception e) {
            System.err.println("Error enviando alerta al webhook: " + e.getMessage());
        }
    }
}
