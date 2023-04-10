kubectl -n=production apply  -f ip_location_server_configmap.yaml
kubectl -n=production rollout restart deployment ip-location-server
sleep 5s
kubectl -n=production get pods
echo kubectl -n=production logs --follow=true