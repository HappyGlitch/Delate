package io.github.happyglitch.delate.audio.input.synth.generator;

import io.github.happyglitch.delate.audio.input.synth.SynthModule;

import java.util.Random;

public class Noise extends SynthModule {
    public enum Type {
        WHITE,
        UNKNOWN
    }
    private Type type;
    Random r = new Random();

    public Noise(Info info, Type type) {
        super(info);
        this.type = type;
    }

    @Override
    protected float generate() {
        return switch (type) {
            case WHITE -> r.nextFloat() * 2 - 1;
            default -> throw new RuntimeException("Invalid noise type!");
        };
    }
}
