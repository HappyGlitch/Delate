package io.github.happyglitch.delate.audio.input.synth;

import io.github.happyglitch.delate.audio.input.AudioInput;
import io.github.happyglitch.delate.instrument.InstrumentEvent;
import io.github.happyglitch.delate.instrument.MIDIUtility;

import java.util.HashMap;

public class Instrument implements AudioInput {
    private static class Note {
        private SynthModule module;
        private int offset = 0;
        private int key;
        public Note(SynthModule synth, long frame, int key) {
            module = synth;
            offset = (int)frame;
            this.key = key;
        }
    }
    private SynthFactory factory;
    private HashMap<Integer, Note> notes = new HashMap<>();

    public int polyphony = 8;

    public Instrument(SynthFactory synthesizer) {
        this.factory = synthesizer;
    }

    @Override
    public float[] readFrames(int framesLength, int sampleRate, InstrumentEvent[] events) {
        processEvents(events, sampleRate);
        float[] frames = new float[framesLength];

        //keys are converted to array to avoid ConcurrentModificationException when removing item from the list
        for(Integer key: notes.keySet().toArray(new Integer[0])) {
            Note note = notes.get(key);
            float[] noteFrames = readNote(note, framesLength);
            for(int i = 0; i < framesLength; i++) {
                frames[i] += noteFrames[i] / polyphony;
            }
            if(note.module.isClosed())
                notes.remove(key);
        }
        return frames;
    }

    private void processEvents(InstrumentEvent[] events, int frameRate) {
        for(InstrumentEvent event: events) {
            if(event.getType() == InstrumentEvent.Type.NOTE_ON) {
                if(notes.size() >= polyphony)
                    continue;
                SynthModule.Info info = new SynthModule.Info(frameRate,
                        (float)MIDIUtility.convertNoteToFrequency(event.getValue1()),
                        event.getValue2());
                Note note = new Note(factory.createSynth(info), event.getTime(), event.getValue1());
                note.module.passInfo(info);
                notes.put(event.getValue1(), note);
            } else if(event.getType() == InstrumentEvent.Type.NOTE_OFF) {
                if(!notes.containsKey(event.getValue1()))
                    continue;
                SynthModule.Info info = notes.get(event.getValue1()).module.getInfo();
                info.currentFrameSinceRelease = -(int)event.getTime();
                info.releaseVelocity = event.getValue2();
            }
        }
    }

    private float[] readNote(Note note, int size) {
        float[] noteFrames = note.module.generateBuffer(size - note.offset);
        if(note.offset == 0)
            return noteFrames;
        float[] result = new float[size];
        System.arraycopy(noteFrames, 0, result, note.offset, noteFrames.length);
        note.offset = 0;
        return result;
    }

    @Override
    public boolean isRealtime() {
        return false;
    }
}
