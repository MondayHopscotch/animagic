package com.bytebreakstudios.animagic.animation;

public class FrameRate {

    private float seconds;
    private boolean total;

    private FrameRate(float seconds, boolean total) {
        if (seconds < 0) seconds = 0;
        this.seconds = seconds;
        this.total = total;
    }

    public static FrameRate total(float seconds) {
        return new FrameRate(seconds, true);
    }

    public static FrameRate perFrame(float seconds) {
        return new FrameRate(seconds, false);
    }

    public float seconds() {
        return seconds;
    }

    public boolean total() {
        return total;
    }

}
