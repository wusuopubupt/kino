#!/bin/bash

executor_memory="20g"
executor_num=5
executor_vcore=3
driver_memory="10g"

spark-submit \
	--class com.mathandcs.sparkapp.Entrance \
	--name "myApp_${driver_memory}_${executor_memory}_${executor_num}_${executor_vcore}" \
	--master yarn \
	--deploy-mode cluster \
	--executor-memory $executor_memory\
	--num-executors $executor_num \
	--executor-cores $executor_vcore \
	--driver-memory $driver_memory\
	--conf spark.driver.maxResultSize=$driver_memory \
	--conf spark.driver.extraJavaOptions="-XX:+HeapDumpOnOutOfMemoryError -XX:MaxPermSize=4096m -XX:PermSize=4096m -Dcom.sun.management.jmxremote.port=10010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false  -Dcom.sun.management.jmxremote.local.only=false  -XX:+UseG1GC -XX:+PrintFlagsFinal -XX:+PrintReferenceGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintAdaptiveSizePolicy -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark  -XX:G1HeapRegionSize=8m -XX:+ParallelRefProcEnabled -XX:-ResizePLAB" \
	--conf spark.executor.extraJavaOptions="-XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+UseG1GC -XX:+PrintGCTimeStamps" \
	--conf spark.yarn.driver.memoryOverhead=4096 \
	--conf spark.yarn.executor.memoryOverhead=4096 \
	--conf spark.yarn.maxAppAttempts=1 \
	--conf spark.network.timeout=800 \
	--conf spark.sql.broadcastTimeout=1200 \
	--files ./config.json \
	--jars /usr/local/spark/lib/datanucleus-api-jdo-3.2.6.jar,/usr/local/spark/lib/datanucleus-core-3.2.10.jar,/usr/local/spark/lib/datanucleus-rdbms-3.2.9.jar \
	./spark-app.jar ./config.json
