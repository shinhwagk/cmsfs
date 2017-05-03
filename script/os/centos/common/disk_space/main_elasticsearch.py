from format_common import format_data
import os
import json

es_timestamp = os.getenv("utc-data")
es_point = os.getenv("collect-name")

datas = format_data()

for data in datas:
  data['@timestamp'] = es_timestamp
  data['point'] = es_point
  
print(json.dumps(datas))
