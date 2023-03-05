package io.github.happyglitch.delate.audio.input.synth;

public interface SynthFactory {
    public SynthModule createSynth(SynthModule.Info info);
}
