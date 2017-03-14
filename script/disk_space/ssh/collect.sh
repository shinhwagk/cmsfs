cd=$(date "+%Y-%m-%dT%H:%M:%SZ"); df -P | sed '1d' | sed "s/$/  ${cd}/"
