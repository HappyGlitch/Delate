package io.github.happyglitch.delate.audio.input.synth.filter;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

import java.util.LinkedList;

public class Delay extends SynthModule {
    private SynthModule signal;
    private final LinkedList<Float> previousFrames = new LinkedList<>();
    private int maxSize;
    private int currentSize = 0;

    public Delay(SynthModule signal, float amountInMilliseconds, SynthModule.Info info) {
        super(info);
        this.signal = signal;
        maxSize = (int)((double)amountInMilliseconds / 1000 * info.getFrameRate());
    }

    @Override
    protected float generate() {
        previousFrames.add(signal.generateNextFrame());
        if(currentSize >= maxSize)
            return previousFrames.removeFirst();
        currentSize++;
        return 0;
    }
}
