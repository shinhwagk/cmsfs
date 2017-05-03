from format_common import format_data
import json
import sys
import os

data = format_data()

f2 = open(sys.argv[2], "r")
args = json.loads(f2.read())
f2.close

hostname = os.getenv("conn-name")
host = os.getenv("conn-ip")

filterData = filter(lambda x: x['Ued%'] >= args["threshold"], data)

contents = list(map(lambda x: "%s(%s) | %s - %s%s" % (hostname,host,d["Filesystem"], d["Ued%"], "%"), filterData))
    
print(contents)
