package ru.avito.flink.example.dto;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class BlacklistDeserializer implements KafkaRecordDeserializationSchema<String> {

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<String> out) throws IOException {
        out.collect(new String(record.value()));
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }
}
