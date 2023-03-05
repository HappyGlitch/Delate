package io.github.happyglitch.delate.audio.input.generator;

import io.github.happyglitch.delate.audio.input.AudioInput;
import io.github.happyglitch.delate.instrument.InstrumentEvent;
import io.github.happyglitch.delate.instrument.MIDIUtility;

public class TestSynth implements AudioInput {
    double frequency = 0;
    int time = 0;
    @Override
    public float[] readFrames(int framesLength, int sampleRate, InstrumentEvent[] events) {
        float[] result = new float[framesLength];
        for(int i = 0; i < framesLength; i++) {
            updateFrequency(events, i);
            time++;
            result[i] = (float)Math.sin(Math.PI * 2 * i * frequency / sampleRate);
        }
        return result;
    }

    private void updateFrequency(InstrumentEvent[] events, int frame) {
        for(InstrumentEvent event: events) {
            if(frame != event.getTime())
                continue;
            if(event.getType() == InstrumentEvent.Type.NOTE_ON)
                frequency = MIDIUtility.convertNoteToFrequency(event.getValue1());
            time = 0;
        }
    }

    @Override
    public boolean isRealtime() {
        return false;
    }
}
