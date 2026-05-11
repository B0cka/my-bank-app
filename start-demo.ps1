Start-Process kubectl -ArgumentList "port-forward","-n","bank","svc/accounts-service","30090:8090","--address","0.0.0.0"
Start-Process kubectl -ArgumentList "port-forward","-n","bank","svc/api-gateway-service","30091:8700","--address","0.0.0.0"
Start-Process kubectl -ArgumentList "port-forward","-n","bank","svc/cash-service","30092:8900","--address","0.0.0.0"
Start-Process kubectl -ArgumentList "port-forward","-n","bank","svc/transfer-service","30094:9800","--address","0.0.0.0"
Start-Process kubectl -ArgumentList "port-forward","-n","bank","svc/notifications-service","30093:9900","--address","0.0.0.0"

Start-Sleep -Seconds 5
Write-Host "Все сервисы доступны. Проверь: http://localhost:30094/actuator/health"
pause