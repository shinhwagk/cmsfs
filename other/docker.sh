# delete image none
docker images | grep none | awk '{print $3}' | while read line; do docker rmi $line; done

# delete image and container none
docker images | grep none | awk '{print $3}' | while read line; do docker rmi $line; done 2>&1 | awk '{print $NF}' | while read line; do docker rm $line; done;
docker images | grep none | awk '{print $3}' | while read line; do docker rmi $line; done
docker images | grep none

# delete container for image name xxx
docker ps -a | grep '[a-z0-9A-Z]\{12\}\s\{8\}[0-9]\{1\}' | awk '{print $1}' | while read line; do docker rm $line; done


# delete container by image name