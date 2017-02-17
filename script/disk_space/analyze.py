import sys
import json
import re

# "Filesystem" "Size" "Used" "Avail" "Use%" "Mounted on" "Timestamp"

f = open(sys.argv[1], "r")
x = f.read()
f.close

j = json.loads(x)


def split_str(y):
    row = re.split(r"\s+", y)
    return {"Filesystem": row[0], "Size": int(row[1]), "Used": int(row[2]), "Avail": int(row[3]), "Ued%": int(row[4][:-1]), "Mounted on": row[5], "Timestamp": row[6]}

g = list(map(split_str, j))
data = json.dumps(g)
print(data)
