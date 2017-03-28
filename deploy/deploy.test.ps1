# $service_name = "monitor"

# scp E:\github\Monitor\deploy\docker-compose.test.yml root@10.65.103.63:/opt/cmsfs_test/docker-compose.yml
# scp E:\github\Monitor\deploy\Dockerfile-lagom.test root@10.65.103.63:/opt/cmsfs_test/Dockerfile-lagom.test
# scp E:\github\Monitor\deploy\entrypoint.sh root@10.65.103.63:/opt/cmsfs_test/entrypoint.sh
# scp E:\github\Monitor\deploy\register.sh root@10.65.103.63:/opt/cmsfs_test/register.sh
# scp E:\github\Monitor\deploy\logback.xml root@10.65.103.63:/opt/cmsfs_test/logback.xml

# ssh root@10.65.103.63 "rm -fr /opt/cmsfs_test/${service_name}"
# ssh root@10.65.103.63 "mkdir -p /opt/cmsfs_test/${service_name}/bin"
# ssh root@10.65.103.63 "mkdir -p /opt/cmsfs_test/${service_name}/lib"
# scp -r E:\github\Monitor\cmsfs\${service_name}\impl\target\universal\stage\bin root@10.65.103.63:/opt/cmsfs_test/${service_name}
# scp -r E:\github\Monitor\cmsfs\${service_name}\impl\target\universal\stage\lib root@10.65.103.63:/opt/cmsfs_test/${service_name}
# ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs stop ${service_name}"
# ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs rm -f ${service_name}"
# ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up --build -d ${service_name}"

function scp-metadate(){
  scp E:\github\Monitor\deploy\docker-compose.test.yml root@10.65.103.63:/opt/cmsfs_test/docker-compose.yml
  scp E:\github\Monitor\deploy\Dockerfile-lagom.test root@10.65.103.63:/opt/cmsfs_test/Dockerfile-lagom.test
  scp E:\github\Monitor\deploy\entrypoint.sh root@10.65.103.63:/opt/cmsfs_test/entrypoint.sh
  scp E:\github\Monitor\deploy\register.py root@10.65.103.63:/opt/cmsfs_test/register.py
  scp E:\github\Monitor\deploy\logback.xml root@10.65.103.63:/opt/cmsfs_test/logback.xml
}

function build-service($service_name){
  cd E:\github\Monitor\cmsfs
  sbt "${service_name}-impl/stage"
}

function start-services($service_name){
  echo "start service: ${service_name}..."
  ssh root@10.65.103.63 "rm -fr /opt/cmsfs_test/${service_name}"
  ssh root@10.65.103.63 "mkdir -p /opt/cmsfs_test/${service_name}/bin"
  ssh root@10.65.103.63 "mkdir -p /opt/cmsfs_test/${service_name}/lib"
  scp -r -C E:\github\Monitor\cmsfs\${service_name}\impl\target\universal\stage\bin root@10.65.103.63:/opt/cmsfs_test/${service_name}
  scp -r -C E:\github\Monitor\cmsfs\${service_name}\impl\target\universal\stage\lib root@10.65.103.63:/opt/cmsfs_test/${service_name}
  ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs stop ${service_name}"
  ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs rm -f ${service_name}"
  ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up --build -d ${service_name} || docker-compose -p cmsfs logs ${service_name}"
  ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs ps"
}

# function restart-cmsfs {
#     ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs stop"
#     ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs rm -f monitor config collect-ssh collect-jdbc format-analyze notification"
#     ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up -d consul db grafana elasticsearch"
#     ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up -d config collect-ssh collect-jdbc format-analyze"
#     ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up -d monitor"
# }

# function start-base_services(){
#     ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up -d consul "
# }

scp-metadate
build-service $args[0]
start-services $args[0]
# ssh root@10.65.103.63 "cd /opt/cmsfs_test; docker-compose -p cmsfs up -d db consul"