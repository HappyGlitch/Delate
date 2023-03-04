package io.github.happyglitch.delate.instrument.input;

import io.github.happyglitch.delate.instrument.InstrumentEvent;

public interface InstrumentInput {
    public InstrumentEvent[] readInput(int lengthInFrames, int frameRate);
    public boolean isRealtime();
}
