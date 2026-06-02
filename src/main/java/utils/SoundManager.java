package utils;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.Toolkit;
import java.net.URL;

/**
 * Sound manager for dispatch alerts and the ambulance siren.
 * The siren WAV loops only while the emergency ambulance is moving.
 */
public class SoundManager {
    private boolean muted = false;
    private Clip sirenClip;

    public SoundManager() {
        loadSirenClip();
    }

    private void loadSirenClip() {
        try {
            URL sirenUrl = getClass().getResource("/sounds/ambulance_siren.wav");
            if (sirenUrl == null) return;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sirenUrl);
            sirenClip = AudioSystem.getClip();
            sirenClip.open(audioIn);
        } catch (Exception ex) {
            sirenClip = null; // fallback to system beeps if audio cannot be loaded
        }
    }

    public void toggleMute() {
        muted = !muted;
        if (muted) stopSirenLoop();
    }

    public boolean isMuted() { return muted; }

    public void dispatchAlert() { beep(1); }
    public void routeFound() { beep(2); }

    /** Backward-compatible short siren pulse. */
    public void siren() { beep(4); }

    /** Starts the real siren loop while the ambulance is moving. */
    public void startSirenLoop() {
        if (muted) return;
        if (sirenClip != null) {
            sirenClip.stop();
            sirenClip.setFramePosition(0);
            sirenClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            beep(4);
        }
    }

    /** Stops the moving-ambulance siren immediately. */
    public void stopSirenLoop() {
        if (sirenClip != null && sirenClip.isRunning()) {
            sirenClip.stop();
            sirenClip.setFramePosition(0);
        }
    }

    private void beep(int times) {
        if (muted) return;
        for (int i = 0; i < times; i++) {
            PauseTransition p = new PauseTransition(Duration.millis(i * 180));
            p.setOnFinished(e -> Toolkit.getDefaultToolkit().beep());
            p.play();
        }
    }
}
