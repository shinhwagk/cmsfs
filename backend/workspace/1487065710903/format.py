import sys
import json
import re

# Filesystem Size  Used Avail Use% Mounted on

f = open(sys.argv[1], "r")
x = f.read()
j = json.loads(x)


def split_str(y):
    return re.split(r"\s+", y)

g = list(map(split_str, j))

print(g)