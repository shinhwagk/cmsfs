import sys
import json

f = open(sys.argv[1], "r")
x = f.read()
f.close

j = json.loads(x)

def format_type(row):
  row["USED_PCT"] = float(row["USED_PCT"])
  row["FREE_MB"] = float(row["FREE_MB"])
  row["USED_MB"] = float(row["USED_MB"])
  row["TOTAL_MB"] = float(row["TOTAL_MB"])
  return row

g = list(map(format_type, j))

data = json.dumps(g)

print(x)
