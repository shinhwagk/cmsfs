#!/bin/bash
CMSFS_HOME="/opt/cmsfs_test2"
CMSFS_PROJECT_HOME="${CMSFS_HOME}/cmsfs"

SERVICE_NAME="${1}"

docker run -t --rm -v ${CMSFS_PROJECT_HOME}:/opt/cmsfs -v /root/.ivy2:/root/.ivy2 sbt:0.13.13 sh -c "cd /opt/cmsfs; sbt clean ${SERVICE_NAME}/stage"