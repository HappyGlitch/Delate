package io.github.happyglitch.delate.audio.output;

import io.github.happyglitch.delate.audio.output.AudioOutput;

import javax.sound.sampled.*;
import java.util.HashSet;

public class PhysicalOutput implements AudioOutput {

    private AudioFormat format;
    private final int bufferSize;
    private SourceDataLine stream;
    public PhysicalOutput(AudioFormat format, Device output, int bufferSize) {
        this.format = format;
        this.bufferSize = bufferSize;
        try {
            stream = AudioSystem.getSourceDataLine(format, output.getInfo());
            stream.open(format, bufferSize);
            stream.start();
        } catch(LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public PhysicalOutput(AudioFormat format, Device output) {
        this(format, output,
                (int)(format.getFrameRate() * format.getFrameSize() / 10));
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

    @Override
    public int getTimeDelayStatus() {
        return stream.available();
    }

    @Override
    public void close() {
        stream.close();
    }

    public static class Device {
        private Mixer mixer;
        private Mixer.Info info;
        private boolean supportsIn;
        private boolean supportsOut;
        private boolean isDefault;
        private Formatter formatter;

        private Device(Mixer.Info port) {
            info = port;
            mixer = AudioSystem.getMixer(info);
            supportsIn = mixer.getTargetLineInfo().length > 0;
            supportsOut = mixer.getSourceLineInfo().length > 0;
            isDefault = mixer.equals(AudioSystem.getMixer(null));
            formatter = new Formatter(this);
        }

        public Mixer.Info getInfo() {
            return info;
        }

        public Formatter getFormatter() {
            return formatter;
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

    public static class Formatter {
        private Device device;
        private HashSet<Integer> channels = new HashSet<>();
        private HashSet<Integer> bitDepth = new HashSet<>();
        private HashSet<Integer> sampleRate = new HashSet<>();
        private boolean floatSupported = false;
        private Formatter(Device d) {
            this.device = d;
            scan();
        }

        private void scan() {
            Line.Info[] info = device.mixer.getSourceLineInfo();
            for(int i = 0; i < info.length; i++) {
                if(!(info[i] instanceof DataLine.Info))
                    continue;
                DataLine.Info data = (DataLine.Info)info[i];
                for(AudioFormat f: data.getFormats()) {
                    if(f.getEncoding() == AudioFormat.Encoding.PCM_FLOAT)
                        floatSupported = true;
                    channels.add(f.getChannels());
                    bitDepth.add(f.getSampleSizeInBits());
                    sampleRate.add((int)f.getSampleRate());
                }
            }
        }

        public int[] supportedChannelNumber() {
            return channels.stream().mapToInt(i -> i).toArray();
        }

        public int[] supportedBitDepth() {
            return bitDepth.stream().mapToInt(i -> i).toArray();
        }

        public int[] supportedSampleRate() {
            return sampleRate.stream().mapToInt(i -> i).toArray();
        }

        public boolean isFloatSupported() {
            return floatSupported;
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
