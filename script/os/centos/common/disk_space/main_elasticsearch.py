from format_common import format_data
import os
import json

es_timestamp = os.getenv("utcDate")
es_point = os.getenv("connectName")

datas = format_data()

for data in datas:
  data['@timestamp'] = es_timestamp
  data['point'] = es_point
  
return json.dumps(datas)
