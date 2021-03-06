package com.sysomos.grid.tools.backup;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kkim on 5/22/15.
 */
public class BackupProducer extends Thread {
    public static Logger logger = Logger.getLogger(BackupProducer.class);

    protected final KafkaStream<byte[], byte[]> stream;
    protected final BlockingQueue<String> queue;
    protected final BackupTool tool;

    public BackupProducer(final KafkaStream<byte[], byte[]> stream, final BackupTool tool) {
        this.stream = stream;
        this.tool=tool;
        this.queue = tool.getQueue();
    }

    @Override
    public void run() {

        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

        while (iterator != null && iterator.hasNext() && !tool.isDone()) {
            try {
                queue.put(new String(iterator.next().message()));
            } catch (InterruptedException e) {
                logger.error("QUEUE PUT FAIL", e);
            }
        }
        tool.setDone(true);
        logger.info(String.format("Producer (%d) is done.", getId()));
    }

}
