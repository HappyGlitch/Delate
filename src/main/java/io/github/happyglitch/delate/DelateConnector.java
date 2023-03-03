package io.github.happyglitch.delate;

import io.github.happyglitch.delate.audio.input.AudioInput;
import io.github.happyglitch.delate.audio.output.AudioOutput;
import io.github.happyglitch.delate.instrument.input.InstrumentInput;
import io.github.happyglitch.delate.instrument.output.InstrumentOutput;

import java.util.ArrayList;

public class DelateConnector {
    private AudioInput audioInput;
    private ArrayList<AudioOutput> audioOutputs = new ArrayList<>();
    private InstrumentInput instrumentInput;
    private ArrayList<InstrumentOutput> instrumentOutputs = new ArrayList<>();

    private boolean realtime = false;

    public DelateConnector(AudioInput audio, InstrumentInput instrument) {
        this.audioInput = audio;
        this.instrumentInput = instrument;
        if(audio.isRealtime() || instrument.isRealtime())
            realtime = true;
    }

    public synchronized DelateConnector registerAudio(AudioOutput output) {
        if(!audioOutputs.isEmpty() && audioOutputs.get(0).getBufferSizeInBytes() != output.getBufferSizeInBytes())
            throw new IllegalArgumentException("Audio output buffer size doesn't match the previous outputs!");
        audioOutputs.add(output);
        if(output.isRealtime())
            realtime = true;
        return this;
    }

    public synchronized DelateConnector registerInstrument(InstrumentOutput output) {
        instrumentOutputs.add(output);
        if(output.isRealtime())
            realtime = true;
        return this;
    }

    public int getBufferSize() {
        if(audioOutputs.isEmpty())
            return -1;
        return audioOutputs.get(0).getBufferSizeInBytes();
    }

    private boolean running = false;
    /**
     * Starts transfer and blocks the thread until transfer ends.
     */
    public synchronized void start() {
        running = true;

    }

    /**
     * Makes the transfer stop at the start of the next buffer
     * (buffer size can be checked using {@link #getBufferSize()} method).
     */
    public void stop() {
        running = false;
    }
}
