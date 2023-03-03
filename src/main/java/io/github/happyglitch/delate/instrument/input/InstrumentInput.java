package io.github.happyglitch.delate.instrument.input;

import io.github.happyglitch.delate.instrument.InstrumentEvent;

public interface InstrumentInput {
    public InstrumentEvent[] readInputUntil(int time);
    public boolean isRealtime();
}
