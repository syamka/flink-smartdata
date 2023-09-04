package ru.avito.flink.example.function;

import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import ru.avito.flink.example.dto.Event;

public class UniqueCounterWithTimerFunction extends KeyedProcessFunction<Integer, Event, Tuple2<Integer, Integer>> {

    private ValueState<Integer> counterState;

    private MapState<Integer, Boolean> itemsState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> counterStateDesc = new ValueStateDescriptor<>("counter", Integer.class);
        StateTtlConfig valuesTtl = StateTtlConfig.newBuilder(Time.days(1))
                .updateTtlOnReadAndWrite()
                .build();
        counterStateDesc.enableTimeToLive(valuesTtl);
        counterState = getRuntimeContext().getState(counterStateDesc);

        MapStateDescriptor<Integer, Boolean> itemsStateDesc = new MapStateDescriptor<>("items", Integer.class, Boolean.class);
        itemsStateDesc.enableTimeToLive(valuesTtl);
        itemsState = getRuntimeContext().getMapState(itemsStateDesc);
    }

    @Override
    public void processElement(Event value, Context ctx, Collector<Tuple2<Integer, Integer>> out) throws Exception {
        Integer counterValue = counterState.value();
        if (counterValue == null) {
            counterValue = 0;
            ctx.timerService().registerEventTimeTimer(value.getTimestamp() + 1000 * 60 * 10);
        }

        Integer itemId = value.getItemId();
        if(itemId != null && itemId > 0 && itemsState.contains(itemId)) {
            return;
        }
        itemsState.put(itemId, true);
        counterState.update(++counterValue);

    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<Integer, Integer>> out) throws Exception {
        Integer counterValue = counterState.value();
        if(counterValue == null || counterValue == 0) {
            return;
        }
        out.collect(new Tuple2<>(ctx.getCurrentKey(), counterValue));
        ctx.timerService().registerEventTimeTimer(timestamp + 1000 * 60 * 10);
        counterState.update(0);
        itemsState.clear();
    }
}
