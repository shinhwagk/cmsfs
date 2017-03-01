FROM cmsfs/oraclejdk8

ARG SVC_NAME

ENV SVC_PATH ${SVC_NAME}/impl/target/universal/stage

ADD $SVC_PATH /opt/$SVC_NAME
ADD logback.xml /opt/logback.xml

ENV COMMAND /opt/${SVC_NAME}/bin/${SVC_NAME}-impl

CMD $COMMAND -Dlogger.file=/opt/logback.xml