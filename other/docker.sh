# delete image none
docker images | grep none | awk '{print $3}' | while read line; do docker rmi $line; done

# delete image and container none
docker images | grep none | awk '{print $3}' | while read line; do docker rmi $line; done 2>&1 | awk '{print $NF}' | while read line; do docker rm $line; done;
docker images | grep none | awk '{print $3}' | while read line; do docker rmi $line; done
docker images | grep none
#  | wc -l

# delete container for image name xxx
docker ps -a | grep '[a-z0-9A-Z]\{12\}\s\{8\}[0-9]\{1\}' | awk '{print $1}' | while read line; do docker rm $line; done


# delete container by image name

delete_image() {
  docker rmi $1
}

docker images | grep 'none' | awk '{print $3}' | while read image
do
  docker ps -a | grep $image | awk '{print $1}' | while read container
  do
    docker rm -f $container 
  done
done

