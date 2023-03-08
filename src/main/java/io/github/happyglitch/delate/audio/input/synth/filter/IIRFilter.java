package io.github.happyglitch.delate.audio.input.synth.filter;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

public class IIRFilter extends Filter {
    private float lastAmplitude = 0;

    public IIRFilter(Info info) {
        super(info);
    }

    @Override
    public float filterSignal(float signal, float resonance, float cutoff) {
        /*float smoothFactor = (float)(
                getInfo().getFrameRate() / (getInfo().getFrameRate() + 1 / (2 * Math.PI * cutoff))
        );
        lastAmplitude = smoothFactor * signal + (1 - smoothFactor) * lastAmplitude;
        return lastAmplitude;*/
        float result = (signal + lastAmplitude) * 0.5f;
        lastAmplitude = signal;
        return result;
    }
}
