function scp_cmsfs() {
  cd E:\github\cmsfs;
  git add -A
  git commit -m "test"
  git push
}

scp_cmsfs

$SERVICE_NAME=$args[0]
$SERVICE_IMPL=$args[1]
$SERVICE_BUILD=$args[2]

echo "start execute deploy"
# ssh root@10.65.103.63 "cd /opt/cmsfs; git pull; cd deploy; sh deploy.test.sh ${SERVICE_NAME} ${SERVICE_IMPL} ${SERVICE_BUILD}"
ssh root@10.65.103.63 "cd /opt/cmsfs/deploy; docker-compose -p cmsfs -f docker-compose.test.yml up -d ${SERVICE_NAME}"