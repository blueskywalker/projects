package com.sysomos.grid.tools.backup.twitter;

import com.sysomos.grid.tools.backup.BackupProducer;
import com.sysomos.grid.tools.backup.BackupTool;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import java.util.concurrent.BlockingQueue;

/**
 * Created by kkim on 5/22/15.
 */
public class TwitterProducer extends BackupProducer {
    final BlockingQueue<String> compliance;
    public TwitterProducer(KafkaStream<byte[], byte[]> stream, BackupTool tool) {
        super(stream, tool);
        compliance = ((TwitterBackupTool)tool).getCompliance();
    }

    @Override
    public void run() {

        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

        while (iterator != null && iterator.hasNext() && !tool.isDone()) {
            try {
                String msg = new String(iterator.next().message());
                if(TweetDetector.isCompliance(msg)) {
                    compliance.put(msg);
                } else {
                    queue.put(msg);
                }
            } catch (InterruptedException e) {
                logger.error("QUEUE PUT FAIL", e);
            }
        }
        tool.setDone(true);
        logger.info(String.format("Producer (%d) is done.", getId()));
    }

}