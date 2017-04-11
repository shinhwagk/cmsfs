#!/bin/bash
python register.py && /opt/cmsfs/${SERVICE_NAME}-impl-1.0-SNAPSHOT/bin/${BOOTSTRAP_FILE} $@;