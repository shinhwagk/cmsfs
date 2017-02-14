import sys
import json
import re

# Filesystem Size Used Avail Use% Mounted on

f = open(sys.argv[1], "r")
x = f.read()
j = json.loads(x)


def split_str(y):
    row = re.split(r"\s+", y)
    return {"Filesystem": row[0], "Size": row[1], "Used": row[2], "Avail": row[3], "Ued%": row[4], "Mounted on": row[5]}

g = list(map(split_str, j))

print(g)
