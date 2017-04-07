

# $SERVICE_NAME=$args[0]
# $SERVICE_IMPL=$args[1]

# echo "start execute deploy"
# ssh root@10.65.103.63 "cd /opt/cmsfs; git pull; cd deploy; sh deploy.test.sh ${SERVICE_NAME} ${SERVICE_IMPL}"
# ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml stop ${SERVICE_NAME}"
# ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml rm -f ${SERVICE_NAME}"
# ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml up -d ${SERVICE_NAME}"

Param(
  [string[]]$startServices,
  [string[]]$buildServices,
  [switch]$startBaseServices
)

function scp_cmsfs() {
  cd E:\github\cmsfs;
  git add -A
  git commit -m "test"
  git push
}

scp_cmsfs


$baseServices = "consul", "db", "redis";

function startBaseService($serviceName) {
  ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml up -d ${serviceName}"
}

function startService($serviceName) {
  Write-Host -ForegroundColor Red "start ${serviceName} service...";
  ssh root@10.65.103.63 "cd /opt/cmsfs; git pull; cd deploy; sh deploy.test.sh ${serviceName} 1"
  ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml stop ${serviceName}"
  ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml rm -f ${serviceName}"
  ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml up -d ${serviceName}"
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