#!/bin/bash
SERVICE_IP=`hostname -i`
body="{\"ID\":\"${RANDOM}\",\"Name\":\"${SERVICE_NAME}\",\"Tags\":[],\"Address\":\"${SERVICE_IP}\",\"Port\":${SERVICE_PORT}}"
curl http://consul.cmsfs.org:8500/v1/agent/service/register -d $body