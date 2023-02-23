package io.github.jeremy_alvin_jr.delate.synth;

import java.util.ArrayList;

public class SynthData {
    private static class Knob {
        String name;
        double setting;
    }
    private static class Value {
        String name;
        int content;

        public Value(String name, int content) {
            this.name = name;
            this.content = content;
        }
    }
    private final int sampleRate;
    public int getSampleRate() {
        return sampleRate;
    }

    private ArrayList<Value> values = new ArrayList<>();
    private ArrayList<Knob> knobs;

    private int getDataLength() {
        return values.size() + knobs.size();
    }

    public String getDataName(int dataId) {
        if(dataId < values.size())
            return values.get(dataId).name;
        return knobs.get(dataId - values.size()).name;
    }

    public double getDataValue(int dataId) {
        if(dataId < values.size())
            return values.get(dataId).content;
        return knobs.get(dataId - values.size()).setting;
    }

    public int getValue(int valueId) {
        return values.get(valueId).content;
    }

    public final static int PRESS_VELOCITY = 0;
    public final static int TIME_AFTER_PRESS = 1;
    public final static int RELEASE_VELOCITY = 2;
    public final static int TIME_AFTER_RELEASE = 3;
    public final static int FUNDAMENTAL = 4;
    private void fillValues() {
        values.add(new Value("Press Velocity", 0));
        values.add(new Value("Time After Press", 0));
        values.add(new Value("Release Velocity", 0));
        values.add(new Value("Time After Release", 0));
        values.add(new Value("Fundamental", 440));
    }

    private SynthData(int sampleRate) {
        this.sampleRate = sampleRate;
        fillValues();
    }

    private void setValue(int valueId, int value) {
        values.get(valueId).content = value;
    }

    private void setKnob(int knobId, double value) {
        knobs.get(knobId).setting = value;
    }

    public static class Owner {
        public SynthData child;
        public Owner(int sampleRate) {
            child = new SynthData(sampleRate);
        }

        public void setData(int dataId, double value) {
            if(dataId < child.values.size())
                child.setValue(dataId, (int)value);
            else
                child.setKnob(dataId - child.values.size(), value);
        }
    }
}
