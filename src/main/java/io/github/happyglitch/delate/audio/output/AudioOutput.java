package io.github.happyglitch.delate.audio.output;

import javax.sound.sampled.AudioFormat;

public interface AudioOutput {
    public void stream(byte[] buffer);
    public AudioFormat getFormat();
    public int getBufferSizeInBytes();
    public int getTimeDelayStatus();
    public void close();
}
