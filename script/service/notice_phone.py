import http.client
import urllib.parse
import time

def sendPhone(phones,content):
  orderNo = int(time.time() * 1000)
  params = urllib.parse.urlencode({'appId': "TOC", 'orderNo': orderNo, 'protocol': 'S',
                                  'targetCount': len(phones), 'targetIdenty': ';'.join(phones), 'content': content, 'isRealTime': 'true'})
  headers = {"Content-type": "application/x-www-form-urlencoded; charset=utf-8"}
  conn = http.client.HTTPConnection("10.65.209.12", 8380)
  conn.request("POST", "/mns-web/services/rest/msgNotify", params, headers)
  response = conn.getresponse()
  print(response.status, response.reason)
