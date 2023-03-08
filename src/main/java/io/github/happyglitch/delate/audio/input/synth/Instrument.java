package io.github.happyglitch.delate.audio.input.synth;

import io.github.happyglitch.delate.audio.input.AudioInput;
import io.github.happyglitch.delate.instrument.InstrumentEvent;
import io.github.happyglitch.delate.instrument.MIDIUtility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class Instrument implements AudioInput {

    public Instrument(SynthFactory synthesizer) {
        this(synthesizer, new SynthProperty[0]);
    }

    public Instrument(SynthFactory synthesizer, SynthProperty[] properties) {
        verifyProperties(properties);
        this.properties = new float[properties.length];
        this.factory = synthesizer;
    }

    private void verifyProperties(SynthProperty[] properties) {
        for(int i = 0; i < properties.length; i++) {
            if(!checkIfPropertiesContainId(properties, i)) {
                if(i == properties.length - 1)
                    throw new IllegalArgumentException("Properties passed to the Instrument have overlapping ids.");
                throw new IllegalArgumentException("Properties passed to the instrument do not contain id " + i);
            }
        }
    }
    private boolean checkIfPropertiesContainId(SynthProperty[] properties, int id) {
        for(SynthProperty property: properties) {
            if(property.id == id)
                return true;
        }
        return false;
    }

    private static class Note {
        private SynthModule synth;
        private int noteInMidi;
        private boolean pressed = true;
        private int offsetInFrames = 0;
        public Note(SynthModule synth, int noteInMidi, int offset) {
            this.synth = synth;
            this.noteInMidi = noteInMidi;
            this.offsetInFrames = offset;
        }
    }

    @Override
    public boolean isRealtime() {
        return false;
    }
    private int frameRate = 0;
    private TreeSet<Integer> activeNotes = new TreeSet<>();
    private SynthFactory factory;
    private LinkedList<Note> notes = new LinkedList<>();
    public int polyphony = 16;
    private final float[] properties;
    private boolean damperPressed = false;

    @Override
    public float[] readFrames(int framesLength, int sampleRate, InstrumentEvent[] events) {
        frameRate = sampleRate;
        processEvents(events, sampleRate);
        float[] frames = new float[framesLength];
        sumFramesTo(frames);
        refreshActiveNotes();
        return frames;
    }

    private void refreshActiveNotes() {
        activeNotes.clear();
        for(Note note: notes) {
            activeNotes.add(note.noteInMidi);
        }
    }

    private void sumFramesTo(float[] output) {
        Iterator<Note> iterator = notes.iterator();
        while(iterator.hasNext()) {
            Note note = iterator.next();
            if(!damperPressed && !note.pressed && note.synth.getInfo().currentFrameSinceRelease < 0)
                note.synth.getInfo().currentFrameSinceRelease = 0;
            float[] frames = generateArrayForNote(note, output.length);
            addArrays(frames, output);
            if(note.synth.isClosed())
                iterator.remove();
        }
    }

    private float[] generateArrayForNote(Note note, int length) {
        if(note.offsetInFrames == 0)
            return note.synth.generateBuffer(length);
        int amountToGenerate = length - note.offsetInFrames;
        float[] frames = new float[length];
        System.arraycopy(note.synth.generateBuffer(amountToGenerate), 0, frames, note.offsetInFrames, amountToGenerate);
        note.offsetInFrames = 0;
        return frames;
    }

    private void addArrays(float[] from, float[] to) {
        for(int i = 0; i < from.length; i++) {
            to[i] += from[i] / polyphony;
        }
    }

    private void processEvents(InstrumentEvent[] events, int frameRate) {
        for(InstrumentEvent event: events) {
            switch (event.getType()) {
                case PEDAL -> processPedal(event);
                case NOTE_ON -> processNoteOn(event);
                case NOTE_OFF -> processNoteOff(event);
            }
        }
    }

    private void processPedal(InstrumentEvent e) {
        if(e.getValue1() == InstrumentEvent.DAMPER_PEDAL) {
            damperPressed = e.getValue2() >= 64;
        }
    }
    private void processNoteOn(InstrumentEvent e) {
        int noteInMidi = e.getValue1();
        if(notes.size() >= polyphony)
            notes.removeFirst();
        if(activeNotes.contains(noteInMidi))
            moveNoteToBeginning(noteInMidi);

        SynthModule.Info info = new SynthModule.Info(frameRate,
                (float)MIDIUtility.convertNoteToFrequency(noteInMidi),
                e.getValue2(), properties);
        notes.add(new Note(factory.createSynth(info), noteInMidi, (int)e.getTime()));
    }

    private void moveNoteToBeginning(int noteInMidi) {
        Note noteToMove = null;
        Iterator<Note> iterator = notes.iterator();
        while(iterator.hasNext()) {
            Note note = iterator.next();
            if(note.noteInMidi == noteInMidi) {
                noteToMove = note;
                iterator.remove();
                break;
            }
        }
        if(noteToMove == null)
            return;
        notes.addFirst(noteToMove);
    }
    private void processNoteOff(InstrumentEvent e) {
        int noteInMidi = e.getValue1();
        if(!activeNotes.contains(noteInMidi))
            return;
        for(Note note: notes) {
            if(note.noteInMidi == noteInMidi)
                note.pressed = false;
        }
    }
    private void processPropertyChange(InstrumentEvent e) {
        properties[e.getValue1()] = e.getValue2() / 100.0f;
    }
}
