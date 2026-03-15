package io.github.TyPit.listenerTemplate;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.tommyettinger.textra.TextraLabel;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Drop extends Game {

    public SpriteBatch batch;
    public FitViewport viewport;
    public boolean debugMenuUnlocked;


    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);
        debugMenuUnlocked = false;

        setScreen(new FirstScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        if (screen != null) screen.dispose();
        batch.dispose();
    }
}
