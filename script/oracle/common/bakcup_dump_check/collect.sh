#!/bin/bash
LS='/bin/ls'

bak_local="/bak_nfs"

today=`date "+%Y%m%d"`

fileCnt=`${LS} ${bak_local} | grep -e "${today}.log" | wc -l`

rs=""

if [[ ${fileCnt} -gt 0 ]]; then
  fileName=`${LS} ${bak_local} | grep -e "${today}.log"`
  tail -n 1 "${bak_local}/${fileName}" | grep 'successfully' > /dev/null 2>&1
  if [[ $? == 0 ]]; then
    rs="[\"${fileName}\",\"successfully\"]"
  else
    rs="[\"${fileName}\",\"failure\"]"
  fi
else
  rs="[\"${fileName}\",\"no exist\"]"
fi

echo "${rs}"