FROM cmsfs/oraclejdk8

ARG SVC_NAME

LABEL traefik.backend=cmsfs-$SVC_NAME
LABEL traefik.frontend.rule=Host:$SVC_NAME.cmsfs.org
LABEL traefik.port=9000

ADD ${SVC_NAME}/impl/target/universal/stage /opt/$SVC_NAME
ADD logback.xml /opt/logback.xml

ENV COMMAND /opt/${SVC_NAME}/bin/${SVC_NAME}-impl

EXPOSE 9000

ENTRYPOINT $COMMAND