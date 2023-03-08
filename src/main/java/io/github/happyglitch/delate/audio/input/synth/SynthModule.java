package io.github.happyglitch.delate.audio.input.synth;

import io.github.happyglitch.delate.audio.input.synth.generator.Oscillator;
import io.github.happyglitch.delate.audio.input.synth.generator.SynthModuleConstant;

import java.util.ArrayList;

public abstract class SynthModule{
    public static class Info {
        private int frameRate;
        private float frequency;
        private float pressVelocity;
        float releaseVelocity = -1;
        private int currentFrame = 0;
        int currentFrameSinceRelease = Integer.MIN_VALUE;

        private final float[] properties;

        public int getFrameRate() {
            return frameRate;
        }
        public float getFrequency() {
            return frequency;
        }
        public int getCurrentFrame() {
            return currentFrame;
        }
        public float getPressVelocity() {
            return pressVelocity;
        }
        public float getReleaseVelocity() {
            return releaseVelocity;
        }
        public int getCurrentFrameSinceRelease() {
            return currentFrameSinceRelease;
        }
        public float getPropertyValue(int propertyId) {
            return properties[propertyId];
        }
        void setProperty(int propertyId, float value) {
            properties[propertyId] = value;
        }
        private void increaseFrame() {
            if(currentFrameSinceRelease != Integer.MIN_VALUE)
                currentFrameSinceRelease++;
            currentFrame++;
        }

        public Info(int frameRate, float frequency, float pressVelocity, float[] properties) {
            this.frameRate = frameRate;
            this.frequency = frequency;
            this.pressVelocity = pressVelocity;
            this.properties = properties;
            creator = new SynthCreator(this);
        }

        private SynthCreator creator;

        public SynthCreator getCreator() {
            return creator;
        }
    }
    private final Info info;

    public final Info getInfo() {
        return info;
    }

    final float[] generateBuffer(int size) {
        float[] buffer = new float[size];
        if(isClosed())
            return buffer;
        for(int i = 0; i < buffer.length; i++) {
            info.increaseFrame();
            if(isClosed())
                break;
            buffer[i] = generateNextFrame();
        }
        return buffer;
    }

    private int closeThreshold = 1;
    public boolean isClosed() {
        return info.currentFrameSinceRelease > closeThreshold;
    }

    private ArrayList<SynthOutput> outputs = new ArrayList<>();
    public final void registerOutput(SynthOutput output) {
        outputs.add(output);
    }

    private int lastFrame = -1;
    public final float generateNextFrame() {
        if(getInfo().getCurrentFrame() == lastFrame)
            throw new RuntimeException("Delate Synth does not support recursion! Use Feedback class instead.");
        float result = generate();
        notifyOutputs(result);
        return result;
    }

    private void notifyOutputs(float value) {
        for(SynthOutput output: outputs) {
            output.sendOutput(value);
        }
    }

    protected abstract float generate();

    public SynthModule(Info info) {
        this.info = info;
    }
}
