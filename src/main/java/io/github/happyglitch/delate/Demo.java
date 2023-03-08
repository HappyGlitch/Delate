package io.github.happyglitch.delate;

import io.github.happyglitch.delate.audio.input.synth.Instrument;
import io.github.happyglitch.delate.audio.input.synth.SynthFactory;
import io.github.happyglitch.delate.audio.input.synth.SynthModule;
import io.github.happyglitch.delate.audio.input.synth.filter.Delay;
import io.github.happyglitch.delate.audio.input.synth.filter.Filter;
import io.github.happyglitch.delate.audio.input.synth.filter.IIRFilter;
import io.github.happyglitch.delate.audio.input.synth.generator.Noise;
import io.github.happyglitch.delate.audio.input.synth.generator.Oscillator;
import io.github.happyglitch.delate.audio.output.PhysicalOutput;
import io.github.happyglitch.delate.instrument.input.FileInstrumentInput;
import io.github.happyglitch.delate.instrument.input.PhysicalInstrumentInput;

import javax.sound.sampled.AudioFormat;

public class Demo implements SynthFactory {
    public static void main(String[] args) {
        SynthFactory synth = new Demo();
        AudioFormat f = new AudioFormat(44100, 8, 1, true, true);

        DelateConnector connector = new DelateConnector(
                new Instrument(synth),
                new PhysicalInstrumentInput()
        );
        connector.registerAudio(new PhysicalOutput(f, PhysicalOutput.getDefaultDevice())).start();
    }

    private SynthModule.Info inf;

    @Override
    public SynthModule createSynth(SynthModule.Info info) {
        inf = info;

        Oscillator mod = info.getCreator().sineWave();
        mod.setBase(info.getCreator().constant(info.getFrequency()));
        mod.setFrequency(info.getCreator().constant(info.getFrequency() /2));
        mod.setAmplitude(info.getCreator().constant(20));

        Oscillator phase = info.getCreator().squareWave();
        phase.setFrequency(info.getCreator().constant(50f));
        phase.setAmplitude(info.getCreator().constant(0.00f));


        Oscillator o = info.getCreator().sineWave();
        o.setAmplitude(info.getCreator().constant(info.getPressVelocity()/40 + 0.2f));
        o.setFrequency(mod);
        o.setPhase(phase);
        return o;
    }
}
