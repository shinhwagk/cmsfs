#!/bin/bash
#-------------------------------------------#
#  DATE: 2017 02 27                         #
#  MAINTAINER: shinhwagk <191631513@qq.com> #
#-------------------------------------------#

set -x
set -e

BASE_HOME=`cd $(dirname $0)/../; pwd`;
DEPLOY_HOME="${BASE_HOME}/deploy"
PROJECT_HOME="${BASE_HOME}/cmsfs"
DEPLOY_PROJECT_HOME="${DEPLOY_HOME}/cmsfs"

function command_check() {
  command -v $1 >/dev/null 2>&1 || { echo >&2 "command: $1, no exist "; exit 1; }
}

function init() {
  command_check "git"; command_check "docker"; #command_check "java"; command_check "tar";
  # git reset --hard; git clean -xfd;
  git pull;
}

function build_service() {
  sbt clean; sbt "${1}-impl"/universal:packageZipTarball || echo "${1}-impl build fail."; exit 1;
}

function build_for_service() {
  rm -fr ${DEPLOY_HOME}/${1}; cp -r ${PROJECT_HOME}/${1}/impl/target/universal/stage/ ${DEPLOY_HOME}/
}

function build_docker_image() {
  PATH="${1}/impl/target/universal/stage"
  docker build -t cmsfs/${1} --build-arg SVC_NAME=${1} --build-arg SVC_PATH=${PATH} .
}

function package_all_service() {
  cp -r ${PROJECT_HOME} ${DEPLOY_HOME}
  docker run -t --rm -v ${DEPLOY_PROJECT_HOME}/cmsfs:/opt/cmsfs -v /root/.ivy2:/root/.ivy2 sbt:0.13.13 sh -c "cd /opt/cmsfs; sbt clean; sbt stage"
  rm -fr ${DEPLOY_PROJECT_HOME}
}

function package_for_service() {
  sbt_service_name=${1}-impl
  docker run -t --rm -v ${PROJECT_HOME}:/opt/cmsfs -v /root/.ivy2:/root/.ivy2 sbt:0.13.13 sh -c "cd /opt/cmsfs; sbt ${sbt_service_name}/clean; sbt ${sbt_service_name}/stage"
}

function start_all_service() {
  docker-compose -p cmsfs up --build
}

# init
# build_all_service

# #
# docker-compose -p cmsfs up --build
function help(){
  echo -e "
    -h | -help   print help
    --build-all  build All service
    --build      build service. eg: ./deploy.sh --build config
  "
}

function process_args(){
  case "$1" in
    -h|-help)       help; exit 1 ;;
    --package-all)  package_all_service ;;
    --package)      package_for_service $2;;
    --build-all)    build_all_service ;;
    --build)        build_for_service $2 ;;
    --start-all)    start_all_service ;;
    *)              help; exit 1;;
    esac
}



cd $DEPLOY_HOME;
process_args $@