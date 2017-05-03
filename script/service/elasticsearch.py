import sys
import json
import re

f = open(sys.argv[1], "r")
datas = json.loads(f.read())
f.close

for data in datas:
  print(data)
