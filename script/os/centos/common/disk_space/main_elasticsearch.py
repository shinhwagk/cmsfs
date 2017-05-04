from format_common import format_data
import os
import json

es_timestamp = os.getenv("utc-date")
es_metric = os.getenv("collect-name")

datas = format_data()

for data in datas:
  data['@timestamp'] = es_timestamp
  data['@metric'] = es_metric
  
print(json.dumps(datas))
