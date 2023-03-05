package io.github.happyglitch.delate;

import io.github.happyglitch.delate.audio.AudioDepthConverter;
import io.github.happyglitch.delate.audio.input.AudioInput;
import io.github.happyglitch.delate.audio.output.AudioOutput;
import io.github.happyglitch.delate.instrument.InstrumentEvent;
import io.github.happyglitch.delate.instrument.input.InstrumentInput;
import io.github.happyglitch.delate.instrument.output.InstrumentOutput;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;

public class DelateConnector {
    private AudioInput audioInput;
    private ArrayList<AudioOutput> audioOutputs = new ArrayList<>();
    private InstrumentInput instrumentInput;
    private ArrayList<InstrumentOutput> instrumentOutputs = new ArrayList<>();

    public DelateConnector(AudioInput audio, InstrumentInput instrument) {
        this.audioInput = audio;
        this.instrumentInput = instrument;
    }

    public synchronized DelateConnector registerAudio(AudioOutput output) {
        if(!audioOutputs.isEmpty() && !getFormat().equals(output.getFormat()))
            throw new IllegalArgumentException("Audio outputs need to have the same format.");
        audioOutputs.add(output);
        return this;
    }

    public synchronized DelateConnector registerInstrument(InstrumentOutput output) {
        instrumentOutputs.add(output);
        return this;
    }

    public int getBufferSizeInBytes() {
        if(audioOutputs.isEmpty())
            return -1;
        return audioOutputs.get(0).getBufferSizeInBytes();
    }

    public int getBufferSizeInFrames() {
        return getBufferSizeInBytes() / getFormat().getFrameSize();
    }

    public int getWriteSizeInFrames() {
        return getBufferSizeInFrames() / WRITES_PER_BUFFER;
    }

    public AudioFormat getFormat() {
        return audioOutputs.get(0).getFormat();
    }

    private boolean running = false;
    private final int WRITES_PER_BUFFER = 2;
    /**
     * Starts transfer and blocks the thread until transfer ends.
     */
    public synchronized void start() {
        running = true;
        int allowedDelay = getBufferSizeInBytes() / 3;
        int frameOffset = 1;
        while(running) {
            frameOffset++;
            frameOffset %= WRITES_PER_BUFFER * 2;

            InstrumentEvent[] events = instrumentInput.readInput(getWriteSizeInFrames(), (int)getFormat().getSampleRate());
            float[] frames = audioInput.readFrames(getWriteSizeInFrames(), (int)getFormat().getSampleRate(), events);
            byte[] buffer = AudioDepthConverter.convertFloatTo(frames, getFormat().getSampleSizeInBits());
            for(AudioOutput audio: audioOutputs) {
                if(frameOffset == 0 && audio.getTimeDelayStatus() > allowedDelay)
                    System.out.println("DelateConnector is low on time. Buffer size may be too small.");
                audio.stream(buffer);
            }
            for(InstrumentOutput instrument: instrumentOutputs) {
                instrument.stream(events);
            }
        }
    }

    /**
     * Makes the transfer stop at the start of the next buffer
     * (buffer size can be checked using the {@link #getBufferSizeInBytes()} method).
     */
    public void stop() {
        running = false;
    }
}
