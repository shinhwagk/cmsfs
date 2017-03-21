docker-compose -p cmsfs up -d db elasticsearch 
docker-compose -p cmsfs up -d consul

docker-compose -p cmsfs stop consul config monitor collect-ssh collect-jdbc format-analyze
docker-compose -p cmsfs rm -f consul config monitor collect-ssh collect-jdbc format-analyze
docker-compose -p cmsfs up -d config monitor collect-ssh collect-jdbc format-analyze