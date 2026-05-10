# My Bank App

Микросервисное банковское приложение на Spring Boot 3.3.5

---

## Технологии

- **Язык/Фреймворк**: Java 21, Spring Boot 3.3.5, Spring Cloud
- **Архитектура**: Микросервисы (accounts, cash, transfer, notifications, api-gateway)
- **БД**: PostgreSQL 16
- **Messaging**: Apache Kafka
- **Auth**: Keycloak (OAuth2/OIDC, JWT)
- **Observability**: Prometheus, Grafana, Zipkin, ELK
- **Deployment**: Docker Compose, Kubernetes (Helm)

---

## Быстрый старт

### Локальный запуск

```bash
# Запустить инфраструктуру и сервисы
docker-compose up -d --build

# Проверить статус
docker-compose ps
```
Доступные сервисы

    Grafana: http://localhost:3000 (admin/admin)
    Prometheus: http://localhost:9090
    Zipkin: http://localhost:9411
    Kibana: http://localhost:5601
````
my-bank-app/
├── accounts-service/          # Управление счетами
├── cash-service/              # Операции с наличными
├── transfer-service/          # Переводы между счетами
├── notifications-service/     # Уведомления через Kafka
├── api-gateway/               # Единая точка входа
├── front-ui/                  # Веб-интерфейс
├── common-clients/            # Общие DTO и клиенты
└── deployment/
    ├── docker-compose.yml     # Локальный запуск
    └── bank-app/              # Helm chart для K8s
````
Почему Kafka внутри, а метрики снаружи?
Kafka внутри (Bitnami chart):

    Часть бизнес-логики: события переводов и уведомлений критичны для работы приложения
    Масштабируется вместе с сервисами: при росте нагрузки можно добавить партиции и консьюмеры
    Проще для локальной разработки и CI/CD: одна команда поднимает весь стек

Prometheus/Grafana/Zipkin/ELK снаружи:

    Это инфраструктурные сервисы, они не содержат бизнес-логики банка
    Имеют свой lifecycle: обновление мониторинга не должно требовать пересборки приложения
    Централизованный подход: один кластер мониторинга может обслуживать десятки приложений
    Безопасность: доступ к логам и метрикам контролируется отдельно от бизнес-сервисов

Также после запуска Helm чарта необходимо использовать скрипт start-demo.ps1 (для проброса портов)
