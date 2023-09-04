package ru.avito.flink.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import ru.avito.flink.example.dto.BlacklistDeserializer;
import ru.avito.flink.example.dto.Event;
import ru.avito.flink.example.dto.EventDeserializer;
import ru.avito.flink.example.dto.ResultSerializer;
import ru.avito.flink.example.function.BlacklistFilterFunction;
import ru.avito.flink.example.function.CounterFunction;
import ru.avito.flink.example.function.CounterWithTtlFunction;

import static ru.avito.flink.example.function.BlacklistFilterFunction.BROADCAST_BLACKLIST_STATE_DESC;

public class ExampleJob5 {

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

        KafkaSource<String> blacklistsSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9093")
                .setTopics("example-smartdata-blacklist")
                .setDeserializer(new BlacklistDeserializer())
                .setStartingOffsets(OffsetsInitializer.earliest())
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromSource(eventsSource, WatermarkStrategy.noWatermarks(), "events")
                .name("Source events")
                .uid("source-events")
                .setParallelism(3)

                .filter(e -> e.getUid() != null)
                .name("Filter by uid")
                .uid("filter-by-uid")

                .connect(
                    env.fromSource(blacklistsSource, WatermarkStrategy.noWatermarks(), "blacklists")
                        .name("Source blacklists")
                        .uid("source-blacklists")

                        .setParallelism(1)
                        .broadcast(BROADCAST_BLACKLIST_STATE_DESC)
                )
                .process(new BlacklistFilterFunction())
                .name("Filter blacklist ips")
                .uid("filter-blacklist-ips")

                .keyBy(Event::getUid)
                .process(new CounterWithTtlFunction())
                .name("Count events")
                .uid("count-events")

                .sinkTo(eventsSink)
                .name("Sink result")
                .uid("sink-result");


        env.execute("example-job");
    }
}

