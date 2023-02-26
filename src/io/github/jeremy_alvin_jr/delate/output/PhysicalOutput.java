package io.github.jeremy_alvin_jr.delate.output;

import javax.sound.sampled.*;

public class PhysicalOutput implements AudioOutput {

    private AudioFormat format;
    private final int bufferSize;
    private SourceDataLine stream;
    public PhysicalOutput(AudioFormat format, Device output) {
        this.format = format;
        bufferSize = (int)(format.getFrameRate() * format.getFrameSize() / 10);
        try {
            stream = AudioSystem.getSourceDataLine(format, output.getInfo());
            stream.open();
            stream.start();
        } catch(LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stream(byte[] buffer) {
        stream.write(buffer, 0, buffer.length);
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }

    @Override
    public int getBufferSizeInBytes() {
        return bufferSize;
    }

    public static class Device {
        private Mixer mixer;
        private Mixer.Info info;
        private boolean supportsIn;
        private boolean supportsOut;
        private boolean isDefault;

        private Device(Mixer.Info port) {
            info = port;
            mixer = AudioSystem.getMixer(info);
            supportsIn = mixer.getTargetLineInfo().length > 0;
            supportsOut = mixer.getSourceLineInfo().length > 0;
            isDefault = mixer.equals(AudioSystem.getMixer(null));
        }

        public Mixer.Info getInfo() {
            return info;
        }

        public boolean supportsInput() {
            return supportsIn;
        }

        public boolean supportsOutput() {
            return supportsOut;
        }

        @Override
        public String toString() {
            String result = "";
            if(isDefault)
                result += "DEFAULT | ";
            result += getInfo().toString();
            if(supportsOut && supportsIn)
                result += " | BIDIRECTIONAL";
            else if(supportsOut)
                result += " | OUTPUT";
            else if(supportsIn)
                result += " | INPUT";
            else
                result += " | EMPTY";
            return result;
        }
    }

    public static Device getDefaultDevice() {
        return new Device(AudioSystem.getMixer(null).getMixerInfo());
    }

    public static Device[] getAvailableDevices() {
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        Device[] devices = new Device[info.length];
        for(int i = 0; i < devices.length; i++) {
            devices[i] = new Device(info[i]);
        }
        return devices;
    }
}
