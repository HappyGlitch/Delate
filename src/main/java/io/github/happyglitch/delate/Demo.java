package io.github.happyglitch.delate;

import io.github.happyglitch.delate.audio.output.AudioOutput;
import io.github.happyglitch.delate.audio.output.PhysicalOutput;
import io.github.happyglitch.delate.audio.output.FileOutput;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public class Demo {
    public static void main(String[] args) {
        AudioFormat f = new AudioFormat(44100, 16, 2, true, true);
        AudioOutput o1 = new PhysicalOutput(f, PhysicalOutput.getDefaultDevice());
        AudioOutput o2 = new FileOutput(f, "test.wav");
        for(int i = 0; i < 30; i++) {
            ByteBuffer buff = ByteBuffer.allocate(o1.getBufferSizeInBytes());
            for(int j = 0; j < o1.getBufferSizeInBytes() / 4; j++) {
                buff.putShort((short)(Math.sin((i * o1.getBufferSizeInBytes() / 4.0 + j) * Math.PI /220) * Short.MAX_VALUE));
                buff.putShort((short)(Math.sin((i * o1.getBufferSizeInBytes() / 4.0 + j) * Math.PI /220) * Short.MAX_VALUE));
            }
            byte[] b = buff.array();
            o1.stream(b);
            o2.stream(b);
        }
        o1.close();
        o2.close();
    }
}
