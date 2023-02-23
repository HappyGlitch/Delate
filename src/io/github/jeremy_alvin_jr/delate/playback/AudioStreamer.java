package io.github.jeremy_alvin_jr.delate.playback;

import javax.sound.sampled.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class AudioStreamer {
    public static class Device {
        private Mixer.Info info;
        private boolean inputSupported;
        private boolean outputSupported;
        private boolean isDefault;

        public String getName() {
            return info.getName();
        }
        public String getDescription() {
            return info.getDescription();
        }
        public String getVendor() {
            return info.getVendor();
        }
        public String getVersion() {
            return info.getVersion();
        }

        private String getAudioTypeText() {
            if(isInputSupported()) {
                if(isOutputSupported())
                    return "Input-Output";
                return "Input";
            }
            if(isOutputSupported())
                return "Output";
            return "Empty";
        }

        @Override
        public String toString() {
            if(isDefault())
                return "DEFAULT | " + getAudioTypeText() + " Audio device " + getName()
                        + " | " + getDescription() + "\n";
            return getAudioTypeText() + " Audio device " + getName()
                    + " | " + getDescription() + "\n";
        }

        public boolean isInputSupported() {
            return inputSupported;
        }

        public boolean isOutputSupported() {
            return outputSupported;
        }

        public boolean isDefault() {
            return isDefault;
        }

        private Device(Mixer.Info info) {
            this.info = info;
            Mixer mixer = AudioSystem.getMixer(info);
            this.inputSupported = mixer.getTargetLineInfo().length > 0;
            this.outputSupported = mixer.getSourceLineInfo().length > 0;
            this.isDefault = AudioSystem.getMixer(null).equals(mixer);
        }
    }

    public static Device[] getAvailableDevices() {
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        Device[] devices = new Device[info.length];
        for(int i = 0; i < info.length; i++) {
            devices[i] = new Device(info[i]);
        }

        return devices;
    }

    private AudioFormat format;
    private Device device;
    private SourceDataLine output;

    private Device getDefaultDevice() {
        return new Device(AudioSystem.getMixer(null).getMixerInfo());
    }

    public AudioStreamer(Device device, int sampleRate, int sampleSize, int channels) {
        this.device = device;
        this.format = new AudioFormat(sampleRate, sampleSize, channels, true, true);
        if(device == null)
            this.device = getDefaultDevice();
        if(!device.isOutputSupported())
            throw new IllegalArgumentException(device + " doesn't support output streaming");

        try {
            openOutput();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private int bufferSize;
    private void openOutput() throws LineUnavailableException {
        bufferSize = (int)(format.getSampleRate()/10);
        output = AudioSystem.getSourceDataLine(format, device.info);
        output.open(format, bufferSize);
        output.start();
    }

    public AudioStreamer(Device device, int sampleRate, int channels) {
        this(device, sampleRate, 16, channels);
    }
    public AudioStreamer(Device device) {
        this(device, 44100, 1);
    }

    public void play() {
        Random r = new Random();
        byte[] buffer = new byte[88100];
        r.nextBytes(buffer);
        output.write(buffer, 0, 88100);
        try {
            Thread.sleep(2000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
