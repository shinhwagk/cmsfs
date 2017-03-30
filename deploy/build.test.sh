#!/bin/bash
docker run -t --rm -v /root/.ivy2:/root/.ivy2 \
 -v /opt/cmsfs_test:/opt/cmsfs_test sbt:0.13.13 \
 sh -c "cd /opt/cmsfs_test/cmsfs; sbt clean ${1}/stage; cd /opt/cmsfs_test; rm -fr ${1}; mkdir -p ${1}; cp -r "