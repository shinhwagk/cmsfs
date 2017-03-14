cd=$(date "+%Y-%m-%dT%H:%M:%S+08:00"); df -P | sed '1d' | sed "s/$/  ${cd}/"
