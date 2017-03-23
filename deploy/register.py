import json
import os
import random
import urllib2
import socket

CONSUL_ID = str(random.randint(1, 100))

SERVICE_NAME = os.environ["SERVICE_NAME"]
SERVICE_IP = socket.gethostbyname(socket.gethostname())
SERVICE_PORT = int(os.environ["SERVICE_PORT"])

CONSUL_CHECK_TCP = "%s:%s" % (SERVICE_IP, SERVICE_PORT)

url = "http://consul.cmsfs.org:8500/v1/agent/service/register"
data = {"id": CONSUL_ID,
        "name": SERVICE_NAME,
        "tags": [],
        "address": SERVICE_IP,
        "port": SERVICE_PORT,
        "check": {
            "tcp": CONSUL_CHECK_TCP,
            "interval": "10s",
            "DeregisterCriticalServiceAfter": "30s",
            "timeout": "1s"
        }}

body = json.dumps(data)
header = {'Content-Type': 'application/json'}
request = urllib2.Request(url, body, header)
urllib2.urlopen(request)
