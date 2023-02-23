package io.github.jeremy_alvin_jr.delate;

import io.github.jeremy_alvin_jr.delate.playback.AudioStreamer;

import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        AudioStreamer.Device devices[] = AudioStreamer.getAvailableDevices();
        for(AudioStreamer.Device d: devices) {
            System.out.println(d);
            if(!d.isOutputSupported()) {
                System.out.println("pass!\n\n");
                try {
                    Thread.sleep(2000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            try {
                new AudioStreamer(d).play();
            } catch(Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2000);
                } catch (Exception g) {

                }
            }
        }
    }
}
