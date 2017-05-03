from format_common import format_data
import json
import sys

data = format_data()

f2 = open(sys.argv[2], "r")
args = json.loads(f2.read())
f2.close

filterData = filter(lambda x: x['Ued%'] >= args["threshold"], data)

for d in filterData:
    content = "%s - %s%s" % (d["Filesystem"], d["Ued%"], "%")
    print(content)
