package io.github.happyglitch.delate.audio.output;

import io.github.happyglitch.delate.audio.output.AudioOutput;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.file.Files;

public class FileOutput implements AudioOutput {
    private AudioFormat format;
    private FileOutputStream stream;
    private String tempPath;
    private String destination;

    public FileOutput(AudioFormat format, String tempFilePath, String destinationPath) {
        this.format = format;
        this.tempPath = tempFilePath;
        try {
            stream = new FileOutputStream(tempFilePath);
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        this.destination = destinationPath;
    }

    public FileOutput(AudioFormat format, String filePath) {
        this(format, filePath, filePath);
    }

    @Override
    public void stream(byte[] buffer) {
        try {
            stream.write(buffer);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }

    @Override
    public int getBufferSizeInBytes() {
        return (int)(format.getFrameRate() * format.getFrameSize() / 10);
    }

    @Override
    public boolean isRealtime() {
        return false;
    }

    @Override
    public void close() {
        try {
            stream.close();
            byte[] bytes = Files.readAllBytes(new File(tempPath).toPath());
            AudioInputStream sampleStream = new AudioInputStream(new ByteArrayInputStream(bytes), format, bytes.length / format.getFrameSize());
            AudioSystem.write(sampleStream, AudioFileFormat.Type.WAVE, new File(destination));
            sampleStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
