docker-compose -p cmsfs up -d db elasticsearch 
docker-compose -p cmsfs up -d consul

docker-compose -p cmsfs stop consul config monitor collect-ssh collect-jdbc format-analyze
docker-compose -p cmsfs rm -f consul config monitor collect-ssh collect-jdbc format-analyze
docker-compose -p cmsfs build --force-rm config monitor collect-ssh collect-jdbc format-analyze
docker-compose -p cmsfs up -d consul config monitor collect-ssh collect-jdbc format-analyze