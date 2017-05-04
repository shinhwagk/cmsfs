import sys
import json
import re
import http.client
import os

f = open(sys.argv[1], "r")
datas = json.loads(f.read())
f.close

def sendElasticsearch(_index, _type, contents):
    for content in contents:
        headers = {"Content-type": "application/json; charset=utf-8"}
        conn = http.client.HTTPConnection("10.65.103.63", 9200)
        url = "/%s/%s" % (_index, _type)
        conn.request("POST", url, json.dumps(content), headers)
        response = conn.getresponse()
        print(response.status, response.reason)

sendElasticsearch("monitor", "oracle", datas)
