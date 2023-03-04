package io.github.happyglitch.delate;

import io.github.happyglitch.delate.audio.input.generator.TestSynth;
import io.github.happyglitch.delate.audio.output.AudioOutput;
import io.github.happyglitch.delate.audio.output.PhysicalOutput;
import io.github.happyglitch.delate.audio.output.FileOutput;
import io.github.happyglitch.delate.instrument.input.FileInstrumentInput;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public class Demo {
    public static void main(String[] args) {
        AudioFormat f = new AudioFormat(44100, 16, 2, true, true);
        DelateConnector connector = new DelateConnector(new TestSynth(), new FileInstrumentInput("test.mid"));
        connector.registerAudio(new PhysicalOutput(f, PhysicalOutput.getDefaultDevice())).start();
    }
}
