cd=$(date "+%Y-%m-%dT%H:%M:%S");df -h | sed '1d' | sed "s/$/  ${cd}/"
