package ru.avito.flink.example.function;

import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import ru.avito.flink.example.dto.Event;

public class CounterWithTtlFunction extends KeyedProcessFunction<Integer, Event, Tuple2<Integer, Integer>> {

    private ValueState<Integer> counterState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> counterStateDesc = new ValueStateDescriptor<>("counter", Integer.class);
        StateTtlConfig valuesTtl = StateTtlConfig.newBuilder(Time.days(1))
                                                .updateTtlOnReadAndWrite()
                                                .build();
        counterStateDesc.enableTimeToLive(valuesTtl);
        counterState = getRuntimeContext().getState(counterStateDesc);
    }

    @Override
    public void processElement(Event value, Context ctx, Collector<Tuple2<Integer, Integer>> out) throws Exception {
        Integer counterValue = counterState.value();
        if (counterValue == null) {
            counterValue = 0;
        }
        counterState.update(++counterValue);
        out.collect(new Tuple2<>(ctx.getCurrentKey(), counterValue));
    }
}