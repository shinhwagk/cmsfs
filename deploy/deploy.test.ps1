function scp_cmsfs() {
  cd E:\github\cmsfs;
  git add -A
  git commit -m "test"
  git push
}

scp_cmsfs

$SERVICE_NAME=$args[0]
$SERVICE_IMPL=$args[1]

echo "start execute deploy"
ssh root@10.65.103.63 "cd /opt/cmsfs; git pull; cd deploy; sh deploy.test.sh ${SERVICE_NAME} ${SERVICE_IMPL}"