package upm.es.AlertService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import upm.es.AlertService.dto.AlertPayload;

@RestController
@RequestMapping("/alerts/webhook")
@Tag(name = "Webhook", description = "Este endpoint manda un correo alertando del estado de los sensores")
public class WebhookController {
     private final JavaMailSender mailSender;

    public WebhookController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Operation(summary = "Recibe alertas", description = "Recibe un payload de alerta generado por el middleware")
    @PostMapping
    public ResponseEntity<Void> receiveAlert(@RequestBody AlertPayload payload) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("b.vcliment@alumnos.upm.es");
        mail.setTo("b.vcliment@alumnos.upm.es");
        mail.setSubject("⚠️ Alerta de " + payload.getType() + " en la fabrica 1");
        mail.setText("Sensor: " + payload.getSensorId() + "\nValor: " + payload.getValue());
        mailSender.send(mail);

        System.out.println("Correo enviado...");

        return ResponseEntity.ok().build();
    }
    
}
