package io.github.happyglitch.delate.audio.input;

import io.github.happyglitch.delate.instrument.InstrumentEvent;

public interface AudioInput {
    public float[] readFrames(int framesLength, InstrumentEvent[] events);
    public boolean isRealtime();
}
