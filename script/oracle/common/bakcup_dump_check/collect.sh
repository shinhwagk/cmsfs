#!/bin/bash
bak_local="/bak_nfs"

. /u03/tools/monitor/env

if [[ ! -d ${bak_local} ]]; then
  echo "${bak_local} dir no exist".
  exit 0;
fi

LS='/bin/ls'

today=`date "+%Y%m%d"`

fileCnt=`${LS} ${bak_local} | grep -e "${today}.log" | wc -l`

fileName="full_${hostname}_${today}.dmp"
fileLogName="full_${hostname}_${today}.log"

if [[ ${fileCnt} -gt 0 ]]; then
  tail -n 1 "${bak_local}/${fileLogName}" | grep 'successfully' > /dev/null 2>&1
  if [[ $? == 0 ]]; then
    echo "${fileName}, successfully"
  else
    echo "${fileName}, failure"
  fi
else
  echo "${fileName}, no exist"
fi
