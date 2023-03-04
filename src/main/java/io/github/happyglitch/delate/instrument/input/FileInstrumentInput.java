package io.github.happyglitch.delate.instrument.input;

import io.github.happyglitch.delate.instrument.InstrumentEvent;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;

public class FileInstrumentInput implements InstrumentInput {

    /**
     * resolution in milliseconds per tick
     */
    private double resolution = 0;
    private long time = 0;
    private InstrumentEvent.Queue queue = new InstrumentEvent.Queue();

    public FileInstrumentInput(String pathToMidiFile) {
        try {
            Sequence sequence = MidiSystem.getSequence(new File(pathToMidiFile));
            loadSequence(sequence);
        } catch(Exception e) {
            throw new IllegalArgumentException("Path " + pathToMidiFile + "is not a valid midi file.");
        }
    }

    private void loadSequence(Sequence sequence) {
        resolution = sequence.getMicrosecondLength() / 1000.0 / sequence.getTickLength();
        Track track = sequence.getTracks()[0];
        for(int i = 0; i < track.size(); i++) {
            queue.add(InstrumentEvent.fromMidiEvent(track.get(i)));
        }
    }

    @Override
    public InstrumentEvent[] readInput(int lengthInFrames, int frameRate) {
        time += lengthInFrames;
        long timeInTicks = (long)(time / (resolution / 1000) / frameRate);
        InstrumentEvent[] events = queue.pollEventsUntil(timeInTicks);
        events = InstrumentEvent.Queue.convertEventsToFrameTime(events, resolution, frameRate, lengthInFrames-time);
        if(queue.isEmpty())
            System.out.println("Playback ended...");
        return events;
    }

    @Override
    public boolean isRealtime() {
        return false;
    }
}
