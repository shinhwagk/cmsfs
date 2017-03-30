#!/bin/bash
docker run -t --rm -v /root/.ivy2:/root/.ivy2 -v /opt/cmsfs_test/cmsfs:/opt/cmsfs sbt:0.13.13 sh -c "cd /opt/cmsfs; sbt clean ${1}/stage"