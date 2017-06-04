#!/bin/bash -xe
ENTRANCE="com._4paradigm.nodeagent.Application"

# kill
ps aux | grep ${ENTRANCE} | grep -v grep | grep -v kill | awk '{print $2}' | xargs kill
