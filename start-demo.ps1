Write-Host "Starting port forwarding for banking services..."

Start-Job -Name "accounts-forward" -ScriptBlock {
    kubectl port-forward -n bank svc/accounts-service 30090:8090 --address 0.0.0.0
}

Start-Job -Name "gateway-forward" -ScriptBlock {
    kubectl port-forward -n bank svc/api-gateway-service 30091:8700 --address 0.0.0.0
}

Start-Job -Name "cash-forward" -ScriptBlock {
    kubectl port-forward -n bank svc/cash-service 30092:8900 --address 0.0.0.0
}

Start-Job -Name "transfer-forward" -ScriptBlock {
    kubectl port-forward -n bank svc/transfer-service 30094:9800 --address 0.0.0.0
}

Start-Job -Name "notifications-forward" -ScriptBlock {
    kubectl port-forward -n bank svc/notifications-service 30093:9900 --address 0.0.0.0
}

Write-Host "All ports forwarded in background!"
Write-Host "Running jobs:"
Get-Job | Select-Object Name, State