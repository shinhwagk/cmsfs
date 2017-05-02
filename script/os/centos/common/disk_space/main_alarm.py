from format_common import format_data
from notify_email import mail_send
import sys

data = format_data()

f2 = open(sys.argv[2], "r")
args = f2.read()
f2.close

filterData = filter(lambda x: x['Ued%'] >= int(args), data)

for d in filterData:
    content = "%s - %s%s" % (d["Filesystem"], d["Ued%"], "%")
    mail_send("x", "disk_space", content)
