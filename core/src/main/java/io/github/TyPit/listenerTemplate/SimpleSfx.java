package io.github.TyPit.listenerTemplate;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

final class SimpleSfx {
    private static final float SAMPLE_RATE = 22050f;

    private SimpleSfx() {
    }

    static void playShot(float volume) {
        playTone(880, 45, volume, 0.55);
    }

    static void playExplosion(float volume) {
        playTone(180, 120, volume, 0.8);
    }

    static void playZap(float volume) {
        playTone(1320, 70, volume, 0.45);
    }

    static void playCoreHit(float volume) {
        playTone(240, 140, volume, 0.9);
    }

    static void playPurchase(float volume) {
        playTone(660, 90, volume, 0.35);
    }

    private static void playTone(final int hz, final int millis, final float volume, final double harmonics) {
        final float clampedVolume = Math.max(0f, Math.min(1f, volume));
        if (clampedVolume <= 0f) {
            return;
        }
        Thread thread = new Thread(() -> {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);
                line.start();
                byte[] buffer = new byte[Math.max(1, (int)(SAMPLE_RATE * millis / 1000f))];
                for (int i = 0; i < buffer.length; i++) {
                    double time = i / SAMPLE_RATE;
                    double fundamental = Math.sin(2.0 * Math.PI * hz * time);
                    double overtone = Math.sin(2.0 * Math.PI * hz * 2.0 * time) * harmonics;
                    double sample = (fundamental + overtone * 0.3) * clampedVolume;
                    buffer[i] = (byte)(Math.max(-1.0, Math.min(1.0, sample)) * 127);
                }
                line.write(buffer, 0, buffer.length);
                line.drain();
            } catch (LineUnavailableException ignored) {
            }
        }, "typit-sfx");
        thread.setDaemon(true);
        thread.start();
    }
}
