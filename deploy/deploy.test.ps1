Param(
  [switch]$startAllLagomServices,
  [string[]]$startLagomServices,
  [string[]]$stopLagomServices,
  [string[]]$buildServices,
  [switch]$startAllBaseServices,
  [string[]]$startBaseServices
)

$BASE_DIR = $PSScriptRoot;
$DEPLOY_DIR = "${BASE_DIR}";
$PROJECT_DIR = "${BASE_DIR}/../cmsfs";
$BASH_BASH_DIR = "/mnt/e/github/cmsfs";
$BASH_PROJECT_DIR = "${BASH_BASH_DIR}/cmsfs"

$REMOTE_SERVICE_IP = "10.65.103.63";

function genLagomProjectPath($serviceName) {
  return "..\cmsfs\${serviceName}\impl\target\universal\${serviceName}-impl-1.0-SNAPSHOT.tgz";
}

$lagomServices = "config", "collect-jdbc", "collect-ssh", "format-analyze", "format-alarm", "monitor";

$baseServices = "consul", "db", "redis", "elasticsearch", "notification", "grafana";

$DOCKER_COMPOSE = "docker-compose -p cmsfs -f docker-compose.test.yml"

function sshExecute($command) {
  ssh root@${REMOTE_SERVICE_IP} $command 
}

function scpCommand($source, $target, [bool]$dir?) {
  if ($dir?) {
    scp -r $source $target
  }
  else {
    scp $source $target
  }
}

function scpCommandForFile($source, $target = "root@${REMOTE_SERVICE_IP}:/opt/cmsfs/") {
  Write-Host -ForegroundColor Red "start scp file: ${source} ...";
  scpCommand $source $target $false
  Write-Host -ForegroundColor Red "end scp file: ${source} ...";
}

function scpDeployFile() {
  Set-Location $DEPLOY_DIR;
  scpCommandForFile docker-compose.test.yml
  scpCommandForFile Dockerfile-lagom.test 
  scpCommandForFile entrypoint.sh 
  scpCommandForFile logback.xml
  scpCommandForFile register.py 
  scpCommandForFile register.sh 
}

function startBaseService($serviceName) {
  sshExecute "cd /opt/cmsfs/; docker-compose -p cmsfs -f docker-compose.test.yml up -d --force-recreate ${serviceName}"
}

function stopLagomServices($serviceName) {
  Set-Location /opt/cmsfs;
  sshExecute "${DOCKER_COMPOSE} stop $serviceName"
  sshExecute "${DOCKER_COMPOSE} rm -f $serviceName"
}

function buildLagomAllService() {
  Set-Location $PROJECT_DIR;
  Write-Host -ForegroundColor Red "start all build lagom service: ${serviceName} ...";
  bashExecute "sbt universal:packageZipTarball";
  Write-Host -ForegroundColor Red "start all build lagom service: ${serviceName} ...";
}

function bashExecute($command) {
  bash -c "source ~/.bash_profile; ${command}";
}

function buildLagomService($serviceName) {
  Set-Location $PROJECT_DIR;
  Write-Host -ForegroundColor Red "start build lagom service: ${serviceName} ...";
  $lagomServiceImplName = "${serviceName}-impl";
  bashExecute "cd ${BASH_PROJECT_DIR}; sbt ${lagomServiceImplName}/universal:packageZipTarball"
  Write-Host -ForegroundColor Red "end build lagom service: ${serviceName} ...";
  Write-Host -ForegroundColor Red "-------------------------------------------";
}

function startLagomService($serviceName) {
  Set-Location $BASE_DIR
  scpCommandForFile (genLagomProjectPath $serviceName);

  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} stop ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} rm -f ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} up -d --build ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} ps"
}

function consulDeregister($serviceName) {
  $url = "http://${REMOTE_SERVICE_IP}:8500/v1/catalog/service/${serviceName}"
  $services = (Invoke-WebRequest -Method GET -Uri $url).Content.Trim() | ConvertFrom-Json ;
  foreach ($service in $services) {
    $serviceId = $service.ServiceID
    $re = "http://${REMOTE_SERVICE_IP}:8500/v1/agent/service/deregister/${serviceId}"
    echo " $serviceId $node $body"
    Invoke-WebRequest -Method Put -Uri $re
  }
}

function startBaseServiceCommand($service) {
  Set-Location $DEPLOY_DIR;
  if ($service.equals("notification")) {
    $RANDOM = Get-Random -minimum 1 -maximum 101
    $body = "{""ID"":""${RANDOM}"",""Name"":""${service}"",""Tags"":[],""Address"":""10.65.209.12"",""Port"":8380}";
    Invoke-WebRequest -Method POST -Body "${body}" -Uri http://${REMOTE_SERVICE_IP}:8500/v1/agent/service/register
  }
  else {
    startBaseService $service;
  }
}

scpDeployFile

if ($startAllBaseServices.IsPresent) {
  foreach ($service in $baseServices) {
    startBaseServiceCommand $service
  }
}

if ($startBaseServices.length -ge 1) {
  foreach ($service in [string[]]$startBaseServices) {
    startBaseServiceCommand $service
  }
}

if ($startLagomServices.Length -ge 1) {
  foreach ($service in [string[]]$startLagomServices) {
    # buildLagomService $service;
    consulDeregister $service;
    startLagomService $service;
  }
}

if ($startAllLagomServices.IsPresent) {
  buildLagomAllService;
  foreach ($service in $lagomServices) {
    startLagomService $service;
  }
}