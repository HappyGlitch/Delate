package io.github.happyglitch.delate.audio.input.synth;

import io.github.happyglitch.delate.audio.input.synth.generator.SynthModuleConstant;

public abstract class SynthModule{
    public static class Info {
        private int frameRate;
        private float frequency;
        private float pressVelocity;
        float releaseVelocity = -1;
        private int currentFrame = 0;
        int currentFrameSinceRelease = Integer.MIN_VALUE;

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
        private void increaseFrame() {
            if(currentFrameSinceRelease != Integer.MIN_VALUE)
                currentFrameSinceRelease++;
            currentFrame++;
        }

        public Info(int frameRate, float frequency, float pressVelocity) {
            this.frameRate = frameRate;
            this.frequency = frequency;
            this.pressVelocity = pressVelocity;
        }
    }
    private Info info = new Info(0, 0, 0);

    public final Info getInfo() {
        return info;
    }
    public final void passInfo(Info info) {
        this.info = info;
        for(SynthModule module: getChildren()) {
            module.passInfo(info);
        }
    }

    public final float[] generateBuffer(int size) {
        float[] buffer = new float[size];
        for(int i = 0; i < buffer.length; i++) {
            info.increaseFrame();
            buffer[i] = generateNextFrame();
        }
        return buffer;
    }

    private int closeThreshold = 1;
    public final boolean isClosed() {
        return info.currentFrameSinceRelease > closeThreshold;
    }

    public abstract SynthModule[] getChildren();
    public abstract float generateNextFrame();

    public static SynthModule constant(float value) {
        return new SynthModuleConstant(value);
    }
}
