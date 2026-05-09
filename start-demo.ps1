
Write-Host "Запуск демонстрации банка..."

Write-Host "готовности подов..."
kubectl wait --for=condition=ready pod --all -n bank --timeout=120s

Write-Host "Проброс портов для Prometheus..."
kubectl port-forward -n bank svc/accounts-service 30090:8090 --address 0.0.0.0
kubectl port-forward -n bank svc/api-gateway-service 30091:8700 --address 0.0.0.0
kubectl port-forward -n bank svc/cash-service 30092:8900 --address 0.0.0.0
kubectl port-forward -n bank svc/transfer-service 30093:9800 --address 0.0.0.0
kubectl port-forward -n bank svc/notifications-service 30094:9900 --address 0.0.0.0

Write-Host "Проброс Ingress для доступа к приложению..."
kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 8080:80 --address 0.0.0.0

Write-Host "Перезапуск Prometheus..."
docker-compose restart prometheus

Write-Host "Prometheus: http://localhost:9090/targets"
Write-Host "Приложение: http://localhost:8080/"
Write-Host "Zipkin: http://localhost:9411"
Write-Host "Grafana: http://localhost:3000"