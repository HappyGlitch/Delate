package io.github.happyglitch.delate;

import io.github.happyglitch.delate.audio.input.synth.Instrument;
import io.github.happyglitch.delate.audio.input.synth.SynthFactory;
import io.github.happyglitch.delate.audio.input.synth.SynthModule;
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

    @Override
    public SynthModule createSynth(SynthModule.Info info) {
        Oscillator oscillator = Oscillator.sine();
        oscillator.setFrequency(SynthModule.constant(info.getFrequency()));
        return oscillator;
    }
}
