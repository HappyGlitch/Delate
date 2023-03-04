package io.github.happyglitch.delate.instrument;

public final class MIDIUtility {

    private static final int MIDI_A4_NUMBER = 69;
    private static final double A4_FREQUENCY = 440;
    public static double convertNoteToFrequency(int midiNote) {
        return Math.pow(2, ((midiNote - MIDI_A4_NUMBER)/12.0)) * A4_FREQUENCY;
    }

}
