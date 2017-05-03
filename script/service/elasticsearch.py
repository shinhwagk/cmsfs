import sys
import json
import re

f = open(sys.argv[1], "r")
datas = json.loads(f.read())
f.close

f = open(sys.argv[2], "r")
args = json.loads(f.read())
f.close

