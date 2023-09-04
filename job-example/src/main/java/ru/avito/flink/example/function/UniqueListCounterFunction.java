package ru.avito.flink.example.function;

import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import ru.avito.flink.example.dto.Event;

public class UniqueListCounterFunction extends KeyedProcessFunction<Integer, Event, Tuple2<Integer, Integer>> {

    private ValueState<Integer> counterState;

    private ListState<Integer> itemsState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> counterStateDesc = new ValueStateDescriptor<>("counter", Integer.class);
        StateTtlConfig valuesTtl = StateTtlConfig.newBuilder(Time.days(1))
                                                .updateTtlOnReadAndWrite()
                                                .build();
        counterStateDesc.enableTimeToLive(valuesTtl);

        counterState = getRuntimeContext().getState(counterStateDesc);

        ListStateDescriptor<Integer> itemsStateDesc = new ListStateDescriptor<Integer>("items", Integer.class);
        itemsStateDesc.enableTimeToLive(valuesTtl);
        itemsState = getRuntimeContext().getListState(itemsStateDesc);
    }

    @Override
    public void processElement(Event value, Context ctx, Collector<Tuple2<Integer, Integer>> out) throws Exception {
        Integer counterValue = counterState.value();
        if (counterValue == null) {
            counterValue = 0;
        }

        Integer itemId = value.getItemId();
        if(itemId != null && itemId > 0) {
            for(Integer iid: itemsState.get()) {
                if(itemId.equals(iid)) {
                    return;
                }
            }
        }

        itemsState.add(itemId);
        counterState.update(++counterValue);
        out.collect(new Tuple2<>(ctx.getCurrentKey(), counterValue));
    }
}
