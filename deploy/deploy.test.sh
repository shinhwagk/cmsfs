#!/bin/bash

set -e

BASE="/opt/cmsfs"
DEPLOY="${BASE}/deploy"
CMSFS_HOME="${BASE}/cmsfs"
SERVICE_NAME="${1}"
SERVICE_IMPL="${2}"

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