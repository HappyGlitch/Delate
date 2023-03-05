package io.github.happyglitch.delate.audio.input.synth.generator;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

public abstract class Oscillator extends SynthModule {
    private SynthModule frequency = SynthModule.constant(440);
    private SynthModule phase = SynthModule.constant(0);
    private SynthModule amplitude = SynthModule.constant(1);;
    private SynthModule base = SynthModule.constant(0);

    @Override
    public final SynthModule[] getChildren() {
        return new SynthModule[] {frequency, phase, amplitude, base};
    }

    public void setFrequency(SynthModule frequency) {
        this.frequency = frequency;
    }
    public void setPhase(SynthModule phase) {
        this.phase = phase;
    }
    public void setAmplitude(SynthModule amplitude) {
        this.amplitude = amplitude;
    }
    public void setBase(SynthModule base) {
        this.base = base;
    }

    private float time;

    @Override
    public final float generateNextFrame() {
        float frequencyInFrames = frequency.generateNextFrame() / getInfo().getFrameRate();
        time += frequencyInFrames;
        if(time >= 1)
            time %= 1;
        return base.generateNextFrame()
                + oscillate((time + phase.generateNextFrame()) % 1)
                * amplitude.generateNextFrame();
    }

    protected abstract float oscillate(float phase);

    private static class Sine extends Oscillator {
        @Override
        protected float oscillate(float phase) {
            return (float)Math.sin(Math.PI * 2 * phase);
        }
    }
    private static class Pulse extends Oscillator {
        public float width;
        @Override
        protected float oscillate(float phase) {
            if(phase < width)
                return 1;
            return -1;
        }
    }
    private static class Saw extends Oscillator {
        @Override
        protected float oscillate(float phase) {
            if(phase < 0.5)
                return phase + 0.5f;
            return phase - 0.5f;
        }
    }
    private static class Triangle extends Oscillator {
        @Override
        protected float oscillate(float phase) {
            if(phase < 0.25)
                return 4 * phase;
            if(phase < 0.75)
                return 2 - 4 * phase;
            return 4 * phase - 4;
        }
    }

    public static Oscillator sine() {
        return new Sine();
    }
    public static Oscillator triangle() {
        return new Triangle();
    }
    public static Oscillator sawtooth() {
        return new Saw();
    }
    public static Oscillator pulse(float width) {
        Pulse p = new Pulse();
        p.width = width;
        return p;
    }
    public static Oscillator square() {
        return pulse(0.5f);
    }
}
