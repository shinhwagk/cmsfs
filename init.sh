#!/bin/bash

## machine
curl http://10.65.103.15:50741/v1/machine -d '{"name":"yali2","tags":"[]","ip":"10.65.193.25","state":true}'
curl http://10.65.103.15:50741/v1/machine -d '{"name":"yali3","tags":"[]","ip":"10.65.193.26","state":true}'

## connector

# id: Option[Int], machineId: Int, tags: Seq[String], name: String, url: String, user: String, password: String,
                            #  category: String, categoryVersion: String,
                            #  state: Boolean
curl http://10.65.103.15:50741/v1/connector/jdbc -d '{"machineId":1,"tags":"[]","name":"10.65.193.25","state":true}'
curl http://10.65.103.15:50741/v1/connector/ssh -d '{"machineId":1,"tags":"[]","user":"oracle","post":22}'