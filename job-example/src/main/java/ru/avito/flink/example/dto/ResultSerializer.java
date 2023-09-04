package ru.avito.flink.example.dto;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

public class ResultSerializer implements KafkaRecordSerializationSchema<Tuple2<Integer, Integer>> {

    private final String topic;

    public ResultSerializer(String topic) {
        this.topic = topic;
    }

    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(Tuple2<Integer, Integer> element, KafkaSinkContext context, Long timestamp) {
        return new ProducerRecord<>(topic, String.valueOf(element.f0).getBytes(), String.valueOf(element.f1).getBytes());
    }
}
