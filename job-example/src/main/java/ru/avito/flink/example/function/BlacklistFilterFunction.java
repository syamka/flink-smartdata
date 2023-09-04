package ru.avito.flink.example.function;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import ru.avito.flink.example.dto.Event;

public class BlacklistFilterFunction  extends BroadcastProcessFunction<Event, String, Event> {

    public static final MapStateDescriptor<String, Boolean> BROADCAST_BLACKLIST_STATE_DESC =
            new MapStateDescriptor<>("blacklists", String.class, Boolean.class);

    @Override
    public void processElement(Event value, ReadOnlyContext ctx, Collector<Event> out) throws Exception {
        if(ctx.getBroadcastState(BROADCAST_BLACKLIST_STATE_DESC).get(value.getIp()) == null) {
            out.collect(value);
        }
    }

    @Override
    public void processBroadcastElement(String value, Context ctx, Collector<Event> out) throws Exception {
        ctx.getBroadcastState(BROADCAST_BLACKLIST_STATE_DESC).put(value, true);
    }
}
