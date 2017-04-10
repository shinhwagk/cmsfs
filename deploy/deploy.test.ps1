Param(
  [switch]$startAllLagomServices,
  [string[]]$startLagomServices,
  [string[]]$stopLagomServices,
  [string[]]$buildServices,
  [switch]$startAllBaseServices,
  [string[]]$startBaseServices
)

$BASE_DIR = $PSScriptRoot; Set-Location $BASE_DIR;

$REMOTE_SERVICE_IP = "10.65.103.63";

[hashtable]$lagomServices = @{}
$lagomServices.Add("config", "..\cmsfs\config\impl\target\universal\config-impl-1.0-SNAPSHOT.zip")
$lagomServices.Add("collect-jdbc", "..\cmsfs\collect-jdbc\impl\target\universal\collect-jdbc-impl-1.0-SNAPSHOT.zip")
$lagomServices.Add("collect-ssh", "..\cmsfs\collect-ssh\impl\target\universal\collect-ssh-impl-1.0-SNAPSHOT.zip")
$lagomServices.Add("format-analyze", "..\cmsfs\format-analyze\impl\target\universal\format-analyze-impl-1.0-SNAPSHOT.zip")
$lagomServices.Add("format-alarm", "..\cmsfs\format-alarm\impl\target\universal\format-alarm-impl-1.0-SNAPSHOT.zip")
$lagomServices.Add("monitor", "..\cmsfs\monitor\impl\target\universal\monitor-impl-1.0-SNAPSHOT.zip")

$baseServices = "consul", "db", "redis", "elasticsearch", "notification";

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
  Write-Host -ForegroundColor Red "start all build lagom service: ${serviceName} ...";
  Set-Location $BASE_DIR\..\cmsfs; sbt dist;
  Write-Host -ForegroundColor Red "start all build lagom service: ${serviceName} ...";
}

function buildLagomService($serviceName) {
  Write-Host -ForegroundColor Red "start build lagom service: ${serviceName} ...";
  $lagomServiceName = "${serviceName}-impl";
  Set-Location $BASE_DIR\..\cmsfs; sbt ${lagomServiceName}/dist | out-null;
  Write-Host -ForegroundColor Red "end build lagom service: ${serviceName} ...";
  Write-Host -ForegroundColor Red "-------------------------------------------";
}

function startLagomService($serviceName) {
  Set-Location $BASE_DIR
  $serviceFile = $lagomServices.Item($serviceName);
  scpCommandForFile $serviceFile;
  $serviceZipFile = $serviceFile.split("\")[-1];
  $serviceProjectName = $serviceZipFile.Substring(0, $serviceZipFile.length - 4);
  sshExecute "cd /opt/cmsfs/; rm -fr ./${serviceProjectName}; unzip ${serviceZipFile} -d ./";

  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} build ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} stop ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} rm -f ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} up -d ${serviceName}";
  sshExecute "cd /opt/cmsfs/; ${DOCKER_COMPOSE} ps"
}

function startBaseServiceCommand($service) {
  if($service.equals("notification")) {
    $RANDOM = Get-Random -minimum 1 -maximum 101
    $body="{""ID"":""${RANDOM}"",""Name"":""${service}"",""Tags"":[],""Address"":""10.65.209.12"",""Port"":8380}";
    Invoke-WebRequest -Method POST -Body "${body}" -Uri http://${REMOTE_SERVICE_IP}:8500/v1/agent/service/register
  } else {
    startBaseService $service;
  }
}

scpDeployFile

if ($startAllBaseServices.IsPresent) {
  foreach ($service in $baseServices) {
    startBaseServiceCommand $service
  }
}

if($startBaseServices.length -ge 1) {
  foreach ($service in [string[]]$startBaseServices) {
    startBaseServiceCommand $service
  }
}

if ($startLagomServices.Length -ge 1) {
  foreach ($service in [string[]]$startLagomServices) {
    buildLagomService $service;
    startLagomService $service;
  }
}

if ($startAllLagomServices.IsPresent) {
  [string[]]$services = @($lagomServices.keys)
  buildLagomAllService;
  foreach ($service in $services) {
    startLagomService $service;
  }
}
