package com.bellman.bible.service.device.speak.event;

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class SpeakEvent {

    private SpeakState speakState;

    public SpeakEvent(SpeakState newSpeakState) {
        this.speakState = newSpeakState;
    }

    public SpeakState getSpeakState() {
        return speakState;
    }

    public boolean isSpeaking() {
        return speakState == SpeakState.SPEAKING;
    }

    public boolean isPaused() {
        return speakState == SpeakState.PAUSED;
    }

    public enum SpeakState {SPEAKING, PAUSED, SILENT}
}
