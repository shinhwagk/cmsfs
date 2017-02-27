FROM cmsfs/oraclejdk8

ARG SVC_NAME

ENV SVC_PATH ${SVC_NAME}/impl/target/universal/stage

ADD $SVC_PATH /opt/$SVC_NAME

ENV COMMAND /opt/${SVC_NAME}/bin/${SVC_NAME}-impl

CMD $COMMAND