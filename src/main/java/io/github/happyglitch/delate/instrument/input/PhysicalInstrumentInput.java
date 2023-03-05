package io.github.happyglitch.delate.instrument.input;

import io.github.happyglitch.delate.instrument.InstrumentEvent;

import javax.sound.midi.*;

public class PhysicalInstrumentInput implements InstrumentInput, Receiver {

    private InstrumentEvent.Queue queue = new InstrumentEvent.Queue();
    private MidiDevice device;
    private long lastTime;

    public PhysicalInstrumentInput() {
        try {
            device = ((MidiDeviceTransmitter)MidiSystem.getTransmitter()).getMidiDevice();
            device.open();
            device.getTransmitter().setReceiver(this);
            lastTime = device.getMicrosecondPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public synchronized InstrumentEvent[] readInput(int lengthInFrames, int frameRate) {
        InstrumentEvent[] events = queue.pollEventsUntil(Long.MAX_VALUE);
        InstrumentEvent.Queue.convertEventsToFrameTime(events, 0.001 / 1000, frameRate, 0);
        lastTime = device.getMicrosecondPosition();
        return events;
    }

    @Override
    public boolean isRealtime() {
        return true;
    }

    @Override
    public synchronized void send(MidiMessage message, long timeStamp) {
        if(message instanceof SysexMessage)
            System.out.println("sSYSEX");
        if(message instanceof MetaMessage)
            System.out.println("mMETA");
        queue.add(InstrumentEvent.fromMidiEvent(
                new MidiEvent(message, timeStamp - lastTime)));
    }

    @Override
    public void close() {
        device.close();
    }
}
