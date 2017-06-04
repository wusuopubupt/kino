#!/bin/bash
set -xe
class_path="../target/workeragent-0.0.1-SNAPSHOT.jar:../target/dependency/*"
entrance="com.mathandcs.kino.workeragent.WorkerAgent"
spring_profile="prd"
server_port="8080"
log_dir="../logs"
log_name="workeragent.log"

echo "start the $entrance server"

if [ ! -f $log_dir ];then
	mkdir -p $log_dir
fi

nohup java -cp $class_path $entrance \
		--spring.profiles.active=$spring_profile \
		--server.port=$server_port \
		--logging.level.root=INFO 2>&1 > "$log_dir/$log_name" &

