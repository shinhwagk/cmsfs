#!/bin/bash
#-------------------------------------------#
#  DATE: 2017 02 27                         #
#  MAINTAINER: shinhwagk <191631513@qq.com> #
#-------------------------------------------#
BASE_HOME=`cd $(dirname $0); pwd`

function command_check() {
  command -v $1 >/dev/null 2>&1 || { echo >&2 "command: $1, no exist "; exit 1; }
}

function init() {
  command_check "git"; # command_check "sbt"; command_check "java"; command_check "tar";
  cd $BASE_HOME; git reset --hard; git clean -xfd; git pull;
}

function build_service() {
  cd $BASE_HOME; sbt clean; sbt "${1}-impl"/universal:packageZipTarball || echo "${1}-impl build fail."; exit 1;
}

function build_all_service() {
  cd $BASE_HOME; sbt "${1}-impl/stage" || echo "build cmsfs fail."; exit 1;
}

function build_docker_image() {
  cd $BASE_HOME;
  PATH="${1}/impl/target/universal/stage"
  docker build -t cmsfs/${1} --build-arg SVC_NAME=${1} --build-arg SVC_PATH=${PATH} .
}

function build_all_service(){
  docker run -t --rm -v ${BASE_HOME}:/opt/cmsfs -v /root/.ivy2:/root/.ivy2 sbt:0.13.13 sh -c "cd /opt/cmsfs; sbt stage"
}

# SERVICE_ALARM="alarm"
# SERVICE_CONFIG="config"
# SERVICE_FORMAT="format"
# SERVICE_MONITOR="monitor"

# function get_service_path(){
#   echo "${BASE_HOME}/${1}"
# }

# function get_service_zip_path(){
#   service_path=`get_service_path $1`
#   echo "${service_path}/impl/target/universal/${1}-api_2.11-1.0-SNAPSHOT.zip"
# }
init
build_all_service