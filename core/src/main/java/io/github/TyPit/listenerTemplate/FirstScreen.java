package io.github.TyPit.listenerTemplate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.KnownFonts;
import com.github.tommyettinger.textra.TextraLabel;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.tommyettinger.textra.TypingLabel;


/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    final Drop game;
    private Stage stage;
    private TypingLabel typingLabel;
    private TypingLabel debugLabel;
    private boolean readyForStart;

    public FirstScreen(final Drop game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.playMusic(Drop.MUSIC_TITLE);
        stage = new Stage(new FitViewport(1920, 1080), game.batch);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        typingLabel = new TypingLabel("{GRADIENT=ffffffff;424238ff;1.0;1.0}{SHRINK=10.0;20.0;true}[%100][@Gentium]{SHAKE=50.0;1.0;5.0}{JOLT=1.5;1.0;inf;0.01;ffffffff;ffff88ff}{WAVE=5.3;0.4;0.1} Press anywhere to begin... {ENDWAVE}{ENDJOLT}{ENDSHAKE}[@][%]{ENDSHRINK}{ENDGRADIENT}",
            KnownFonts.getStandardFamily());

        table.add(typingLabel).center();

        Table debugTable = new Table();
        debugTable.setFillParent(true);
        debugTable.bottom().right().pad(24f);
        debugLabel = new TypingLabel("", KnownFonts.getStandardFamily());
        debugTable.add(debugLabel);
        stage.addActor(debugTable);
        updateDebugLabel();
        readyForStart = false;

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            game.debugMenuUnlocked = !game.debugMenuUnlocked;
            updateDebugLabel();
        }

        if (!Gdx.input.isTouched()) {
            readyForStart = true;
        }

        stage.act(delta);
        stage.draw();

        if ((readyForStart && Gdx.input.justTouched()) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new SecondScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
    }

    private void updateDebugLabel() {
        if (debugLabel == null) return;
        debugLabel.setText(game.debugMenuUnlocked
            ? "[#C7FFD1]Debug Menu Enabled [#AAB0BF](F1)"
            : "[#6E7482]Press F1 to enable debug menu");
    }
}
