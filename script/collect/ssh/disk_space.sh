cd=$(date "+%Y-%m-%dT%H:%M:%S");df -P | sed '1d' | sed "s/$/  ${cd}/"
