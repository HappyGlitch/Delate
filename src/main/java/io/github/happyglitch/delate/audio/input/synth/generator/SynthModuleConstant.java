package io.github.happyglitch.delate.audio.input.synth.generator;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

public class SynthModuleConstant extends SynthModule {
    private float constant;

    public SynthModuleConstant(Info info, float value) {
        super(info);
        constant = value;
    }

    @Override
    protected float generate() {
        return constant;
    }
}
