package com.mathandcs.kino.abacus.streaming.runtime.io.channel.memory;

import com.mathandcs.kino.abacus.streaming.api.common.UniqueId;
import com.mathandcs.kino.abacus.streaming.runtime.io.channel.Producer;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MemoryProducer<T> implements Producer<T> {

    private final static Logger LOG = LoggerFactory.getLogger(MemoryProducer.class);

    private final static int CAPACITY = 100;

    private Set<UniqueId> targetChannelIds = new HashSet<>();

    private Map<UniqueId/*queue id*/, Queue<Object> /*queue entity*/> channelMap;

    public MemoryProducer(
            Collection<UniqueId> outputQueueIds,
            Map<UniqueId, Queue<Object>> msgQueueMap) {

        this.targetChannelIds.addAll(outputQueueIds);
        this.channelMap = msgQueueMap;

        outputQueueIds.forEach(qidItem -> {
            Queue<Object> queue = msgQueueMap.get(qidItem);
            if (null == queue) {
                queue = new ConcurrentLinkedQueue<>();
                msgQueueMap.put(qidItem, queue);
            }
        });
    }

    @Override
    public void produce(T payload, UniqueId targetChannelId) {
        Queue<Object> queue = channelMap.get(targetChannelId);
        if (null == queue) {
            channelMap.put(targetChannelId, new ConcurrentLinkedQueue<>());
        }

        while (queue.size() > CAPACITY) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {
                //
            }
        }

        queue.add(new StreamRecord(payload, System.currentTimeMillis()));
    }
}
