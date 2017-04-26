import sys
import json
import re

# "Filesystem" "Size" "Used" "Avail" "Use%" "Mounted on"

f = open(sys.argv[1], "r")
x = f.read()
f.close

f2 = open(sys.argv[2], "r")
x2 = f2.read()
f2.close

j = json.loads(x)


def split_str(y):
    row = re.split(r"\s+", y)
    return {"Filesystem": row[0], "Size": int(row[1]), "Used": int(row[2]), "Avail": int(row[3]), "Ued%": int(row[4][:-1]), "Mounted on": row[5]}

g = list(map(split_str, j))

xr = filter(lambda x: x['Ued%'] >= int(x2), g)

rs = map(lambda y: {"mail": ("%s - %s%s") %
                    (y["Filesystem"], y["Ued%"], "%"), "mobile": ("%s - %s%s") %
                    (y["Filesystem"], y["Ued%"], "%")}, list(xr))
data = json.dumps(list(rs))
print(data)
