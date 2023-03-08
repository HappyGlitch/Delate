package io.github.happyglitch.delate.audio.input.synth.generator;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

public abstract class Oscillator extends SynthModule {
    private SynthModule frequency;
    private SynthModule phase;
    private SynthModule amplitude;
    private SynthModule base;


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
    public final float generate() {
        float frequencyInFrames = frequency.generateNextFrame() / getInfo().getFrameRate();
        time += frequencyInFrames;
        if(time >= 1)
            time %= 1;
        return base.generateNextFrame()
                + oscillate((time + phase.generateNextFrame()) % 1)
                * amplitude.generateNextFrame();
    }

    protected abstract float oscillate(float phase);

    public Oscillator(Info info) {
        super(info);
        frequency = info.getCreator().constant(440);
        amplitude = info.getCreator().constant(1);
        phase = info.getCreator().constant(0);
        base = info.getCreator().constant(0);
    }

    public static class Sine extends Oscillator {
        public Sine(Info info) {
            super(info);
        }

        @Override
        protected float oscillate(float phase) {
            return (float)Math.sin(Math.PI * 2 * phase);
        }
    }
    public static class Pulse extends Oscillator {
        public float width;

        public Pulse(Info info, float width) {
            super(info);
            this.width = width;
        }

        @Override
        protected float oscillate(float phase) {
            if(phase < width)
                return 1;
            return -1;
        }
    }
    public static class Saw extends Oscillator {
        public Saw(Info info) {
            super(info);
        }

        @Override
        protected float oscillate(float phase) {
            if(phase < 0.5)
                return phase + 0.5f;
            return phase - 0.5f;
        }
    }
    public static class Triangle extends Oscillator {
        public Triangle(Info info) {
            super(info);
        }

        @Override
        protected float oscillate(float phase) {
            if(phase < 0.25)
                return 4 * phase;
            if(phase < 0.75)
                return 2 - 4 * phase;
            return 4 * phase - 4;
        }
    }
}
