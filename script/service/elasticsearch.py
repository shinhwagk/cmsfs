import sys
import json
import re

f = open(sys.argv[1], "r")
datas = json.loads(f.read())
f.close

f = open(sys.argv[2], "r")
args = json.loads(f.read())
f.close

def sendElasticsearch(_index,_type,contents):
  orderNo = int(time.time() * 1000)
  for content in contents:
    headers = {"Content-type": "application/json; charset=utf-8"}
    conn = http.client.HTTPConnection("10.65.103.63", 9200)
    conn.request("POST", "/mns-web/services/rest/msgNotify", content, headers)
    response = conn.getresponse()
    print(response.status, response.reason)
