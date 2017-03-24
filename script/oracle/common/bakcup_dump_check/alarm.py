import json
import sys

def readFileData(file):
  f = open(file, "r")
  x = f.read()
  f.close
  return x

collectData = readFileData(sys.argv[1])
notificationData = json.loads(readFileData(sys.argv[2]))

n = [[notificationData[0],notificationData[1],collectData]]

print(json.dumps(n))