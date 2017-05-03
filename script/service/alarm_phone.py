from notice_phone import sendPhone
import json

f = open(sys.argv[1], "r")
content = json.loads(f.read())
f.close

f = open(sys.argv[2], "r")
phones = json.loads(f.read())
f.close

sendPhone(phones, content)
