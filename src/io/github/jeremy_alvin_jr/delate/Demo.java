package io.github.jeremy_alvin_jr.delate;

import io.github.jeremy_alvin_jr.delate.output.PhysicalOutput;

import javax.sound.sampled.AudioFormat;
import java.util.Random;

public class Demo {
    public static void main(String[] args) {
        AudioFormat f = new AudioFormat(44100, 16, 1, true, true);
        PhysicalOutput a = new PhysicalOutput(f, PhysicalOutput.getDefaultDevice());
        Random r = new Random();
        while(true) {
            byte[] b = new byte[a.getBufferSizeInBytes()];
            r.nextBytes(b);
            a.stream(b);
        }
    }
}
