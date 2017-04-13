import json
import os
import random
import urllib2
import socket
import uuid

CONSUL_ID = str(uuid.uuid1())

SERVICE_NAME = os.environ["SERVICE_NAME"]
SERVICE_IP = socket.gethostbyname(socket.gethostname())
SERVICE_PORT = int(os.environ["SERVICE_PORT"])

CONSUL_CHECK_TCP = "%s:%s" % (SERVICE_IP, SERVICE_PORT)

url = "http://consul.cmsfs.org:8500/v1/agent/service/register"
data = {
    "ID" : CONSUL_ID,
    "Name": SERVICE_NAME,
    "Address": SERVICE_IP,
    "Port": SERVICE_PORT,
    "Check": {
        "DeregisterCriticalServiceAfter": "90m",
        "tcp": CONSUL_CHECK_TCP,
        "interval": "10s",
        "timeout": "1s"
    }
}

body = json.dumps(data)
header = {'Content-Type': 'application/json'}
request = urllib2.Request(url, body, header)
request.get_method = lambda: 'PUT'
urllib2.urlopen(request)
