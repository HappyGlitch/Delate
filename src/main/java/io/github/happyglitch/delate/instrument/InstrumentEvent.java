package io.github.happyglitch.delate.instrument;

import java.util.NavigableSet;
import java.util.TreeSet;

public class InstrumentEvent implements Comparable<InstrumentEvent> {

    public static class Queue {
        private TreeSet<InstrumentEvent> queue = new TreeSet<>();

        public synchronized void add(InstrumentEvent... events) {
            for(InstrumentEvent event: events) {
                queue.add(event);
            }
        }

        public synchronized boolean isEmpty() {
            return queue.isEmpty();
        }

        public synchronized InstrumentEvent[] pollEventsUntil(long tick) {
            NavigableSet<InstrumentEvent> head = queue.headSet(new InstrumentEvent(tick, Type.UNKNOWN, 0, 0), true);
            InstrumentEvent[] events = head.toArray(new InstrumentEvent[head.size()]);
            for(int i = 0; i < events.length; i++)
                queue.pollFirst();
            return events;
        }
    }

    public static enum Type {
        NOTE_ON,
        NOTE_OFF,
        NOTE_TOUCH,
        SET_SYNTH_PROPERTY,
        UNKNOWN
    }

    private Type type;
    private int id;
    private int value;
    private long tick;

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public long getTick() {
        return tick;
    }

    public InstrumentEvent(long tick, Type type, int id, int value) {
        this.tick = tick;
        this.type = type;
        this.id = id;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstrumentEvent that = (InstrumentEvent) o;

        if (id != that.id) return false;
        if (value != that.value) return false;
        if (tick != that.tick) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id;
        result = 31 * result + value;
        result = 31 * result + (int) (tick ^ (tick >>> 32));
        return result;
    }

    @Override
    public int compareTo(InstrumentEvent o) {
        return (int)(tick - o.tick);
    }
}
