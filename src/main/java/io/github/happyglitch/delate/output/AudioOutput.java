package io.github.happyglitch.delate.output;

import javax.sound.sampled.AudioFormat;

public interface AudioOutput {
    public void stream(byte[] buffer);
    public AudioFormat getFormat();
    public int getBufferSizeInBytes();
    public boolean isRealtime();
    public void close();
}
