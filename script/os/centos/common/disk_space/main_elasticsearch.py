from format_common import format_data
import os
import json

es_timestamp = os.getenv("utc-date")
es_metric = os.getenv("collect-name")
es_host = os.getenv("conn-name")
es_ip = os.getenv("conn-ip")

datas = format_data()

for data in datas:
  data['@timestamp'] = es_timestamp
  data['@metric'] = es_metric
  data['@ip'] = es_ip
  data['@host'] = es_host
  
print(json.dumps(datas))
