from format_common import get_data
import os
import json

es_timestamp = os.getenv("utc-data")
es_metric = os.getenv("collect-name")
service_name = os.getenv("conn-service")

datas = get_data()

for data in datas:
  data['@timestamp'] = es_timestamp
  data['@metric'] = es_metric
  data['@service'] = service_name
  
print(json.dumps(datas))
