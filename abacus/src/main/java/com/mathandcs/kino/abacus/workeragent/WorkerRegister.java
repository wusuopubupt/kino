package com.mathandcs.kino.abacus.workeragent;

import com.mathandcs.kino.workeragent.worker.Worker;
import com.mathandcs.kino.workeragent.worker.WorkerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by dashwang on 2017-06-03
 */
@Component
public class WorkerRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerRegister.class);
    public Map<String, WorkerInfo> workerMap = new HashMap<>();

    public void registerWorkers() throws Exception {
        LOGGER.info("Starting to register workers.");
        try {
            ServiceLoader<Worker> loader = ServiceLoader.load(Worker.class);
            Iterator<Worker> iterator = loader.iterator();
            while (iterator.hasNext()) {
                Worker worker = iterator.next();
                WorkerInfo workerInfo = new WorkerInfo().addClazz(worker.getClass())
                        .addTaskType(worker.getTaskType());
                workerMap.put(worker.getTaskType(), workerInfo);
            }

            for (String taskType : workerMap.keySet()) {
                LOGGER.debug("Found worker: {}", workerMap.get(taskType).getClazz().getCanonicalName());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load workers", e);
        }
        LOGGER.info("Finished to register workers.");
    }

    public Worker getWorkerTypeTaskType(String taskType) throws Exception {
        Worker worker = null;
        try {
            Class<? extends Worker> clazz = workerMap.get(taskType).getClazz();
            if (clazz == null) {
                throw new Exception("No executor available for task type " + taskType);
            }
            worker = clazz.newInstance();
            return worker;
        } catch (Exception e) {
            throw e;
        }
    }
}