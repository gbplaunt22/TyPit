package io.github.TyPit.listenerTemplate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.KnownFonts;
import com.github.tommyettinger.textra.Layout;
import java.util.Random;

public class SecondScreen implements Screen {
    private static final int VISIBLE_LINES = 4;
    private static final float BOX_WIDTH = 1180f;
    private static final float BOX_HEIGHT = 260f;
    private static final float BOX_Y = 140f;
    private static final float PADDING = 28f;
    private static final float ERROR_FLASH_DURATION = 0.18f;
    private static final float CORE_RADIUS = 58f;
    private static final float CORE_RING_RADIUS = 84f;
    private static final float CORE_Y = 610f;
    private static final float ENEMY_RADIUS = 18f;
    private static final float BASE_SPAWN_INTERVAL = 1.35f;
    private static final float MIN_SPAWN_INTERVAL = 0.05f;
    private static final float SPAWN_INTERVAL_DECAY = 0.82f;
    private static final float ENEMY_MIN_SPEED = 62f;
    private static final float ENEMY_MAX_SPEED = 104f;
    private static final int BASE_ENEMY_HEALTH = 1;
    private static final int BASE_ENEMY_DAMAGE = 1;
    private static final int TOTAL_ROUNDS = 24;
    private static final int NORMAL_ROUNDS_PER_BOSS = 3;
    private static final int BASE_NORMAL_ROUND_SPAWN_EVENTS = 16;
    private static final float NORMAL_ROUND_SPAWN_GROWTH = 1.45f;
    private static final float ROUND_START_DELAY = 0.85f;
    private static final float BASE_BOSS_SPEED = 28f;
    private static final int BASE_BOSS_HEALTH = 8;
    private static final int BASE_BOSS_DAMAGE = 10;
    private static final float BULLET_RADIUS = 6f;
    private static final float BULLET_SPEED = 520f;
    private static final float DAMAGE_NUMBER_LIFETIME = 0.45f;
    private static final int BASE_CORE_MAX_HEALTH = 3;
    private static final float RESTART_BUTTON_WIDTH = 260f;
    private static final float RESTART_BUTTON_HEIGHT = 64f;
    private static final float SETTINGS_PANEL_WIDTH = 440f;
    private static final float SETTINGS_BUTTON_WIDTH = 390f;
    private static final float SETTINGS_BUTTON_HEIGHT = 42f;
    private static final float SETTINGS_BUTTON_GAP = 14f;
    private static final float SHOP_PANEL_WIDTH = 920f;
    private static final float SHOP_PANEL_HEIGHT = 420f;
    private static final float SHOP_CARD_WIDTH = 250f;
    private static final float SHOP_CARD_HEIGHT = 220f;
    private static final int SHOP_OFFER_COUNT = 3;
    private static final float DEBUG_PANEL_WIDTH = 280f;
    private static final float DEBUG_PANEL_ROW_HEIGHT = 34f;
    private static final float DEBUG_PANEL_PADDING = 12f;
    private static final int DEFAULT_AUTO_WPM = 60;

    private final Drop game;
    private final Array<String> visibleLines = new Array<>(VISIBLE_LINES);
    private final Array<Bullet> bullets = new Array<>();
    private final Array<DamageNumber> damageNumbers = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<ShopOffer> shopOffers = new Array<>();
    private final InfiniteEssay essay = new InfiniteEssay();
    private final Layout layout = new Layout();
    private final Layout measureLayout = new Layout();
    private final Vector3 tempTouch = new Vector3();
    private final TypingInput typingInput = new TypingInput();

    private ShapeRenderer shapes;
    private Font font;
    private float boxX;
    private float coreX;
    private float lineHeight;
    private float errorFlash;
    private float spawnTimer;
    private float roundMessageTimer;
    private float currentSpawnInterval;
    private float restartButtonX;
    private float restartButtonY;
    private float shopPanelX;
    private float shopPanelY;
    private float settingsPanelX;
    private float settingsPanelY;
    private float textWrapWidth;
    private float debugPanelX;
    private float debugPanelY;
    private int currentLineIndex;
    private int currentCharIndex;
    private int completedWords;
    private int coreHealth;
    private int coreMaxHealth;
    private int gold;
    private int roundNumber;
    private int bossesDefeated;
    private int enemiesSpawnedThisRound;
    private int enemiesToSpawnThisRound;
    private int lastSpawnBatch;
    private boolean bossRound;
    private boolean roundActive;
    private boolean runComplete;
    private boolean easyTextMode;
    private boolean intermissionActive;
    private boolean settingsActive;
    private int pendingRoundNumber;
    private boolean godModeEnabled;
    private boolean roundJumpKeysEnabled;
    private boolean autoModeEnabled;
    private int playerBaseDamage;
    private int autoFireWpm;
    private float autoFireTimer;
    private int enemyKillGoldBonus;
    private int roundClearGoldBonus;
    private int bossClearGoldBonus;

    public SecondScreen(final Drop game) {
        this.game = game;
    }

    @Override
    public void show() {
        shapes = new ShapeRenderer();
        font = new Font(KnownFonts.getMapleMono()).scale(1.25f).useIntegerPositions(false);
        layout.font(font);
        measureLayout.font(font);
        lineHeight = font.cellHeight + 10f;
        boxX = (game.viewport.getWorldWidth() - BOX_WIDTH) * 0.5f;
        coreX = game.viewport.getWorldWidth() * 0.5f;
        restartButtonX = coreX - RESTART_BUTTON_WIDTH * 0.5f;
        restartButtonY = CORE_Y - 120f;
        shopPanelX = coreX - SHOP_PANEL_WIDTH * 0.5f;
        shopPanelY = CORE_Y - 190f;
        settingsPanelX = coreX - SETTINGS_PANEL_WIDTH * 0.5f;
        settingsPanelY = CORE_Y - 150f;
        textWrapWidth = BOX_WIDTH - PADDING * 2f;
        debugPanelX = game.viewport.getWorldWidth() - DEBUG_PANEL_WIDTH - 24f;
        debugPanelY = game.viewport.getWorldHeight() - 300f;

        resetRunState();

        Gdx.input.setInputProcessor(typingInput);
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f);

        game.viewport.apply();
        shapes.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        drawTypingBox();
        drawCore();
        drawEnemies();
        drawBullets();
        drawText(game.batch);
        drawStatus(game.batch);
        drawDamageNumbers(game.batch);
        drawDebugPanel();
        drawShopOverlay();
        drawSettingsOverlay();
        drawOverlay();
    }

    private void update(float delta) {
        TimeState.elapsed += delta;
        if (settingsActive) {
            return;
        }
        if (errorFlash > 0f) {
            errorFlash = Math.max(0f, errorFlash - delta);
        }
        if (roundMessageTimer > 0f) {
            roundMessageTimer = Math.max(0f, roundMessageTimer - delta);
        }
        updateEnemies(delta);
        updateBullets(delta);
        updateDamageNumbers(delta);
        updateAutoMode(delta);
        updateRoundState();
    }

    private void drawTypingBox() {
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.08f, 0.09f, 0.13f, 0.96f);
        shapes.rect(boxX, BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.72f, 0.75f, 0.8f, 0.9f);
        shapes.rect(boxX, BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        shapes.end();
    }

    private void drawCore() {
        float pulse = 0.92f + 0.08f * (float)Math.sin(TimeState.elapsed * 1.6f);

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.06f, 0.08f, 0.11f, 0.85f);
        shapes.circle(coreX, CORE_Y, CORE_RING_RADIUS + 18f);
        shapes.setColor(0.17f, 0.24f, 0.3f, 0.9f);
        shapes.circle(coreX, CORE_Y, CORE_RING_RADIUS);
        shapes.setColor(0.18f * pulse, 0.62f * pulse, 0.92f * pulse, 0.95f);
        shapes.circle(coreX, CORE_Y, CORE_RADIUS);
        shapes.setColor(0.82f, 0.93f, 1f, 0.75f);
        shapes.circle(coreX, CORE_Y, CORE_RADIUS * 0.38f);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.7f, 0.9f, 1f, 0.5f);
        shapes.circle(coreX, CORE_Y, CORE_RING_RADIUS + 10f);
        shapes.setColor(0.5f, 0.8f, 0.95f, 0.85f);
        shapes.circle(coreX, CORE_Y, CORE_RING_RADIUS);
        shapes.end();
    }

    private void drawEnemies() {
        if (enemies.size == 0) return;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : enemies) {
            float radius = enemy.isBoss ? ENEMY_RADIUS * 2.35f : ENEMY_RADIUS;
            shapes.setColor(enemy.fillColor);
            shapes.circle(enemy.position.x, enemy.position.y, radius);
            shapes.setColor(0.98f, 0.84f, 0.64f, 0.9f);
            shapes.circle(enemy.position.x + radius * 0.28f, enemy.position.y + radius * 0.22f, radius * 0.18f);
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        for (Enemy enemy : enemies) {
            shapes.setColor(enemy.outlineColor);
            float radius = enemy.isBoss ? ENEMY_RADIUS * 2.35f : ENEMY_RADIUS;
            shapes.circle(enemy.position.x, enemy.position.y, radius + 1.5f);
        }
        shapes.end();
    }

    private void drawBullets() {
        if (bullets.size == 0) return;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Bullet bullet : bullets) {
            shapes.setColor(0.9f, 0.95f, 1f, 0.95f);
            shapes.circle(bullet.position.x, bullet.position.y, BULLET_RADIUS);
            shapes.setColor(0.36f, 0.78f, 1f, 0.35f);
            shapes.circle(bullet.position.x, bullet.position.y, BULLET_RADIUS * 2.5f);
        }
        shapes.end();
    }

    private void drawText(SpriteBatch batch) {
        batch.begin();
        font.enableShader(batch);
        for (int i = 0; i < visibleLines.size; i++) {
            String line = visibleLines.get(i);
            float y = BOX_Y + BOX_HEIGHT - PADDING - i * lineHeight;
            if (i == currentLineIndex) {
                drawActiveLine(batch, line, y);
            } else {
                drawMarkupText(batch, "[#8C99B2]" + line, boxX + PADDING, y);
            }
        }
        font.pauseDistanceFieldShader(batch);
        batch.end();
    }

    private void drawActiveLine(SpriteBatch batch, String line, float y) {
        String typed = line.substring(0, Math.min(currentCharIndex, line.length()));
        String remaining = line.substring(Math.min(currentCharIndex, line.length()));
        float flash = errorFlash / ERROR_FLASH_DURATION;

        drawMarkupText(batch, "[#59D98C]" + typed, boxX + PADDING, y);
        float offsetX = measureTextWidth(typed);
        if (currentCharIndex < line.length()) {
            String currentCharacter = String.valueOf(line.charAt(currentCharIndex));
            String afterCurrent = line.substring(currentCharIndex + 1);
            String currentMarkup = flash > 0f ? "[#FF5C5C]" + currentCharacter : "[#F2F5FA]" + currentCharacter;
            drawMarkupText(batch, currentMarkup, boxX + PADDING + offsetX, y);
            float currentWidth = measureTextWidth(currentCharacter);
            drawMarkupText(batch, "[#F2F5FA]" + afterCurrent, boxX + PADDING + offsetX + currentWidth, y);

            String prefix = line.substring(0, currentCharIndex);
            float caretX = boxX + PADDING + measureTextWidth(prefix);
            currentWidth = Math.max(8f, currentWidth);
            float underlineY = y - font.cellHeight * 0.22f;
            batch.end();
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.96f, 0.86f, 0.32f, 0.95f);
            shapes.rect(caretX, underlineY, currentWidth, 3f);
            shapes.end();
            batch.begin();
            font.enableShader(batch);
        } else {
            drawMarkupText(batch, "[#F2F5FA]" + remaining, boxX + PADDING + offsetX, y);
        }
    }

    private void drawStatus(SpriteBatch batch) {
        batch.begin();
        font.enableShader(batch);
        //drawMarkupText(batch, "[#AAB0BF]Core stabilized", coreX - 78f, CORE_Y + CORE_RING_RADIUS + 34f);
        drawMarkupText(batch,
            "[" + (coreHealth > 1 ? "#C7E6FF" : "#FF5C5C") + "]Core health: " + coreHealth + "/" + coreMaxHealth,
            coreX - 92f, CORE_Y - CORE_RING_RADIUS - 22f);
        drawMarkupText(batch, "[#AAB0BF]Round " + roundNumber + "/" + TOTAL_ROUNDS + (bossRound ? "  BOSS" : ""), boxX, BOX_Y + BOX_HEIGHT + 72f);
        drawMarkupText(batch, "[#FFF2BC]Gold: " + gold, boxX + 250f, BOX_Y + BOX_HEIGHT + 72f);
        //drawMarkupText(batch, "[#AAB0BF]Enemies in field: " + enemies.size, boxX + BOX_WIDTH - 250f, BOX_Y + BOX_HEIGHT + 42f);
        //drawMarkupText(batch, "[#AAB0BF]Words completed: " + completedWords, boxX, BOX_Y + BOX_HEIGHT + 42f);
        drawMarkupText(batch, "[#AAB0BF]Spawned: " + enemiesSpawnedThisRound + "/" + enemiesToSpawnThisRound, boxX + 250f, BOX_Y + BOX_HEIGHT + 42f);
        //drawMarkupText(batch, "[#AAB0BF]Type exactly as shown. Wrong letters flash red and do not advance.", boxX, BOX_Y - 18f);
        float debugX = boxX + BOX_WIDTH - 430f;
        float debugY = BOX_Y - 52f;
        //drawMarkupText(batch, "[#AAB0BF]Debug panel easy text: " + (easyTextMode ? "ON" : "OFF"), debugX, debugY);
        //drawMarkupText(batch, "[#AAB0BF]" + String.format("Spawn interval: %.2f", currentSpawnInterval), debugX, debugY - 28f);
        //drawMarkupText(batch, "[#AAB0BF]Bosses defeated: " + bossesDefeated, debugX, debugY - 56f);
        //drawMarkupText(batch, "[#AAB0BF]Phase: " + getPhaseIndex(), debugX, debugY - 84f);
        drawMarkupText(batch, "[#AAB0BF]Normal rounds done: " + getNormalRoundsCompleted(), debugX, debugY - 112f);
        //drawMarkupText(batch, "[#AAB0BF]Round spawn budget: " + enemiesToSpawnThisRound, debugX, debugY - 140f);
        //drawMarkupText(batch, "[#AAB0BF]Last spawn batch: " + lastSpawnBatch, debugX, debugY - 168f);
        //drawMarkupText(batch, "[#AAB0BF]" + String.format("Health x%.2f  Speed x%.2f  Damage x%.2f",
            //getHealthMultiplier(), getSpeedMultiplier(), getDamageMultiplier()), debugX, debugY - 196f);
        if (roundMessageTimer > 0f) {
            String message = runComplete ? "Run complete" : (bossRound ? "Boss round" : "Round start");
            drawMarkupText(batch, "[#E5F2FF]" + message, coreX - 64f, CORE_Y + CORE_RING_RADIUS + 72f);
        }
        if (intermissionActive) {
            drawMarkupText(batch, "[#FFF2BC]Shop placeholder", coreX - 94f, CORE_Y + 8f);
            drawMarkupText(batch, "[#C7D6E6]Round " + roundNumber + " cleared. Press Enter to continue.", coreX - 220f, CORE_Y - 28f);
        }
        if (coreHealth <= 0) {
            drawMarkupText(batch, "[#FF7373]Core destroyed", coreX - 88f, CORE_Y + 8f);
        }
        font.pauseDistanceFieldShader(batch);
        batch.end();
    }

    private void drawDamageNumbers(SpriteBatch batch) {
        if (damageNumbers.size == 0) return;

        batch.begin();
        font.enableShader(batch);
        for (DamageNumber number : damageNumbers) {
            drawMarkupText(batch, "[#FFDB8F]" + number.text, number.position.x, number.position.y);
        }
        font.pauseDistanceFieldShader(batch);
        batch.end();
    }

    private void drawOverlay() {
        if (coreHealth > 0) {
            return;
        }

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.02f, 0.02f, 0.03f, 0.78f);
        shapes.rect(0f, 0f, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        shapes.setColor(0.17f, 0.06f, 0.06f, 0.95f);
        shapes.rect(restartButtonX, restartButtonY, RESTART_BUTTON_WIDTH, RESTART_BUTTON_HEIGHT);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.95f, 0.45f, 0.45f, 1f);
        shapes.rect(restartButtonX, restartButtonY, RESTART_BUTTON_WIDTH, RESTART_BUTTON_HEIGHT);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#FF7373]Core destroyed", coreX - 96f, CORE_Y + 54f);
        drawMarkupText(game.batch, "[#C7D6E6]Round reached: " + roundNumber, coreX - 106f, CORE_Y + 16f);
        drawMarkupText(game.batch, "[#FFF2BC]Restart run", restartButtonX + 54f, restartButtonY + 42f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();
    }

    private void drawSettingsOverlay() {
        if (!settingsActive) {
            return;
        }

        float panelHeight = 86f + 5f * (SETTINGS_BUTTON_HEIGHT + SETTINGS_BUTTON_GAP);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.02f, 0.03f, 0.04f, 0.82f);
        shapes.rect(0f, 0f, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        shapes.setColor(0.07f, 0.08f, 0.1f, 0.96f);
        shapes.rect(settingsPanelX, settingsPanelY, SETTINGS_PANEL_WIDTH, panelHeight);

        float buttonX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_BUTTON_WIDTH) * 0.5f;
        shapes.setColor(0.14f, 0.16f, 0.2f, 0.95f);
        for (int i = 0; i < 5; i++) {
            float buttonY = getSettingsButtonY(i);
            shapes.rect(buttonX, buttonY, SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT);
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.52f, 0.58f, 0.7f, 0.95f);
        shapes.rect(settingsPanelX, settingsPanelY, SETTINGS_PANEL_WIDTH, panelHeight);
        shapes.setColor(0.48f, 0.54f, 0.66f, 1f);
        for (int i = 0; i < 5; i++) {
            float buttonY = getSettingsButtonY(i);
            shapes.rect(buttonX, buttonY, SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT);
        }
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#FFF2BC]Settings", settingsPanelX + 22f, settingsPanelY + panelHeight - 44f);
        drawSettingsButtonText("Restart run", 0);
        drawSettingsButtonText("Fullscreen", 1);
        drawSettingsButtonText("Windowed", 2);
        drawSettingsButtonText("Fullscreen Windowed", 3);
        drawSettingsButtonText("Title Screen", 4);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();
    }

    private void drawShopOverlay() {
        if (!intermissionActive) {
            return;
        }

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.03f, 0.04f, 0.05f, 0.82f);
        shapes.rect(0f, 0f, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        shapes.setColor(0.08f, 0.09f, 0.11f, 0.97f);
        shapes.rect(shopPanelX, shopPanelY, SHOP_PANEL_WIDTH, SHOP_PANEL_HEIGHT);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.5f, 0.56f, 0.68f, 0.95f);
        shapes.rect(shopPanelX, shopPanelY, SHOP_PANEL_WIDTH, SHOP_PANEL_HEIGHT);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#FFF2BC]Intermission Shop", shopPanelX + 24f, shopPanelY + SHOP_PANEL_HEIGHT - 24f);
        drawMarkupText(game.batch, "[#C7D6E6]Gold: " + gold, shopPanelX + SHOP_PANEL_WIDTH - 170f, shopPanelY + SHOP_PANEL_HEIGHT - 24f);
        drawMarkupText(game.batch, "[#AAB0BF]Choose upgrades, then continue to the next round.", shopPanelX + 24f, shopPanelY + SHOP_PANEL_HEIGHT - 58f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();

        for (int i = 0; i < shopOffers.size; i++) {
            drawShopCard(shopOffers.get(i), i);
        }
        drawShopContinueButton();
    }

    private void drawShopCard(ShopOffer offer, int index) {
        float cardX = getShopCardX(index);
        float cardY = shopPanelY + 92f;
        boolean affordable = gold >= offer.cost;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        if (offer.purchased) {
            shapes.setColor(0.1f, 0.18f, 0.11f, 0.95f);
        } else if (affordable) {
            shapes.setColor(0.12f, 0.13f, 0.16f, 0.95f);
        } else {
            shapes.setColor(0.15f, 0.09f, 0.09f, 0.95f);
        }
        shapes.rect(cardX, cardY, SHOP_CARD_WIDTH, SHOP_CARD_HEIGHT);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(offer.purchased ? 0.45f : 0.58f, offer.purchased ? 0.86f : 0.62f, offer.purchased ? 0.48f : 0.72f, 1f);
        shapes.rect(cardX, cardY, SHOP_CARD_WIDTH, SHOP_CARD_HEIGHT);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawWrappedMarkupText(game.batch,
            offer.purchased ? "[#C7FFD1]Purchased" : "[#FFF2BC]" + offer.name,
            cardX + 16f, cardY + SHOP_CARD_HEIGHT - 18f, SHOP_CARD_WIDTH - 32f, 2);
        drawWrappedMarkupText(game.batch,
            "[#C7D6E6]" + offer.description,
            cardX + 16f, cardY + SHOP_CARD_HEIGHT - 100f, SHOP_CARD_WIDTH - 32f, 3);
        drawMarkupText(game.batch, offer.purchased ? "[#AAB0BF]Owned" : "[#E5F2FF]Cost: " + offer.cost, cardX + 16f, cardY + 28f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();
    }

    private void drawShopContinueButton() {
        float buttonWidth = 220f;
        float buttonHeight = 42f;
        float buttonX = shopPanelX + SHOP_PANEL_WIDTH - buttonWidth - 24f;
        float buttonY = shopPanelY + 24f;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.16f, 0.2f, 0.28f, 0.96f);
        shapes.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.56f, 0.66f, 0.88f, 1f);
        shapes.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#E5F2FF]Continue", buttonX + 56f, buttonY + 24f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();
    }

    private float getShopCardX(int index) {
        return shopPanelX + 24f + index * (SHOP_CARD_WIDTH + 35f);
    }

    private void drawSettingsButtonText(String label, int index) {
        float buttonX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_BUTTON_WIDTH) * 0.4f;
        float buttonY = getSettingsButtonY(index);
        float textY = buttonY + SETTINGS_BUTTON_HEIGHT * 0.5f + font.cellHeight * -0.2f;
        drawMarkupText(game.batch, "[#E5F2FF]" + label, buttonX + 18f, textY);
    }

    private float getSettingsButtonY(int index) {
        float panelHeight = 86f + 5f * (SETTINGS_BUTTON_HEIGHT + SETTINGS_BUTTON_GAP);
        return settingsPanelY + panelHeight - 82f - (index + 1) * SETTINGS_BUTTON_HEIGHT - index * SETTINGS_BUTTON_GAP;
    }

    private void drawDebugPanel() {
        if (!game.debugMenuUnlocked || coreHealth <= 0) {
            return;
        }

        float panelHeight = DEBUG_PANEL_PADDING * 2f + DEBUG_PANEL_ROW_HEIGHT * 7f;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.05f, 0.06f, 0.08f, 0.92f);
        shapes.rect(debugPanelX, debugPanelY, DEBUG_PANEL_WIDTH, panelHeight);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.46f, 0.52f, 0.62f, 0.95f);
        shapes.rect(debugPanelX, debugPanelY, DEBUG_PANEL_WIDTH, panelHeight);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#FFF2BC]Debug", debugPanelX + DEBUG_PANEL_PADDING, debugPanelY + panelHeight - 12f);
        drawDebugToggleRow("God", godModeEnabled, 0);
        drawDebugToggleRow("Round Keys", roundJumpKeysEnabled, 1);
        drawDebugStepperRow("Damage", Integer.toString(playerBaseDamage), 2);
        drawDebugToggleRow("Easy Text", easyTextMode, 3);
        drawDebugToggleRow("Auto Fire", autoModeEnabled, 4);
        drawDebugStepperRow("WPM", Integer.toString(autoFireWpm), 5);
        drawMarkupText(game.batch, "[#AAB0BF]Up/Down rounds only when enabled", debugPanelX + DEBUG_PANEL_PADDING, getDebugRowCenterY(6) + 6f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();
    }

    private void drawDebugToggleRow(String label, boolean enabled, int rowIndex) {
        float centerY = getDebugRowCenterY(rowIndex);
        drawMarkupText(game.batch, "[#AAB0BF]" + label, debugPanelX + DEBUG_PANEL_PADDING, centerY + 6f);
        float toggleWidth = 74f;
        float toggleHeight = 24f;
        float toggleX = debugPanelX + DEBUG_PANEL_WIDTH - DEBUG_PANEL_PADDING - toggleWidth;
        float toggleY = centerY - toggleHeight * 0.5f;

        game.batch.end();
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        if (enabled) {
            shapes.setColor(0.18f, 0.42f, 0.24f, 0.95f);
        } else {
            shapes.setColor(0.26f, 0.12f, 0.12f, 0.95f);
        }
        shapes.rect(toggleX, toggleY, toggleWidth, toggleHeight);
        shapes.end();
        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, enabled ? "[#C7FFD1]ON" : "[#FFB0B0]OFF", toggleX + 18f, centerY + 6f);
    }

    private void drawDebugStepperRow(String label, String value, int rowIndex) {
        float centerY = getDebugRowCenterY(rowIndex);
        drawMarkupText(game.batch, "[#AAB0BF]" + label, debugPanelX + DEBUG_PANEL_PADDING, centerY + 6f);
        drawMarkupText(game.batch, "[#E5F2FF]" + value, debugPanelX + DEBUG_PANEL_WIDTH - 96f, centerY + 6f);
        drawMarkupText(game.batch, "[#FFF2BC]-", debugPanelX + DEBUG_PANEL_WIDTH - 136f, centerY + 6f);
        drawMarkupText(game.batch, "[#FFF2BC]+", debugPanelX + DEBUG_PANEL_WIDTH - 36f, centerY + 6f);
    }

    private float getDebugRowCenterY(int rowIndex) {
        float panelHeight = DEBUG_PANEL_PADDING * 2f + DEBUG_PANEL_ROW_HEIGHT * 7f;
        return debugPanelY + panelHeight - DEBUG_PANEL_PADDING - 36f - rowIndex * DEBUG_PANEL_ROW_HEIGHT;
    }

    private void drawMarkupText(SpriteBatch batch, String markup, float x, float y) {
        layout.clear();
        font.markup(markup, layout);
        font.drawGlyphs(batch, layout, x, y);
    }

    private void drawWrappedMarkupText(SpriteBatch batch, String markup, float x, float y, float maxWidth, int maxLines) {
        Array<String> lines = wrapMarkupText(markup, maxWidth, maxLines);
        for (int i = 0; i < lines.size; i++) {
            drawMarkupText(batch, lines.get(i), x, y - i * (font.cellHeight * 0.92f));
        }
    }

    private Array<String> wrapMarkupText(String markup, float maxWidth, int maxLines) {
        Array<String> lines = new Array<>();
        String colorPrefix = "";
        String content = markup;
        int tagEnd = markup.indexOf(']');
        if (markup.startsWith("[") && tagEnd >= 0) {
            colorPrefix = markup.substring(0, tagEnd + 1);
            content = markup.substring(tagEnd + 1);
        }

        String[] words = content.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            if (current.length() > 0 && measureTextWidth(candidate) > maxWidth) {
                lines.add(colorPrefix + current);
                current.setLength(0);
                current.append(word);
                if (lines.size >= maxLines - 1) {
                    break;
                }
            } else {
                current.setLength(0);
                current.append(candidate);
            }
        }
        if (lines.size < maxLines && current.length() > 0) {
            lines.add(colorPrefix + current);
        }
        return lines;
    }

    private float measureTextWidth(String text) {
        if (text.isEmpty()) {
            return 0f;
        }
        measureLayout.clear();
        font.markup(text, measureLayout);
        return measureLayout.getWidth();
    }

    private void updateEnemies(float delta) {
        if (coreHealth <= 0) {
            roundActive = false;
        }

        if (roundActive && !runComplete && coreHealth > 0) {
            spawnTimer -= delta;
            while (spawnTimer <= 0f && enemiesSpawnedThisRound < enemiesToSpawnThisRound) {
                spawnTimer += currentSpawnInterval;
                int spawnBatch = bossRound ? 1 : determineSpawnBatchSize();
                int actualSpawnCount = Math.min(spawnBatch, enemiesToSpawnThisRound - enemiesSpawnedThisRound);
                lastSpawnBatch = actualSpawnCount;
                for (int i = 0; i < actualSpawnCount; i++) {
                    enemies.add(bossRound ? createBossEnemy() : createEnemy());
                    enemiesSpawnedThisRound++;
                }
            }
            if (enemiesSpawnedThisRound >= enemiesToSpawnThisRound) {
                roundActive = false;
            }
        }

        Vector2 coreTarget = new Vector2(coreX, CORE_Y);
        for (Enemy enemy : enemies) {
            enemy.velocity.set(coreTarget).sub(enemy.position).nor().scl(enemy.speed * delta);
            enemy.position.add(enemy.velocity);
            float radius = enemy.isBoss ? ENEMY_RADIUS * 2.35f : ENEMY_RADIUS;
            enemy.position.y = Math.max(enemy.position.y, BOX_Y + BOX_HEIGHT + radius + 12f);
        }

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            float radius = enemy.isBoss ? ENEMY_RADIUS * 2.35f : ENEMY_RADIUS;
            float impactDistance = CORE_RADIUS + radius;
            if (enemy.position.dst2(coreX, CORE_Y) <= impactDistance * impactDistance) {
                enemies.removeIndex(i);
                if (!godModeEnabled) {
                    coreHealth = Math.max(0, coreHealth - enemy.damage);
                }
            }
        }
    }

    private void updateBullets(float delta) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (bullet.target != null && bullet.target.health > 0) {
                bullet.velocity.set(bullet.target.position).sub(bullet.position);
                if (!bullet.velocity.isZero(0.001f)) {
                    bullet.velocity.nor().scl(BULLET_SPEED * delta);
                }
            }

            if (bullet.velocity.isZero(0.001f)) {
                bullets.removeIndex(i);
                continue;
            }

            bullet.position.add(bullet.velocity);

            Enemy hitEnemy = findCollidingEnemy(bullet);
            if (hitEnemy != null) {
                applyBulletHit(i, bullet, hitEnemy);
            }
        }
    }

    private Enemy findCollidingEnemy(Bullet bullet) {
        for (Enemy enemy : enemies) {
            float targetRadius = enemy.isBoss ? ENEMY_RADIUS * 2.35f : ENEMY_RADIUS;
            float hitDistance = targetRadius + BULLET_RADIUS + 2f;
            if (bullet.position.dst2(enemy.position) <= hitDistance * hitDistance) {
                return enemy;
            }
        }
        return null;
    }

    private void applyBulletHit(int bulletIndex, Bullet bullet, Enemy target) {
        target.health -= bullet.damage;
        damageNumbers.add(new DamageNumber(new Vector2(target.position.x - 8f, target.position.y + 34f), Integer.toString(bullet.damage)));
        bullets.removeIndex(bulletIndex);

        if (target.health <= 0) {
            gold += getEnemyKillGold(target);
            enemies.removeValue(target, true);
        }
    }

    private void updateDamageNumbers(float delta) {
        for (int i = damageNumbers.size - 1; i >= 0; i--) {
            DamageNumber number = damageNumbers.get(i);
            number.remaining -= delta;
            number.position.y += 36f * delta;
            if (number.remaining <= 0f) {
                damageNumbers.removeIndex(i);
            }
        }
    }

    private void updateAutoMode(float delta) {
        if (!game.debugMenuUnlocked || !autoModeEnabled || coreHealth <= 0 || intermissionActive || runComplete) {
            return;
        }

        float secondsPerShot = 60f / Math.max(1, autoFireWpm);
        autoFireTimer -= delta;
        while (autoFireTimer <= 0f) {
            fireWordShot();
            autoFireTimer += secondsPerShot;
        }
    }

    private Enemy createEnemy() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        int side = MathUtils.random(2);
        Vector2 spawn = new Vector2();

        if (side == 0) {
            spawn.set(MathUtils.random(ENEMY_RADIUS, worldWidth - ENEMY_RADIUS), worldHeight + ENEMY_RADIUS + 10f);
        } else if (side == 1) {
            spawn.set(-ENEMY_RADIUS - 10f, MathUtils.random(CORE_Y + 40f, worldHeight - ENEMY_RADIUS));
        } else {
            spawn.set(worldWidth + ENEMY_RADIUS + 10f, MathUtils.random(CORE_Y + 40f, worldHeight - ENEMY_RADIUS));
        }

        float tint = MathUtils.random(0.82f, 1f);
        float speedMultiplier = getSpeedMultiplier();
        return new Enemy(
            spawn,
            MathUtils.random(ENEMY_MIN_SPEED, ENEMY_MAX_SPEED) * speedMultiplier,
            Math.max(1, Math.round(BASE_ENEMY_HEALTH * getHealthMultiplier())),
            Math.max(1, Math.round(BASE_ENEMY_DAMAGE * getDamageMultiplier())),
            false,
            new Color(0.48f * tint, 0.18f * tint, 0.18f * tint, 1f),
            new Color(0.82f, 0.44f, 0.38f, 0.95f)
        );
    }

    private Enemy createBossEnemy() {
        float worldWidth = game.viewport.getWorldWidth();
        Vector2 spawn = new Vector2(worldWidth * 0.5f, game.viewport.getWorldHeight() + ENEMY_RADIUS * 3f);
        return new Enemy(
            spawn,
            BASE_BOSS_SPEED * getSpeedMultiplier(),
            Math.max(1, Math.round(BASE_BOSS_HEALTH * getHealthMultiplier())),
            Math.max(1, Math.round(BASE_BOSS_DAMAGE * getDamageMultiplier())),
            true,
            new Color(0.3f, 0.12f, 0.12f, 1f),
            new Color(1f, 0.7f, 0.4f, 0.95f)
        );
    }

    private void fireWordShot() {
        Enemy target = findNearestEnemy();
        if (target == null) return;

        bullets.add(new Bullet(new Vector2(coreX, CORE_Y), target, playerBaseDamage));
    }

    private Enemy findNearestEnemy() {
        Enemy nearest = null;
        float bestDistance = Float.MAX_VALUE;
        for (Enemy enemy : enemies) {
            float distance = enemy.position.dst2(coreX, CORE_Y);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }

    private void advanceChar() {
        String line = visibleLines.get(currentLineIndex);
        currentCharIndex++;

        if (currentCharIndex > 0 && currentCharIndex <= line.length() && Character.isWhitespace(line.charAt(currentCharIndex - 1))) {
            completedWords++;
            fireWordShot();
        }

        if (currentCharIndex >= line.length()) {
            visibleLines.removeIndex(0);
            visibleLines.add(nextVisibleLine());
            currentCharIndex = 0;
        }
    }

    private void updateRoundState() {
        if (runComplete || coreHealth <= 0 || intermissionActive) {
            return;
        }
        if (roundActive) {
            return;
        }
        if (enemiesSpawnedThisRound < enemiesToSpawnThisRound) {
            return;
        }
        if (enemies.size > 0) {
            return;
        }
        if (roundNumber >= TOTAL_ROUNDS) {
            runComplete = true;
            roundMessageTimer = 1.6f;
            return;
        }

        gold += getRoundClearGold();
        if (bossRound) {
            gold += getBossClearGold();
            bossesDefeated++;
        }
        pendingRoundNumber = roundNumber + 1;
        intermissionActive = true;
        rollShopOffers();
    }

    private void startRound(int newRoundNumber) {
        roundNumber = newRoundNumber;
        bossRound = isBossRound(newRoundNumber);
        enemiesSpawnedThisRound = 0;
        enemiesToSpawnThisRound = bossRound ? 1 : getScaledSpawnEvents();
        currentSpawnInterval = bossRound ? BASE_SPAWN_INTERVAL : getScaledSpawnInterval();
        spawnTimer = ROUND_START_DELAY;
        roundActive = true;
        roundMessageTimer = 1.4f;
        intermissionActive = false;
        pendingRoundNumber = -1;
        shopOffers.clear();
    }

    private boolean isBossRound(int currentRound) {
        return currentRound % (NORMAL_ROUNDS_PER_BOSS + 1) == 0;
    }

    private int getNormalRoundsCompleted() {
        return Math.max(0, (roundNumber - 1) - bossesDefeated);
    }

    private float getScaledSpawnInterval() {
        float computedSpawnInterval = (float)(BASE_SPAWN_INTERVAL * Math.pow(SPAWN_INTERVAL_DECAY, getNormalRoundsCompleted()));
        return Math.max(MIN_SPAWN_INTERVAL, computedSpawnInterval);
    }

    private int getScaledSpawnEvents() {
        return Math.max(BASE_NORMAL_ROUND_SPAWN_EVENTS,
            Math.round(BASE_NORMAL_ROUND_SPAWN_EVENTS * (float)Math.pow(NORMAL_ROUND_SPAWN_GROWTH, getNormalRoundsCompleted())));
    }

    private int determineSpawnBatchSize() {
        float maxNormalRounds = (TOTAL_ROUNDS - 1f) - ((TOTAL_ROUNDS - 1f) / (NORMAL_ROUNDS_PER_BOSS + 1f));
        float progress = MathUtils.clamp(getNormalRoundsCompleted() / Math.max(1f, maxNormalRounds), 0f, 1f);

        float chanceTwo = MathUtils.lerp(0.025f, 0.08f, progress);
        float chanceThree = MathUtils.lerp(0.005f, 0.05f, progress);
        float chanceFour = MathUtils.lerp(0f, 0.42f, progress);
        float chanceEight = MathUtils.lerp(0f, 0.28f, progress);
        float chanceSixteen = MathUtils.lerp(0.00000000001f, 0.15f, progress);

        float roll = MathUtils.random();
        if ((roll -= chanceSixteen) < 0f) return 16;
        if ((roll -= chanceEight) < 0f) return 8;
        if ((roll -= chanceFour) < 0f) return 4;
        if ((roll -= chanceThree) < 0f) return 3;
        if ((roll -= chanceTwo) < 0f) return 2;
        return 1;
    }

    private float getHealthMultiplier() {
        return (float)Math.pow(1.30, bossesDefeated);
    }

    private float getSpeedMultiplier() {
        return (float)Math.pow(1.08, bossesDefeated);
    }

    private float getDamageMultiplier() {
        return (float)Math.pow(1.20, bossesDefeated);
    }

    private int getPhaseIndex() {
        return ((roundNumber - 1) / (NORMAL_ROUNDS_PER_BOSS + 1)) + 1;
    }

    private int getEnemyKillGold(Enemy enemy) {
        return enemy.isBoss ? 10 * getPhaseIndex() + enemyKillGoldBonus : 1 + enemyKillGoldBonus;
    }

    private int getRoundClearGold() {
        return 10 + 5 * getPhaseIndex() + roundClearGoldBonus;
    }

    private int getBossClearGold() {
        return 25 + 10 * getPhaseIndex() + bossClearGoldBonus;
    }

    private void refillVisibleLines() {
        visibleLines.clear();
        for (int i = 0; i < VISIBLE_LINES; i++) {
            visibleLines.add(nextVisibleLine());
        }
        currentCharIndex = 0;
    }

    private void resetRunState() {
        currentLineIndex = 0;
        currentCharIndex = 0;
        completedWords = 0;
        errorFlash = 0f;
        spawnTimer = ROUND_START_DELAY;
        roundMessageTimer = 0f;
        currentSpawnInterval = BASE_SPAWN_INTERVAL;
        coreMaxHealth = BASE_CORE_MAX_HEALTH;
        coreHealth = coreMaxHealth;
        gold = 0;
        roundNumber = 1;
        bossesDefeated = 0;
        enemiesSpawnedThisRound = 0;
        enemiesToSpawnThisRound = 0;
        lastSpawnBatch = 1;
        bossRound = false;
        roundActive = false;
        runComplete = false;
        intermissionActive = false;
        settingsActive = false;
        pendingRoundNumber = -1;
        godModeEnabled = false;
        roundJumpKeysEnabled = false;
        autoModeEnabled = false;
        playerBaseDamage = 1;
        autoFireWpm = DEFAULT_AUTO_WPM;
        autoFireTimer = 0f;
        enemyKillGoldBonus = 0;
        roundClearGoldBonus = 0;
        bossClearGoldBonus = 0;
        bullets.clear();
        damageNumbers.clear();
        enemies.clear();
        shopOffers.clear();
        lastSpawnBatch = 1;
        refillVisibleLines();
        startRound(roundNumber);
    }

    private void jumpToRound(int targetRound) {
        int clampedRound = MathUtils.clamp(targetRound, 1, TOTAL_ROUNDS);
        roundNumber = clampedRound;
        bossesDefeated = getBossesDefeatedBeforeRound(clampedRound);
        coreHealth = coreMaxHealth;
        errorFlash = 0f;
        roundMessageTimer = 1.2f;
        runComplete = false;
        intermissionActive = false;
        pendingRoundNumber = -1;
        bullets.clear();
        damageNumbers.clear();
        enemies.clear();
        lastSpawnBatch = 1;
        startRound(clampedRound);
    }

    private int getBossesDefeatedBeforeRound(int round) {
        return Math.max(0, (round - 1) / (NORMAL_ROUNDS_PER_BOSS + 1));
    }

    private boolean isInsideRestartButton(float worldX, float worldY) {
        return worldX >= restartButtonX && worldX <= restartButtonX + RESTART_BUTTON_WIDTH
            && worldY >= restartButtonY && worldY <= restartButtonY + RESTART_BUTTON_HEIGHT;
    }

    private boolean handleSettingsClick(float worldX, float worldY) {
        if (!settingsActive) {
            return false;
        }
        float buttonX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_BUTTON_WIDTH) * 0.5f;
        for (int i = 0; i < 5; i++) {
            float buttonY = getSettingsButtonY(i);
            if (worldX >= buttonX && worldX <= buttonX + SETTINGS_BUTTON_WIDTH
                && worldY >= buttonY && worldY <= buttonY + SETTINGS_BUTTON_HEIGHT) {
                activateSettingsButton(i);
                return true;
            }
        }
        return true;
    }

    private boolean handleShopClick(float worldX, float worldY) {
        if (!intermissionActive) {
            return false;
        }

        float buttonWidth = 220f;
        float buttonHeight = 42f;
        float buttonX = shopPanelX + SHOP_PANEL_WIDTH - buttonWidth - 24f;
        float buttonY = shopPanelY + 24f;
        if (worldX >= buttonX && worldX <= buttonX + buttonWidth && worldY >= buttonY && worldY <= buttonY + buttonHeight) {
            startRound(pendingRoundNumber);
            return true;
        }

        for (int i = 0; i < shopOffers.size; i++) {
            ShopOffer offer = shopOffers.get(i);
            float cardX = getShopCardX(i);
            float cardY = shopPanelY + 92f;
            if (worldX >= cardX && worldX <= cardX + SHOP_CARD_WIDTH && worldY >= cardY && worldY <= cardY + SHOP_CARD_HEIGHT) {
                purchaseOffer(offer);
                return true;
            }
        }
        return true;
    }

    private void rollShopOffers() {
        shopOffers.clear();
        Array<ShopOffer> pool = new Array<>();
        pool.add(new ShopOffer("Sharpened Rounds", "+1 base damage", 100, ShopOfferType.DAMAGE_UP));
        pool.add(new ShopOffer("Core Plating", "+1 max core hp and heal 1", 90, ShopOfferType.CORE_UP));
        pool.add(new ShopOffer("Bounty Ledger", "+1 gold from every kill", 120, ShopOfferType.KILL_GOLD_UP));
        pool.add(new ShopOffer("Round Dividend", "+10 round clear gold", 110, ShopOfferType.ROUND_GOLD_UP));
        pool.add(new ShopOffer("Boss Contract", "+20 boss clear gold", 140, ShopOfferType.BOSS_GOLD_UP));

        while (shopOffers.size < SHOP_OFFER_COUNT && pool.size > 0) {
            int idx = MathUtils.random(pool.size - 1);
            shopOffers.add(pool.removeIndex(idx));
        }
    }

    private void purchaseOffer(ShopOffer offer) {
        if (offer.purchased || gold < offer.cost) {
            return;
        }
        gold -= offer.cost;
        offer.purchased = true;

        switch (offer.type) {
            case DAMAGE_UP:
                playerBaseDamage += 1;
                break;
            case CORE_UP:
                coreMaxHealth += 1;
                coreHealth = Math.min(coreMaxHealth, coreHealth + 1);
                break;
            case KILL_GOLD_UP:
                enemyKillGoldBonus += 1;
                break;
            case ROUND_GOLD_UP:
                roundClearGoldBonus += 10;
                break;
            case BOSS_GOLD_UP:
                bossClearGoldBonus += 20;
                break;
            default:
                break;
        }
    }

    private void activateSettingsButton(int index) {
        switch (index) {
            case 0:
                resetRunState();
                break;
            case 1:
                applyFullscreenMode();
                settingsActive = false;
                break;
            case 2:
                applyWindowedMode();
                settingsActive = false;
                break;
            case 3:
                applyBorderlessWindowedMode();
                settingsActive = false;
                break;
            case 4:
                settingsActive = false;
                game.setScreen(new FirstScreen(game));
                break;
            default:
                break;
        }
    }

    private void applyFullscreenMode() {
        Gdx.graphics.setUndecorated(false);
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }

    private void applyWindowedMode() {
        Gdx.graphics.setUndecorated(false);
        Gdx.graphics.setResizable(true);
        Gdx.graphics.setWindowedMode(1600, 900);
    }

    private void applyBorderlessWindowedMode() {
        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setUndecorated(true);
        Gdx.graphics.setResizable(false);
        Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
    }

    private boolean handleDebugPanelClick(float worldX, float worldY) {
        if (!game.debugMenuUnlocked || coreHealth <= 0) {
            return false;
        }
        float panelHeight = DEBUG_PANEL_PADDING * 2f + DEBUG_PANEL_ROW_HEIGHT * 7f;
        if (worldX < debugPanelX || worldX > debugPanelX + DEBUG_PANEL_WIDTH || worldY < debugPanelY || worldY > debugPanelY + panelHeight) {
            return false;
        }

        if (isInsideDebugToggle(worldX, worldY, 0)) {
            godModeEnabled = !godModeEnabled;
            return true;
        }
        if (isInsideDebugToggle(worldX, worldY, 1)) {
            roundJumpKeysEnabled = !roundJumpKeysEnabled;
            return true;
        }
        if (isInsideDebugStepperLeft(worldX, worldY, 2)) {
            playerBaseDamage = Math.max(1, playerBaseDamage - 1);
            return true;
        }
        if (isInsideDebugStepperRight(worldX, worldY, 2)) {
            playerBaseDamage++;
            return true;
        }
        if (isInsideDebugToggle(worldX, worldY, 3)) {
            easyTextMode = !easyTextMode;
            refillVisibleLines();
            return true;
        }
        if (isInsideDebugToggle(worldX, worldY, 4)) {
            autoModeEnabled = !autoModeEnabled;
            autoFireTimer = 0f;
            return true;
        }
        if (isInsideDebugStepperLeft(worldX, worldY, 5)) {
            autoFireWpm = Math.max(1, autoFireWpm - 10);
            autoFireTimer = 0f;
            return true;
        }
        if (isInsideDebugStepperRight(worldX, worldY, 5)) {
            autoFireWpm += 10;
            autoFireTimer = 0f;
            return true;
        }
        return true;
    }

    private boolean isInsideDebugToggle(float worldX, float worldY, int rowIndex) {
        float centerY = getDebugRowCenterY(rowIndex);
        float toggleWidth = 74f;
        float toggleHeight = 24f;
        float toggleX = debugPanelX + DEBUG_PANEL_WIDTH - DEBUG_PANEL_PADDING - toggleWidth;
        float toggleY = centerY - toggleHeight * 0.5f;
        return worldX >= toggleX && worldX <= toggleX + toggleWidth && worldY >= toggleY && worldY <= toggleY + toggleHeight;
    }

    private boolean isInsideDebugStepperLeft(float worldX, float worldY, int rowIndex) {
        float centerY = getDebugRowCenterY(rowIndex);
        return worldX >= debugPanelX + DEBUG_PANEL_WIDTH - 148f && worldX <= debugPanelX + DEBUG_PANEL_WIDTH - 116f
            && worldY >= centerY - 14f && worldY <= centerY + 14f;
    }

    private boolean isInsideDebugStepperRight(float worldX, float worldY, int rowIndex) {
        float centerY = getDebugRowCenterY(rowIndex);
        return worldX >= debugPanelX + DEBUG_PANEL_WIDTH - 48f && worldX <= debugPanelX + DEBUG_PANEL_WIDTH - 16f
            && worldY >= centerY - 14f && worldY <= centerY + 14f;
    }

    private String nextVisibleLine() {
        return easyTextMode ? buildEasyLine() : buildEssayLine();
    }

    private String buildEssayLine() {
        StringBuilder builder = new StringBuilder(96);
        while (true) {
            String token = essay.nextToken();
            String candidate = builder.length() == 0 ? token : builder + " " + token;

            if (builder.length() > 0 && measureTextWidth(candidate + " ") > textWrapWidth) {
                break;
            }

            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(token);

            if (measureTextWidth(builder.toString() + " ") >= textWrapWidth * 0.9f && endsCleanly(builder)) {
                break;
            }
        }
        builder.append(' ');
        return builder.toString();
    }

    private String buildEasyLine() {
        StringBuilder builder = new StringBuilder(96);
        while (true) {
            String candidate = builder.length() == 0 ? "a" : builder + " a";
            if (builder.length() > 0 && measureTextWidth(candidate + " ") > textWrapWidth) {
                break;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append('a');
        }
        builder.append(' ');
        return builder.toString();
    }

    private boolean endsCleanly(StringBuilder builder) {
        char c = builder.charAt(builder.length() - 1);
        return c == '.' || c == ',' || c == ';' || c == '?' || c == '!';
    }

    private final class TypingInput extends InputAdapter {
        @Override
        public boolean keyTyped(char character) {
            if (settingsActive || intermissionActive) {
                return true;
            }
            if (coreHealth <= 0) {
                return true;
            }
            if (character == 8 || character == 127 || character == '\r' || character == '\n') {
                return true;
            }
            if (character == '\t') {
                character = ' ';
            }

            String line = visibleLines.get(currentLineIndex);
            if (currentCharIndex >= line.length()) {
                return true;
            }

            char expected = line.charAt(currentCharIndex);
            if (character == expected) {
                advanceChar();
            } else {
                errorFlash = ERROR_FLASH_DURATION;
            }
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Keys.ESCAPE) {
                settingsActive = !settingsActive;
                return true;
            }
            if (settingsActive) {
                return false;
            }
            if (game.debugMenuUnlocked && roundJumpKeysEnabled && keycode == Keys.UP) {
                jumpToRound(roundNumber + 1);
                return true;
            }
            if (game.debugMenuUnlocked && roundJumpKeysEnabled && keycode == Keys.DOWN) {
                jumpToRound(roundNumber - 1);
                return true;
            }
            if (coreHealth <= 0 && keycode == Keys.ENTER) {
                resetRunState();
                return true;
            }
            if (intermissionActive && keycode == Keys.ENTER && pendingRoundNumber > 0) {
                startRound(pendingRoundNumber);
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            game.viewport.unproject(tempTouch.set(screenX, screenY, 0f));
            if (handleSettingsClick(tempTouch.x, tempTouch.y)) {
                return true;
            }
            if (handleShopClick(tempTouch.x, tempTouch.y)) {
                return true;
            }
            if (handleDebugPanelClick(tempTouch.x, tempTouch.y)) {
                return true;
            }
            if (coreHealth > 0) {
                return false;
            }
            if (isInsideRestartButton(tempTouch.x, tempTouch.y)) {
                resetRunState();
                return true;
            }
            return false;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        game.viewport.update(width, height, true);
        boxX = (game.viewport.getWorldWidth() - BOX_WIDTH) * 0.5f;
        coreX = game.viewport.getWorldWidth() * 0.5f;
        restartButtonX = coreX - RESTART_BUTTON_WIDTH * 0.5f;
        restartButtonY = CORE_Y - 120f;
        textWrapWidth = BOX_WIDTH - PADDING * 2f;
        debugPanelX = game.viewport.getWorldWidth() - DEBUG_PANEL_WIDTH - 24f;
        debugPanelY = game.viewport.getWorldHeight() - 300f;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (shapes != null) {
            shapes.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }

    private static final class InfiniteEssay {
        private static final String[] TOKENS = {
            "The", "\"quick\"", "brown", "fox.", "Jumps", "over", "a", "'LaZy'", "river.",
            "Every", "sentence", "pushes", "the", "player", "forward,", "but", "precision",
            "still", "matters.", "A", "measured", "rhythm", "keeps", "the", "screen", "clear,",
            "while", "panic", "invites", "mistakes.", "Some", "words", "arrive", "softly;",
            "others", "land", "with", "teeth.", "The", "story", "does", "not", "end,", "it",
            "simply", "rolls", "onward", "until", "focus", "breaks.", "Letters", "become",
            "cadence,", "cadence", "becomes", "survival.", "Aren't", "you", "glad", "the",
            "essay", "never", "runs", "dry?"
        };

        private final Random random = new Random(0x71F1E55L);
        private int tokenIndex;

        private String nextToken() {
            if (tokenIndex >= TOKENS.length) {
                tokenIndex = 0;
                shuffleTokens();
            }
            return TOKENS[tokenIndex++];
        }

        private void shuffleTokens() {
            for (int i = TOKENS.length - 1; i > 0; i--) {
                int swap = random.nextInt(i + 1);
                String temp = TOKENS[i];
                TOKENS[i] = TOKENS[swap];
                TOKENS[swap] = temp;
            }
        }

        private boolean endsCleanly(StringBuilder builder) {
            char c = builder.charAt(builder.length() - 1);
            return c == '.' || c == ',' || c == ';' || c == '?' || c == '!';
        }
    }

    private static final class TimeState {
        private static float elapsed;
    }

    private static final class Enemy {
        private final Vector2 position;
        private final Vector2 velocity = new Vector2();
        private final float speed;
        private int health;
        private final int damage;
        private final boolean isBoss;
        private final Color fillColor;
        private final Color outlineColor;

        private Enemy(Vector2 position, float speed, int health, int damage, boolean isBoss, Color fillColor, Color outlineColor) {
            this.position = position;
            this.speed = speed;
            this.health = health;
            this.damage = damage;
            this.isBoss = isBoss;
            this.fillColor = fillColor;
            this.outlineColor = outlineColor;
        }
    }

    private static final class Bullet {
        private final Vector2 position;
        private final Vector2 velocity = new Vector2();
        private final Enemy target;
        private final int damage;

        private Bullet(Vector2 position, Enemy target, int damage) {
            this.position = position;
            this.target = target;
            this.damage = damage;
        }
    }

    private static final class DamageNumber {
        private final Vector2 position;
        private final String text;
        private float remaining = DAMAGE_NUMBER_LIFETIME;

        private DamageNumber(Vector2 position, String text) {
            this.position = position;
            this.text = text;
        }
    }

    private enum ShopOfferType {
        DAMAGE_UP,
        CORE_UP,
        KILL_GOLD_UP,
        ROUND_GOLD_UP,
        BOSS_GOLD_UP
    }

    private static final class ShopOffer {
        private final String name;
        private final String description;
        private final int cost;
        private final ShopOfferType type;
        private boolean purchased;

        private ShopOffer(String name, String description, int cost, ShopOfferType type) {
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.type = type;
            this.purchased = false;
        }
    }
}
