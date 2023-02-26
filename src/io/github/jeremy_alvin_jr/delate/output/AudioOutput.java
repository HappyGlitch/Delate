package io.github.jeremy_alvin_jr.delate.output;

import javax.sound.sampled.AudioFormat;

public interface AudioOutput {
    public void stream(byte[] buffer);
    public AudioFormat getFormat();

    public int getBufferSizeInBytes();
}
