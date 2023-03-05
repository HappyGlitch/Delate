package io.github.happyglitch.delate.audio.input.synth.generator;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

public class SynthModuleConstant extends SynthModule {
    private float constant;

    public SynthModuleConstant(float value) {
        constant = value;
    }

    @Override
    public SynthModule[] getChildren() {
        return new SynthModule[0];
    }

    @Override
    public float generateNextFrame() {
        return constant;
    }
}
