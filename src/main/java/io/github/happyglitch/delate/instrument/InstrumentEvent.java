package io.github.happyglitch.delate.instrument;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;
import java.util.NavigableSet;
import java.util.TreeSet;

public class InstrumentEvent implements Comparable<InstrumentEvent> {

    public static class Queue {
        private final TreeSet<InstrumentEvent> queue = new TreeSet<>();

        public synchronized void add(InstrumentEvent... events) {
            queue.addAll(Arrays.asList(events));
        }

        public synchronized boolean isEmpty() {
            return queue.isEmpty();
        }

        public synchronized InstrumentEvent[] pollEventsUntil(long time) {
            NavigableSet<InstrumentEvent> head = queue.headSet(new InstrumentEvent(time, Type.UNKNOWN, 0, 0, 0), true);
            InstrumentEvent[] events = head.toArray(new InstrumentEvent[0]);
            for(int i = 0; i < events.length; i++)
                queue.pollFirst();
            return events;
        }

        /**
         * Converts all events in an array from ticks to frames and then returns the array
         * @param events the array to convert
         * @param resolution resolution in milliseconds per tick
         * @param frameRate frame rate in frames per second
         * @return the array
         */
        public static InstrumentEvent[] convertEventsToFrameTime(InstrumentEvent[] events, double resolution, int frameRate) {
            for(int i = 0; i < events.length; i++) {
                events[i].convertTimeFromTicksToFrames(resolution, frameRate);
            }
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
    private int value1;
    private int value2;

    private long time;

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getValue1() {
        return value1;
    }
    public int getValue2() {
        return value2;
    }

    public long getTime() {
        return time;
    }

    private void convertTimeFromTicksToFrames(double resolution, int frameRate) {
        time = (long)(time * resolution * frameRate / 1000.0);
    }

    public InstrumentEvent(long tick, Type type, int id, int value1, int value2) {
        this.time = tick;
        this.type = type;
        this.id = id;
        this.value1 = value1;
        this.value2 = value2;
    }

    public static InstrumentEvent fromMidiEvent(MidiEvent e) {
        InstrumentEvent instrument = new InstrumentEvent(0, Type.UNKNOWN, 0, 0, 0);
        instrument.setTo(e);
        return instrument;
    }

    private void setTo(MidiEvent e) {
        time = e.getTick();
        if(e.getMessage() instanceof ShortMessage message) {
            value1 = message.getData1();
            value2 = message.getData2();
            id = message.getChannel();
            type = getTypeFromMidiCommand(message.getCommand());
        }
    }

    private Type getTypeFromMidiCommand(int midiCommand) {
        if(value2 == 0 && midiCommand == ShortMessage.NOTE_ON)
            return Type.NOTE_OFF;
        return switch (midiCommand) {
            case ShortMessage.NOTE_ON -> Type.NOTE_ON;
            case ShortMessage.NOTE_OFF -> Type.NOTE_OFF;
            case ShortMessage.POLY_PRESSURE -> Type.NOTE_TOUCH;
            default -> Type.UNKNOWN;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstrumentEvent)) return false;

        InstrumentEvent that = (InstrumentEvent) o;

        if (id != that.id) return false;
        if (value1 != that.value1) return false;
        if (value2 != that.value2) return false;
        if (time != that.time) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + value1;
        result = 31 * result + value2;
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public int compareTo(InstrumentEvent o) {
        return (int)(time - o.time);
    }
}
