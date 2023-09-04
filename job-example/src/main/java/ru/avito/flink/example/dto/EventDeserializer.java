package ru.avito.flink.example.dto;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectReader;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class EventDeserializer implements KafkaRecordDeserializationSchema<Event> {

    private ObjectReader reader = new ObjectMapper().readerFor(Event.class);

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<Event> out) {
        try {
            out.collect(reader.readValue(record.value()));
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public TypeInformation<Event> getProducedType() {
        return TypeInformation.of(Event.class);
    }
}
