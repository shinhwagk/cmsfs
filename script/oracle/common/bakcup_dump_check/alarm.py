import json
import sys

def readFileData(file):
  f = open(file, "r")
  x = f.read()
  f.close
  return x

collectData = readFileData(sys.argv[1])

rs = [{"mail": collectData}]

print(json.dumps(rs))
