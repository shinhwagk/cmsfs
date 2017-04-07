Param(
  [string[]]$startServices,
  [string[]]$buildServices,
  [switch]$startBaseServices
)

function sshExecute($command) {
  ssh root@10.65.103.63 $command
}

function scp_cmsfs() {
  cd E:\github\cmsfs;
  git add -A; git commit -m "test"; git push;
}

scp_cmsfs

$baseServices = "consul", "db", "redis";

function startBaseService($serviceName) {
  ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml up -d ${serviceName}"
}

function startService($serviceName) {
  sshExecute "cd /opt/cmsfs; git pull"
  $DOCKER_COMPOSE = "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml"
  Write-Host -ForegroundColor Red "start ${serviceName} service...";

  # sshExecute "cd /opt/cmsfs; git pull; cd deploy; sh deploy.test.sh ${serviceName} 1"
  sshExecute "docker run -t --rm -v /opt/cmsfs/cmsfs:/opt/cmsfs -v /root/.ivy2:/root/.ivy2 sbt:0.13.13 sh -c 'cd /opt/cmsfs; sbt clean ${serviceName}-impl/stage'"

  $serviceDeployPath = "/opt/cmsfs/deploy/cmsfs/${serviceName}"
  sshExecute "rm -fr ${serviceDeployPath}; mkdir -p ${serviceDeployPath}"
  sshExecute "cp -r /opt/cmsfs/cmsfs/${serviceName}/impl/target/universal/stage/* ${serviceDeployPath}"

  sshExecute "${DOCKER_COMPOSE} stop ${serviceName}"
  sshExecute "${DOCKER_COMPOSE} rm -f ${serviceName}"
  sshExecute "${DOCKER_COMPOSE} up -d ${serviceName}"
  sshExecute "${DOCKER_COMPOSE} ps"
  Write-Host -ForegroundColor Red "end ${serviceName} service...";
}

function buildService($serviceName) {
  Write-Host -ForegroundColor Red "start ${serviceName} build...";
  sbt "${serviceName}-impl/stage" | Out-Null
  Write-Host -ForegroundColor Red "end ${serviceName} build..."
}

# foreach ($service in $buildServices) {
#   Set-Location ../cmsfs/; buildService $service;
# }

foreach ($service in $startServices) {
  startService $service;
}

if ($startBaseServices.IsPresent) {
  foreach ($service in $baseServices) {
    startBaseService $service;
  }
}