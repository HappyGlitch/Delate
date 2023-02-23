package io.github.jeremy_alvin_jr.delate.playback;

import io.github.jeremy_alvin_jr.delate.Demo;

import javax.sound.sampled.*;
import java.util.Random;

public class BufferedSoundPlayer {
    private AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
    public void play() {
        SourceDataLine output = null;
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, 44100);
            output = (SourceDataLine) AudioSystem.getLine(info);
            output.open(format, 44100);
            output.start();
        } catch(LineUnavailableException e) {
            e.printStackTrace();
        }
        Random r = new Random();
        for(int i = 0; i < 10; i++) {
            byte[] buffer = new byte[44100];
            r.nextBytes(buffer);
            output.write(buffer, 0, 4410);
            try {
                Thread.sleep(500);
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    }

    public BufferedSoundPlayer() {

    }
}
