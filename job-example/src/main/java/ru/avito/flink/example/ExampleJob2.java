package ru.avito.flink.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import ru.avito.flink.example.dto.Event;
import ru.avito.flink.example.dto.EventDeserializer;
import ru.avito.flink.example.dto.ResultSerializer;
import ru.avito.flink.example.function.CounterFunction;

public class ExampleJob2 {

    public static void main(String[] args) throws Exception {

        KafkaSource<Event> eventsSource = KafkaSource.<Event>builder()
                .setBootstrapServers("localhost:9093")
                .setTopics("example-smartdata")
                .setDeserializer(new EventDeserializer())
                .setStartingOffsets(OffsetsInitializer.earliest())
                .build();

        KafkaSink<Tuple2<Integer, Integer>> eventsSink = KafkaSink.<Tuple2<Integer, Integer>>builder()
                .setBootstrapServers("localhost:9093")
                .setRecordSerializer(new ResultSerializer("example-smartdata-result"))
                .build();


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromSource(eventsSource, WatermarkStrategy.noWatermarks(), "events")
            .name("Source events")
            .uid("source-events")
            .setParallelism(3)

            .keyBy(Event::getUid)
            .process(new CounterFunction())
            .name("Count events")
            .uid("count-events")

            .sinkTo(eventsSink)
            .name("Sink result")
            .uid("sink-result");


        env.execute("example-job");
    }
}

