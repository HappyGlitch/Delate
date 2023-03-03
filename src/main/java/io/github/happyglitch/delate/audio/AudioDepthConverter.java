package io.github.happyglitch.delate.audio;

import java.nio.ByteBuffer;

public final class AudioDepthConverter {
    public static float convert8ToFloat(byte b) {
        return (float)b / Byte.MAX_VALUE;
    }
    public static float convert16ToFloat(short b) {
        return (float)b / Short.MAX_VALUE;
    }
    private static final int MAX_24_BIT_VALUE = 8388608;
    public static float convert24ToFloat(int b) {
        return (float)b / MAX_24_BIT_VALUE;
    }
    public static float convert32ToFloat(int b) {
        return (float)b / Integer.MAX_VALUE;
    }

    public static byte convertFloatTo8(float f) {
        return (byte)(f * Byte.MAX_VALUE);
    }
    public static short convertFloatTo16(float f) {
        return (short)(f * Short.MAX_VALUE);
    }
    public static int convertFloatTo24(float f) {
        return (int)(f * MAX_24_BIT_VALUE);
    }
    public static int convertFloatTo32(float f) {
        return (int)(f * Integer.MAX_VALUE);
    }

    public static byte[] convertFloatTo8(float[] f) {
        ByteBuffer buffer = ByteBuffer.allocate(f.length);
        for(int i = 0; i < f.length; i++) {
            buffer.put(convertFloatTo8(f[i]));
        }
        return buffer.array();
    }
    public static byte[] convertFloatTo16(float[] f) {
        ByteBuffer buffer = ByteBuffer.allocate(f.length * 2);
        for(int i = 0; i < f.length; i++) {
            buffer.putShort(convertFloatTo16(f[i]));
        }
        return buffer.array();
    }
    public static byte[] convertFloatTo24(float[] f) {
        ByteBuffer buffer = ByteBuffer.allocate(f.length * 3);
        for(int i = 0; i < f.length; i++) {
            int value = convertFloatTo24(f[i]);
            buffer.putShort((short)(value >> 8));
            buffer.put((byte)value);
        }
        return buffer.array();
    }
    public static byte[] convertFloatTo32(float[] f) {
        ByteBuffer buffer = ByteBuffer.allocate(f.length * 4);
        for(int i = 0; i < f.length; i++) {
            buffer.putInt(convertFloatTo32(f[i]));
        }
        return buffer.array();
    }
    public static byte[] convertFloatTo(float[] f, int resultingBitDepth) {
        switch(resultingBitDepth) {
            case 8:
                return convertFloatTo8(f);
            case 16:
                return convertFloatTo16(f);
            case 24:
                return convertFloatTo24(f);
            case 32:
                return convertFloatTo32(f);
            default:
                throw new IllegalArgumentException("Invalid bit depth " + resultingBitDepth);
        }
    }
}
