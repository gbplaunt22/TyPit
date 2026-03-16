package io.github.TyPit.listenerTemplate;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.tommyettinger.textra.TextraLabel;
import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Drop extends Game {
    private static final float MUSIC_FADE_DURATION = 1.2f;
    public static final String MUSIC_TITLE = "music/music_takanaka_main_theme.ogg";
    public static final String MUSIC_GAMEPLAY = "music/music_takanaka_main_theme.ogg";
    public static final String MUSIC_BOSS = "music/music_takanaka_boss_theme.ogg";
    public static final String MUSIC_SHOP = "music/music_takanaka_shop_theme.ogg";

    public SpriteBatch batch;
    public FitViewport viewport;
    public boolean debugMenuUnlocked;
    public float musicVolume;
    public float soundVolume;
    private Music currentMusic;
    private String currentMusicPath;
    private Music fadingMusic;
    private String fadingMusicPath;
    private float fadeTimer;
    private final Map<String, Float> musicPositions = new HashMap<String, Float>();


    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);
        debugMenuUnlocked = false;
        musicVolume = 0.05f;
        soundVolume = 0.05f;

        setScreen(new FirstScreen(this));
    }

    public void render() {
        updateMusicTransition(Gdx.graphics.getDeltaTime());
        super.render();
    }

    public void dispose() {
        if (screen != null) screen.dispose();
        disposeMusic(currentMusic, currentMusicPath);
        disposeMusic(fadingMusic, fadingMusicPath);
        currentMusic = null;
        currentMusicPath = null;
        fadingMusic = null;
        fadingMusicPath = null;
        batch.dispose();
    }

    public void playMusic(String assetPath) {
        if (assetPath == null || assetPath.isEmpty()) {
            stopMusic();
            return;
        }
        if (currentMusic != null && assetPath.equals(currentMusicPath)) {
            applyMusicVolumes();
            if (!currentMusic.isPlaying()) {
                currentMusic.play();
            }
            return;
        }
        if (fadingMusic != null) {
            disposeMusic(fadingMusic, fadingMusicPath);
            fadingMusic = null;
            fadingMusicPath = null;
        }
        fadingMusic = currentMusic;
        fadingMusicPath = currentMusicPath;

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(assetPath));
        currentMusic.setLooping(true);
        currentMusic.setVolume(fadingMusic != null ? 0f : musicVolume);
        currentMusic.play();
        restoreMusicPosition(currentMusic, assetPath);
        currentMusicPath = assetPath;
        fadeTimer = fadingMusic != null ? MUSIC_FADE_DURATION : 0f;
        applyMusicVolumes();
    }

    public void stopMusic() {
        disposeMusic(currentMusic, currentMusicPath);
        disposeMusic(fadingMusic, fadingMusicPath);
        currentMusic = null;
        currentMusicPath = null;
        fadingMusic = null;
        fadingMusicPath = null;
        fadeTimer = 0f;
    }

    public void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        applyMusicVolumes();
    }

    public void setSoundVolume(float volume) {
        soundVolume = Math.max(0f, Math.min(1f, volume));
    }

    private void updateMusicTransition(float delta) {
        cacheMusicPosition(currentMusic, currentMusicPath);
        cacheMusicPosition(fadingMusic, fadingMusicPath);

        if (fadingMusic == null) {
            applyMusicVolumes();
            return;
        }

        fadeTimer = Math.max(0f, fadeTimer - delta);
        applyMusicVolumes();
        if (fadeTimer == 0f) {
            disposeMusic(fadingMusic, fadingMusicPath);
            fadingMusic = null;
            fadingMusicPath = null;
            applyMusicVolumes();
        }
    }

    private void applyMusicVolumes() {
        float fadeProgress = fadingMusic == null || MUSIC_FADE_DURATION <= 0f
            ? 1f
            : 1f - (fadeTimer / MUSIC_FADE_DURATION);
        fadeProgress = Math.max(0f, Math.min(1f, fadeProgress));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume * fadeProgress);
        }
        if (fadingMusic != null) {
            fadingMusic.setVolume(musicVolume * (1f - fadeProgress));
        }
    }

    private void cacheMusicPosition(Music music, String path) {
        if (music == null || path == null) {
            return;
        }
        musicPositions.put(path, music.getPosition());
    }

    private void restoreMusicPosition(Music music, String path) {
        if (music == null || path == null) {
            return;
        }
        Float savedPosition = musicPositions.get(path);
        if (savedPosition == null || savedPosition <= 0f) {
            return;
        }
        try {
            music.setPosition(savedPosition);
        } catch (RuntimeException ignored) {
            musicPositions.remove(path);
        }
    }

    private void disposeMusic(Music music, String path) {
        if (music == null) {
            return;
        }
        cacheMusicPosition(music, path);
        music.stop();
        music.dispose();
    }
}
