package io.github.happyglitch.delate.audio.input.synth.filter;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

public abstract class Filter extends SynthModule {
    private SynthModule cutoff;
    private SynthModule resonance;
    private SynthModule signal;

    public final Filter setCutoff(SynthModule cutoff) {
        this.cutoff = cutoff;
        return this;
    }
    public final Filter setResonance(SynthModule resonance) {
        this.resonance = resonance;
        return this;
    }
    public final Filter setSignal(SynthModule signal) {
        this.signal = signal;
        return this;
    }

    @Override
    public final float generate() {
        return filterSignal(signal.generateNextFrame(), resonance.generateNextFrame(), cutoff.generateNextFrame());
    }

    protected abstract float filterSignal(float signal, float resonance, float cutoff);

    public Filter(SynthModule.Info info) {
        super(info);
        cutoff = info.getCreator().constant(880);
        resonance = info.getCreator().constant(0);
    }
}
