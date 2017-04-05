#!/bin/bash

set -e

BASE="/opt/cmsfs"
DEPLOY="${BASE}/deploy"
CMSFS_HOME="${BASE}/cmsfs"
SERVICE_NAME="${1}"
SERVICE_IMPL="${2}"
CMSFS_PROJECT_HOEM="${CMSFS_HOME}/${SERVICE_NAME}"
CMSFS_PROJECT_RUNNING_HOME="${DEPLOY}/cmsfs/${SERVICE_NAME}"

echo "remote: generate cmsfs project name & location..."
PROJECT_PATH=""
PROJECT_NAME=""
if [[ ${SERVICE_IMPL} == 1 ]]; then
  PROJECT_PATH=${CMSFS_HOME}/${SERVICE_NAME}/impl/target/universal/stage
  PROJECT_NAME="${SERVICE_NAME}-impl"
else
  PROJECT_PATH=${CMSFS_HOME}/${SERVICE_NAME}/target/universal/stage
  PROJECT_NAME="${SERVICE_NAME}"
fi
echo "remote: generate cmsfs project name & location: ${PROJECT_NAME} & ${PROJECT_PATH}"

echo "remote: build ${SERVICE_NAME}..."
docker run -t --rm -v ${CMSFS_HOME}:/opt/cmsfs -v /root/.ivy2:/root/.ivy2 sbt:0.13.13 sh -c "cd /opt/cmsfs; sbt clean ${PROJECT_NAME}/stage"
echo "remote: build ${SERVICE_NAME} success..."

rm -fr ${CMSFS_PROJECT_RUNNING_HOME}
mkdir -p ${CMSFS_PROJECT_RUNNING_HOME}
cp -r ${PROJECT_PATH}/* ${CMSFS_PROJECT_RUNNING_HOME}
echo ${PROJECT_PATH}
# docker run -t --rm -v ${CMSFS_PROJECT_RUNNING_HOME}:/opt/cmsfs sbt:0.13.13 sh -c "cd /opt/cmsfs/bin; ./web-gateway"

cd ${DEPLOY}
DOCKER_COMPOSE_COMMAND="docker-compose -p cmsfs -f docker-compose.test.yml"
${DOCKER_COMPOSE_COMMAND} stop ${SERVICE_NAME}
${DOCKER_COMPOSE_COMMAND} rm -f ${SERVICE_NAME}
${DOCKER_COMPOSE_COMMAND} up --build -d ${SERVICE_NAME} || docker-compose -p cmsfs logs ${SERVICE_NAME}
${DOCKER_COMPOSE_COMMAND} ps