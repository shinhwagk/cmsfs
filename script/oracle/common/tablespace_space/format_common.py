import sys
import json
import re

f = open(sys.argv[1], "r")
x = f.read()
f.close

j = json.loads(x)

def get_data():
    return j
