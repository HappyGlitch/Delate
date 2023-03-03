package io.github.happyglitch.delate.instrument.output;

import io.github.happyglitch.delate.instrument.InstrumentEvent;

public interface InstrumentOutput {
    public void stream(InstrumentEvent[] t);
    public boolean isRealtime();
    public void close();
}
