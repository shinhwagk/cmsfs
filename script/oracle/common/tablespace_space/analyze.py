import sys

f = open(sys.argv[1], "r")
x = f.read()
f.close

print(x)
