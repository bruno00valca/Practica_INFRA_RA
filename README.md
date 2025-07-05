
# Práctica: Despliegue de Infraestructura y Seguridad

Este proyecto simula una infraestructura orientada a servicios para la gestión de sensores en tres fábricas. Incluye balanceo de carga, control de tráfico con Token Bucket, almacenamiento en InfluxDB, visualización con Grafana y un servicio adicional de alertas por correo.

## 📦 Estructura del Proyecto

- Simulador de sensores (Java)
- Balanceador de carga (HAProxy)
- Backend/API REST (Flask)
- Broker MQTT (Mosquitto)
- Middleware a InfluxDB (Python)
- Dashboard de visualización (Grafana)
- Servicio de alertas por correo (Python + Swagger)

---

## ⚙️ Prerrequisitos

Instalar los siguientes componentes en tu máquina local (preferiblemente Linux o WSL si estás en Windows):

### General

- Java 11 o superior
- Python 3.10+
- pip
- Git

### Backend y middleware

- Flask
- Paho MQTT Client
- influxdb-client
- Flask-Mail (opcional, si se quiere mejorar el envío de correos)
- Swagger UI (via Flask-Swagger o manualmente)

Instalación con pip:

```bash
pip install flask paho-mqtt influxdb-client flask-swagger-ui
```

### Servidores

- [HAProxy](http://www.haproxy.org/)
- [Mosquitto MQTT Broker](https://mosquitto.org/download/)
- [InfluxDB](https://www.influxdata.com/)
- [Grafana](https://grafana.com/grafana/download)

---

## 🚀 Instrucciones de Despliegue

### 1. Clona el repositorio

```bash
git clone https://github.com/bruno00valca/Practica_INFRA_RA.git
cd Practica_INFRA_RA
```

---

### 2. Simulador de Sensores (Java)

Compila y ejecuta la aplicación Java que simula los sensores de las tres fábricas.

Esto genera hilos que hacen peticiones HTTP POST al endpoint `/data` cada 5 segundos.

---

### 3. Configurar HAProxy

Ejemplo de configuración `haproxy.cfg`:

```txt
frontend http_front
   bind *:80
   default_backend http_back

backend http_back
   balance roundrobin
   server middleware1 127.0.0.1:4000 check
   server middleware2 127.0.0.1:4001 check
```

Inicia HAProxy:

```bash
sudo systemctl start haproxy
```

---

### 4. Inicia las instancias del Backend Flask

Lanza las dos instancias en diferentes puertos:

```bash
cd backend
FLASK_APP=app.py flask run --port=4000
FLASK_APP=app.py flask run --port=4001
```

---

### 5. Configurar y ejecutar Mosquitto

Edita `mosquitto.conf` para configurar el puerto y los logs si hace falta.

Ejecuta Mosquitto y asegurate de que el listener está en el puerto 1883

---

### 6. Middleware para InfluxDB

Suscribe al topic MQTT y almacena los datos:

```bash
export INFLUX_TOKEN='TU_TOKEN'
```

> ⚠️ Si usas PyCharm, asegúrate de definir las variables de entorno en el entorno de ejecución del IDE.

---

### 7. Visualización con Grafana

- Accede a Grafana: [http://localhost:3000](http://localhost:3000)
- Añade la fuente de datos: InfluxDB
- Importa el Dashboard desde `grafana_dashboard.json` (si lo has exportado) o créalo manualmente con los paneles tipo "Gauge"

---

### 8. Servicio Propio de Alertas

Este servicio de Spring Boot consulta si la temperatura de la Fábrica 1 supera los 40°C y envía un correo al correo que se configure.

Documentación disponible en: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)



---

## 🛡️ Control de tráfico

El backend usa **Token Bucket** para limitar el número de peticiones:

- Capacidad: 12 tokens
- Recarga: 5 tokens/segundo

Si se excede el límite, se devuelve HTTP 429 (Too Many Requests).

---

## 📌 Autor

Bruno Valcárcel Climent 
[Repositorio original](https://github.com/bruno00valca/Practica_INFRA_RA)

---
