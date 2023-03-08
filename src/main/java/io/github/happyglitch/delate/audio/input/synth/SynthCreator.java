package io.github.happyglitch.delate.audio.input.synth;

import io.github.happyglitch.delate.audio.input.synth.generator.Oscillator;
import io.github.happyglitch.delate.audio.input.synth.generator.SynthModuleConstant;

public class SynthCreator {
    private SynthModule.Info info;
    protected SynthCreator(SynthModule.Info i) {
        this.info = i;
    }

    public SynthModule constant(float value) {
        return new SynthModuleConstant(info, value);
    }
    public Oscillator sineWave() {
        return new Oscillator.Sine(info);
    }
    public Oscillator triangleWave() {
        return new Oscillator.Triangle(info);
    }
    public Oscillator sawtoothWave() {
        return new Oscillator.Saw(info);
    }
    public Oscillator squareWave() {
        return new Oscillator.Pulse(info, 0.5f);
    }
    public Oscillator pulseWave() {
        return new Oscillator.Sine(info);
    }
}
