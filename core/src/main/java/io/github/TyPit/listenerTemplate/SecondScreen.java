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
import io.github.TyPit.listenerTemplate.items.AttackData;
import io.github.TyPit.listenerTemplate.items.BossClearedEvent;
import io.github.TyPit.listenerTemplate.items.BulletHitEnemyEvent;
import io.github.TyPit.listenerTemplate.items.CharacterTypedCorrectEvent;
import io.github.TyPit.listenerTemplate.items.CharacterTypedWrongEvent;
import io.github.TyPit.listenerTemplate.items.CoreDamagedEvent;
import io.github.TyPit.listenerTemplate.items.EnemyKilledEvent;
import io.github.TyPit.listenerTemplate.items.GameContext;
import io.github.TyPit.listenerTemplate.items.InventoryEntry;
import io.github.TyPit.listenerTemplate.items.ItemDefinition;
import io.github.TyPit.listenerTemplate.items.ItemEvent;
import io.github.TyPit.listenerTemplate.items.ItemId;
import io.github.TyPit.listenerTemplate.items.ItemPurchasedEvent;
import io.github.TyPit.listenerTemplate.items.ItemRarity;
import io.github.TyPit.listenerTemplate.items.ItemRegistry;
import io.github.TyPit.listenerTemplate.items.ItemSystem;
import io.github.TyPit.listenerTemplate.items.RoundClearedEvent;
import io.github.TyPit.listenerTemplate.items.RoundStartedEvent;
import io.github.TyPit.listenerTemplate.items.WordCompletedEvent;
import java.util.EnumSet;
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
    private static final float ENEMY_MIN_SPEED = 48f;
    private static final float ENEMY_MAX_SPEED = 82f;
    private static final int BASE_ENEMY_HEALTH = 1;
    private static final int BASE_ENEMY_DAMAGE = 1;
    private static final int TOTAL_ROUNDS = 24;
    private static final int NORMAL_ROUNDS_PER_BOSS = 3;
    private static final int BASE_NORMAL_ROUND_SPAWN_EVENTS = 16;
    private static final float NORMAL_ROUND_SPAWN_GROWTH = 1.45f;
    private static final float ROUND_START_DELAY = 0.85f;
    private static final float BASE_BOSS_SPEED = 22f;
    private static final int BASE_BOSS_HEALTH = 8;
    private static final int BASE_BOSS_DAMAGE = 10;
    private static final float BULLET_RADIUS = 6f;
    private static final float BULLET_SPEED = 520f;
    private static final float CRIT_MULTIPLIER = 2f;
    private static final float PROC_CHAIN_STEP_DELAY = 0.02f;
    private static final float DAMAGE_NUMBER_LIFETIME = 0.45f;
    private static final int BASE_CORE_MAX_HEALTH = 3;
    private static final float RESTART_BUTTON_WIDTH = 260f;
    private static final float RESTART_BUTTON_HEIGHT = 64f;
    private static final float SETTINGS_PANEL_WIDTH = 440f;
    private static final float SETTINGS_BUTTON_WIDTH = 390f;
    private static final float SETTINGS_BUTTON_HEIGHT = 42f;
    private static final float SETTINGS_BUTTON_GAP = 14f;
    private static final float SETTINGS_SLIDER_WIDTH = 390f;
    private static final float SETTINGS_SLIDER_HEIGHT = 16f;
    private static final int SETTINGS_BUTTON_COUNT = 5;
    private static final int SETTINGS_SLIDER_COUNT = 2;
    private static final float SHOP_PANEL_WIDTH = 1140f;
    private static final float SHOP_PANEL_HEIGHT = 560f;
    private static final float SHOP_CARD_WIDTH = 340f;
    private static final float SHOP_CARD_HEIGHT = 360f;
    private static final int SHOP_OFFER_COUNT = 3;
    private static final int STARTING_GOLD = 100;
    private static final int BASE_COMMON_ITEM_COST = 45;
    private static final int BASE_UNCOMMON_ITEM_COST = 65;
    private static final int BASE_RARE_ITEM_COST = 90;
    private static final int BASE_LEGENDARY_ITEM_COST = 120;
    private static final float BASE_REROLL_COST_FACTOR = 0.5f;
    private static final float REROLL_COST_GROWTH = 1.3f;
    private static final float MYSTERY_BOX_CHANCE = 0.30f;
    private static final Color SHOP_CARD_BG_PURCHASED = new Color(0.09f, 0.12f, 0.1f, 0.98f);
    private static final Color SHOP_CARD_HEADER_PURCHASED = new Color(0.16f, 0.3f, 0.2f, 0.98f);
    private static final Color SHOP_CARD_BORDER_PURCHASED = new Color(0.46f, 0.8f, 0.58f, 1f);
    private static final Color SHOP_CARD_BG_AFFORDABLE = new Color(0.1f, 0.11f, 0.15f, 0.98f);
    private static final Color SHOP_CARD_BG_UNAFFORDABLE = new Color(0.12f, 0.09f, 0.1f, 0.98f);
    private static final Color SHOP_CARD_HEADER_COMMON = new Color(0.2f, 0.22f, 0.24f, 0.98f);
    private static final Color SHOP_CARD_BORDER_COMMON = new Color(0.6f, 0.62f, 0.68f, 1f);
    private static final Color SHOP_CARD_HEADER_UNCOMMON = new Color(0.18f, 0.28f, 0.2f, 0.98f);
    private static final Color SHOP_CARD_BORDER_UNCOMMON = new Color(0.48f, 0.78f, 0.54f, 1f);
    private static final Color SHOP_CARD_HEADER_RARE = new Color(0.18f, 0.24f, 0.34f, 0.98f);
    private static final Color SHOP_CARD_BORDER_RARE = new Color(0.42f, 0.64f, 0.92f, 1f);
    private static final Color SHOP_CARD_HEADER_LEGENDARY = new Color(0.32f, 0.22f, 0.12f, 0.98f);
    private static final Color SHOP_CARD_BORDER_LEGENDARY = new Color(0.92f, 0.68f, 0.28f, 1f);
    private static final Color SHOP_CARD_HEADER_MYSTERY = new Color(0.26f, 0.16f, 0.32f, 0.98f);
    private static final Color SHOP_CARD_BORDER_MYSTERY = new Color(0.72f, 0.42f, 0.88f, 1f);
    private static final String COST_COLOR_UNAFFORDABLE = "[#FFB0B0]";
    private static final float DEBUG_PANEL_WIDTH = 280f;
    private static final float DEBUG_PANEL_ROW_HEIGHT = 38f;
    private static final float DEBUG_PANEL_PADDING = 12f;
    private static final int DEFAULT_AUTO_WPM = 60;

    private final Drop game;
    private final Array<String> visibleLines = new Array<>(VISIBLE_LINES);
    private final Array<Bullet> bullets = new Array<>();
    private final Array<DamageNumber> damageNumbers = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<ShopOffer> shopOffers = new Array<>();
    private final Array<QueuedProcAction> queuedProcActions = new Array<>();
    private final Array<ExplosionEffect> explosionEffects = new Array<>();
    private final Array<ChainEffect> chainEffects = new Array<>();
    private final Array<CorePulseEffect> corePulseEffects = new Array<>();
    private final InfiniteEssay essay = new InfiniteEssay();
    private final ItemRegistry itemRegistry = new ItemRegistry();
    private final ItemSystem itemSystem = new ItemSystem(itemRegistry);
    private final ScreenGameContext itemContext = new ScreenGameContext();
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
    private boolean isDraggingDebugPanel;
    private float debugPanelDragOffsetX;
    private float debugPanelDragOffsetY;
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
    private boolean infiniteGoldEnabled;
    private boolean itemMenuEnabled;
    private int playerBaseDamage;
    private int autoFireWpm;
    private int rerollsUsedThisShop;
    private float autoFireTimer;
    private String lastItemEventLabel = "None";
    private AttackData currentItemEventAttack;
    private Enemy currentItemEventTarget;
    private int nextAttackId = 1;
    private final float[] nextProcChainTimes = new float[4096];

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
        shopPanelY = CORE_Y - 280f;
        settingsPanelX = coreX - SETTINGS_PANEL_WIDTH * 0.5f;
        settingsPanelY = CORE_Y - getSettingsPanelHeight() * 0.5f;
        textWrapWidth = BOX_WIDTH - PADDING * 2f;
        debugPanelX = game.viewport.getWorldWidth() - DEBUG_PANEL_WIDTH - 24f;
        debugPanelY = game.viewport.getWorldHeight() - getDebugPanelHeight() - 24f;
        isDraggingDebugPanel = false;
        debugPanelDragOffsetX = 0f;
        debugPanelDragOffsetY = 0f;

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
        drawVisualEffects();
        drawText(game.batch);
        drawStatus(game.batch);
        drawDamageNumbers(game.batch);
        drawDebugPanel();
        drawItemMenuOverlay();
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
        updateVisualEffects(delta);
        updateQueuedProcs(delta);
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
            if (bullet.bonusShot) {
                shapes.setColor(1f, 0.92f, 0.55f, 0.98f);
            } else {
                shapes.setColor(0.9f, 0.95f, 1f, 0.95f);
            }
            shapes.circle(bullet.position.x, bullet.position.y, BULLET_RADIUS);
            if (bullet.bonusShot) {
                shapes.setColor(1f, 0.72f, 0.28f, 0.38f);
            } else {
                shapes.setColor(0.36f, 0.78f, 1f, 0.35f);
            }
            shapes.circle(bullet.position.x, bullet.position.y, BULLET_RADIUS * 2.5f);
        }
        shapes.end();
    }

    private void drawVisualEffects() {
        if (explosionEffects.size == 0 && chainEffects.size == 0 && corePulseEffects.size == 0) return;

        shapes.begin(ShapeRenderer.ShapeType.Line);
        for (ExplosionEffect effect : explosionEffects) {
            float alpha = effect.remaining / effect.duration;
            shapes.setColor(1f, 0.74f, 0.28f, alpha);
            shapes.circle(effect.position.x, effect.position.y, effect.radius * (1.2f - alpha * 0.4f), 28);
        }
        for (CorePulseEffect effect : corePulseEffects) {
            float alpha = effect.remaining / effect.duration;
            shapes.setColor(0.55f, 0.82f, 1f, alpha);
            shapes.circle(coreX, CORE_Y, effect.radius * (1.15f - alpha * 0.35f), 32);
        }
        for (ChainEffect effect : chainEffects) {
            float alpha = effect.remaining / effect.duration;
            shapes.setColor(0.72f, 0.92f, 1f, alpha);
            shapes.line(effect.fromX, effect.fromY, effect.toX, effect.toY);
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
            drawMarkupText(batch, (number.critical ? "[#FF8A6B]" : "[#FFDB8F]") + number.text, number.position.x, number.position.y);
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

        float panelHeight = getSettingsPanelHeight();
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.02f, 0.03f, 0.04f, 0.82f);
        shapes.rect(0f, 0f, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        shapes.setColor(0.07f, 0.08f, 0.1f, 0.96f);
        shapes.rect(settingsPanelX, settingsPanelY, SETTINGS_PANEL_WIDTH, panelHeight);

        float buttonX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_BUTTON_WIDTH) * 0.5f;
        shapes.setColor(0.14f, 0.16f, 0.2f, 0.95f);
        for (int i = 0; i < SETTINGS_BUTTON_COUNT; i++) {
            float buttonY = getSettingsButtonY(i);
            shapes.rect(buttonX, buttonY, SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT);
        }
        for (int i = 0; i < SETTINGS_SLIDER_COUNT; i++) {
            float sliderX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_SLIDER_WIDTH) * 0.5f;
            float sliderY = getSettingsSliderTrackY(i);
            shapes.setColor(0.14f, 0.16f, 0.2f, 0.95f);
            shapes.rect(sliderX, sliderY, SETTINGS_SLIDER_WIDTH, SETTINGS_SLIDER_HEIGHT);
            float fillWidth = SETTINGS_SLIDER_WIDTH * (i == 0 ? game.musicVolume : game.soundVolume);
            shapes.setColor(0.78f, 0.65f, 0.28f, 0.95f);
            shapes.rect(sliderX, sliderY, fillWidth, SETTINGS_SLIDER_HEIGHT);
            shapes.setColor(0.96f, 0.92f, 0.74f, 1f);
            float knobX = sliderX + fillWidth;
            shapes.rect(knobX - 4f, sliderY - 5f, 8f, SETTINGS_SLIDER_HEIGHT + 10f);
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.52f, 0.58f, 0.7f, 0.95f);
        shapes.rect(settingsPanelX, settingsPanelY, SETTINGS_PANEL_WIDTH, panelHeight);
        shapes.setColor(0.48f, 0.54f, 0.66f, 1f);
        for (int i = 0; i < SETTINGS_BUTTON_COUNT; i++) {
            float buttonY = getSettingsButtonY(i);
            shapes.rect(buttonX, buttonY, SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT);
        }
        for (int i = 0; i < SETTINGS_SLIDER_COUNT; i++) {
            float sliderX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_SLIDER_WIDTH) * 0.5f;
            float sliderY = getSettingsSliderTrackY(i);
            shapes.rect(sliderX, sliderY, SETTINGS_SLIDER_WIDTH, SETTINGS_SLIDER_HEIGHT);
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
        drawSettingsSliderText("Music Volume", game.musicVolume, 0);
        drawSettingsSliderText("Sound Volume", game.soundVolume, 1);
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
        drawMarkupText(game.batch, "[#AAB0BF]Choose upgrades, reroll if needed, and watch for mystery boxes.", shopPanelX + 24f, shopPanelY + SHOP_PANEL_HEIGHT - 58f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();

        for (int i = 0; i < shopOffers.size; i++) {
            drawShopCard(shopOffers.get(i), i);
        }
        drawShopRerollButton();
        drawShopContinueButton();
    }

    private String getRarityColorMarkup(ItemRarity rarity) {
        if (rarity == null) return "[#999EAD]";
        switch (rarity) {
            case UNCOMMON: return "[#7AC689]"; // Green
            case RARE: return "[#6BA3EA]"; // Blue
            case LEGENDARY: return "[#EAAD47]"; // Gold
            case COMMON:
            default: return "[#999EAD]"; // Grey/White
        }
    }

    private void drawShopCard(ShopOffer offer, int index) {
        float cardX = getShopCardX(index);
        float cardY = getShopCardY();
        boolean affordable = infiniteGoldEnabled || gold >= offer.cost;
        float headerHeight = 75f;
        float footerHeight = 55f;

        // 1. Determine colors based on state
        Color bgColor, headerColor, borderColor;
        String costColorMarkup;
        if (offer.purchased) {
            bgColor = SHOP_CARD_BG_PURCHASED;
            headerColor = SHOP_CARD_HEADER_PURCHASED;
            borderColor = SHOP_CARD_BORDER_PURCHASED;
            costColorMarkup = "[#AAB0BF]";
        } else { 
            bgColor = affordable ? SHOP_CARD_BG_AFFORDABLE : SHOP_CARD_BG_UNAFFORDABLE;
            costColorMarkup = affordable ? "[#E5F2FF]" : COST_COLOR_UNAFFORDABLE;
            
            if (offer.isMystery) {
                headerColor = SHOP_CARD_HEADER_MYSTERY;
                borderColor = SHOP_CARD_BORDER_MYSTERY;
            } else {
                switch (offer.rarity) {
                    case UNCOMMON:
                        headerColor = SHOP_CARD_HEADER_UNCOMMON;
                        borderColor = SHOP_CARD_BORDER_UNCOMMON;
                        break;
                    case RARE:
                        headerColor = SHOP_CARD_HEADER_RARE;
                        borderColor = SHOP_CARD_BORDER_RARE;
                        break;
                    case LEGENDARY:
                        headerColor = SHOP_CARD_HEADER_LEGENDARY;
                        borderColor = SHOP_CARD_BORDER_LEGENDARY;
                        break;
                    case COMMON:
                    default:
                        headerColor = SHOP_CARD_HEADER_COMMON;
                        borderColor = SHOP_CARD_BORDER_COMMON;
                        break;
                }
            }
        }

        // 2. Draw shapes
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(bgColor);
        shapes.rect(cardX, cardY, SHOP_CARD_WIDTH, SHOP_CARD_HEIGHT);
        shapes.setColor(headerColor);
        shapes.rect(cardX, cardY + SHOP_CARD_HEIGHT - headerHeight, SHOP_CARD_WIDTH, headerHeight);
        shapes.rect(cardX, cardY, SHOP_CARD_WIDTH, footerHeight);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(borderColor);
        shapes.rect(cardX, cardY, SHOP_CARD_WIDTH, SHOP_CARD_HEIGHT);
        shapes.line(cardX, cardY + SHOP_CARD_HEIGHT - headerHeight, cardX + SHOP_CARD_WIDTH, cardY + SHOP_CARD_HEIGHT - headerHeight);
        shapes.line(cardX, cardY + footerHeight, cardX + SHOP_CARD_WIDTH, cardY + footerHeight);
        shapes.end();

        // 3. Draw text
        game.batch.begin();
        font.enableShader(game.batch);
        String titleColor = offer.isMystery && !offer.purchased ? "[#E0B0FF]" : (offer.purchased ? "[#C7FFD1]" : getRarityColorMarkup(offer.rarity));
        drawWrappedMarkupText(game.batch, "[%75]" + titleColor + offer.name, cardX + 16f, cardY + SHOP_CARD_HEIGHT - 26f, SHOP_CARD_WIDTH - 32f, 2);
        
        String descMarkup = (offer.isMystery ? "[%70]" : "[%85]") + "[#C7D6E6]" + offer.description;
        float descLineHeight = offer.isMystery ? 0.85f : 0.95f;
        drawWrappedMarkupText(game.batch, descMarkup, cardX + 16f, cardY + SHOP_CARD_HEIGHT - headerHeight - 24f, SHOP_CARD_WIDTH - 32f, 6, descLineHeight);
        
        String costText = offer.purchased ? costColorMarkup + "OWNED" : costColorMarkup + "Cost: " + offer.cost;
        drawMarkupText(game.batch, costText, cardX + 16f, cardY + footerHeight - 22f);
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

    private void drawShopRerollButton() {
        float buttonWidth = 220f;
        float buttonHeight = 42f;
        float buttonX = shopPanelX + SHOP_PANEL_WIDTH - buttonWidth * 2f - 40f;
        float buttonY = shopPanelY + 24f;
        int rerollCost = getCurrentRerollCost();
        boolean affordable = infiniteGoldEnabled || gold >= rerollCost;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(affordable ? new Color(0.2f, 0.18f, 0.1f, 0.96f) : new Color(0.16f, 0.11f, 0.11f, 0.96f));
        shapes.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(affordable ? new Color(0.9f, 0.74f, 0.34f, 1f) : new Color(0.7f, 0.44f, 0.44f, 1f));
        shapes.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#FFF2BC]Reroll", buttonX + 26f, buttonY + 24f);
        drawMarkupText(game.batch, (affordable ? "[#E5F2FF]" : COST_COLOR_UNAFFORDABLE) + rerollCost, buttonX + 132f, buttonY + 24f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();
    }

    private float getShopCardX(int index) {
        float totalCardsWidth = SHOP_OFFER_COUNT * SHOP_CARD_WIDTH;
        float spacing = (SHOP_PANEL_WIDTH - totalCardsWidth) / (SHOP_OFFER_COUNT + 1);
        return shopPanelX + spacing + index * (SHOP_CARD_WIDTH + spacing);
    }

    private float getShopCardY() {
        return shopPanelY + 96f;
    }

    private void drawSettingsButtonText(String label, int index) {
        float buttonX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_BUTTON_WIDTH) * 0.4f;
        float buttonY = getSettingsButtonY(index);
        float textY = buttonY + SETTINGS_BUTTON_HEIGHT * 0.5f + font.cellHeight * -0.2f;
        drawMarkupText(game.batch, "[#E5F2FF]" + label, buttonX + 18f, textY);
    }

    private float getSettingsButtonY(int index) {
        float panelHeight = getSettingsPanelHeight();
        return settingsPanelY + panelHeight - 82f - (index + 1) * SETTINGS_BUTTON_HEIGHT - index * SETTINGS_BUTTON_GAP;
    }

    private float getSettingsPanelHeight() {
        return 104f + SETTINGS_BUTTON_COUNT * (SETTINGS_BUTTON_HEIGHT + SETTINGS_BUTTON_GAP)
            + SETTINGS_SLIDER_COUNT * (SETTINGS_BUTTON_HEIGHT + SETTINGS_BUTTON_GAP);
    }

    private float getSettingsSliderTrackY(int index) {
        float buttonX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_BUTTON_WIDTH) * 0.5f;
        float baseY = getSettingsButtonY(SETTINGS_BUTTON_COUNT - 1) - SETTINGS_BUTTON_GAP - SETTINGS_BUTTON_HEIGHT;
        return baseY - index * (SETTINGS_BUTTON_HEIGHT + SETTINGS_BUTTON_GAP) + (SETTINGS_BUTTON_HEIGHT - SETTINGS_SLIDER_HEIGHT) * 0.5f;
    }

    private void drawSettingsSliderText(String label, float value, int index) {
        float sliderX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_SLIDER_WIDTH) * 0.5f;
        float rowY = getSettingsSliderTrackY(index);
        drawMarkupText(game.batch, "[#E5F2FF]" + label, sliderX, rowY + 34f);
        drawMarkupText(game.batch, "[#FFF2BC]" + Math.round(value * 100f) + "%", sliderX + SETTINGS_SLIDER_WIDTH - 64f, rowY + 34f);
    }

    private void drawDebugPanel() {
        if (!game.debugMenuUnlocked || coreHealth <= 0) {
            return;
        }

        float panelHeight = getDebugPanelHeight();
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
        drawDebugStepperRow("Damage", Integer.toString(getCurrentBaseDamage()), 2);
        drawDebugToggleRow("Easy Text", easyTextMode, 3);
        drawDebugToggleRow("Auto Fire", autoModeEnabled, 4);
        drawDebugStepperRow("WPM", Integer.toString(autoFireWpm), 5);
        drawDebugToggleRow("Infinite Gold", infiniteGoldEnabled, 6);
        drawDebugToggleRow("Item Spawner", itemMenuEnabled, 7);
        drawWrappedMarkupText(game.batch, "[#AAB0BF]Up/Down rounds only when enabled",
            debugPanelX + DEBUG_PANEL_PADDING, getDebugRowCenterY(8) + 6f, DEBUG_PANEL_WIDTH - DEBUG_PANEL_PADDING * 2f, 2);
        drawWrappedMarkupText(game.batch, "[#AAB0BF]Items: " + getInventorySummary(),
            debugPanelX + DEBUG_PANEL_PADDING, getDebugRowCenterY(10) + 6f, DEBUG_PANEL_WIDTH - DEBUG_PANEL_PADDING * 2f, 3);
        drawWrappedMarkupText(game.batch, "[#AAB0BF]Stats: " + getPassiveSummary(),
            debugPanelX + DEBUG_PANEL_PADDING, getDebugRowCenterY(13) + 6f, DEBUG_PANEL_WIDTH - DEBUG_PANEL_PADDING * 2f, 2);
        drawWrappedMarkupText(game.batch, "[#AAB0BF]Last item event: " + lastItemEventLabel,
            debugPanelX + DEBUG_PANEL_PADDING, getDebugRowCenterY(15) + 6f, DEBUG_PANEL_WIDTH - DEBUG_PANEL_PADDING * 2f, 2);
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
        float panelHeight = getDebugPanelHeight();
        return debugPanelY + panelHeight - DEBUG_PANEL_PADDING - 36f - rowIndex * DEBUG_PANEL_ROW_HEIGHT;
    }

    private float getDebugPanelHeight() {
        return DEBUG_PANEL_PADDING * 2f + DEBUG_PANEL_ROW_HEIGHT * 17f;
    }

    private void drawItemMenuOverlay() {
        if (!itemMenuEnabled || !game.debugMenuUnlocked || coreHealth <= 0) {
            return;
        }

        float menuWidth = 860f;
        float menuHeight = 560f;
        float menuX = coreX - menuWidth * 0.5f;
        float menuY = CORE_Y - menuHeight * 0.5f;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.04f, 0.05f, 0.07f, 0.98f);
        shapes.rect(menuX, menuY, menuWidth, menuHeight);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.46f, 0.52f, 0.62f, 0.95f);
        shapes.rect(menuX, menuY, menuWidth, menuHeight);
        shapes.end();

        game.batch.begin();
        font.enableShader(game.batch);
        drawMarkupText(game.batch, "[#FFF2BC]Debug Item Spawner", menuX + 24f, menuY + menuHeight - 24f);
        font.pauseDistanceFieldShader(game.batch);
        game.batch.end();

        int index = 0;
        float cardWidth = 190f;
        float cardHeight = 100f;
        float padding = 16f;
        float startX = menuX + 26f;
        float startY = menuY + menuHeight - 64f;

        for (ItemDefinition def : itemRegistry.getAll()) {
            int col = index % 4;
            int row = index / 4;
            float cardX = startX + col * (cardWidth + padding);
            float cardY = startY - cardHeight - row * (cardHeight + padding);
            float btnWidth = 70f;
            float btnHeight = 28f;
            float btnY = cardY + 8f;
            float plusX = cardX + 8f;
            float minusX = cardX + cardWidth - btnWidth - 8f;

            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.1f, 0.12f, 0.15f, 0.95f);
            shapes.rect(cardX, cardY, cardWidth, cardHeight);
            shapes.setColor(0.18f, 0.42f, 0.24f, 0.95f);
            shapes.rect(plusX, btnY, btnWidth, btnHeight);
            shapes.setColor(0.42f, 0.18f, 0.18f, 0.95f);
            shapes.rect(minusX, btnY, btnWidth, btnHeight);
            shapes.end();

            game.batch.begin();
            font.enableShader(game.batch);
            String titleColor = getRarityColorMarkup(def.rarity);
            drawWrappedMarkupText(game.batch, titleColor + def.name, cardX + 8f, cardY + cardHeight - 8f, cardWidth - 16f, 2);
            drawMarkupText(game.batch, "[#C7D6E6]Owned: " + itemSystem.getStacks(def.id), cardX + 8f, cardY + cardHeight - 44f);
            drawMarkupText(game.batch, "[#C7FFD1]+", plusX + 28f, btnY + 22f);
            drawMarkupText(game.batch, "[#FFB0B0]-", minusX + 30f, btnY + 22f);
            font.pauseDistanceFieldShader(game.batch);
            game.batch.end();
            index++;
        }
    }

    private void drawMarkupText(SpriteBatch batch, String markup, float x, float y) {
        layout.clear();
        font.markup(markup, layout);
        font.drawGlyphs(batch, layout, x, y);
    }

    private void drawWrappedMarkupText(SpriteBatch batch, String markup, float x, float y, float maxWidth, int maxLines) {
        drawWrappedMarkupText(batch, markup, x, y, maxWidth, maxLines, 1.25f);
    }

    private void drawWrappedMarkupText(SpriteBatch batch, String markup, float x, float y, float maxWidth, int maxLines, float lineHeightMultiplier) {
        Array<String> lines = wrapMarkupText(markup, maxWidth, maxLines);
        float lineHeight = font.cellHeight * lineHeightMultiplier;
        for (int i = 0; i < lines.size; i++) {
            drawMarkupText(batch, lines.get(i), x, y - i * lineHeight);
        }
    }

    private Array<String> wrapMarkupText(String markup, float maxWidth, int maxLines) {
        Array<String> lines = new Array<>();
        String colorPrefix = "";
        String content = markup;
        while (content.startsWith("[")) {
            int tagEnd = content.indexOf(']');
            if (tagEnd >= 0) {
                colorPrefix += content.substring(0, tagEnd + 1);
                content = content.substring(tagEnd + 1);
            } else {
                break;
            }
        }

        String[] words = content.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            if (current.length() > 0 && measureTextWidth(colorPrefix + candidate) > maxWidth) {
                lines.add(colorPrefix + current.toString());
                if (lines.size >= maxLines) {
                    current.setLength(0);
                    break;
                }
                current.setLength(0);
                current.append(word);
            } else {
                current.setLength(0);
                current.append(candidate);
            }
        }
        if (lines.size < maxLines && current.length() > 0) {
            lines.add(colorPrefix + current.toString());
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
            if (enemy.slowTimer > 0f) {
                enemy.slowTimer = Math.max(0f, enemy.slowTimer - delta);
                if (enemy.slowTimer == 0f) {
                    enemy.slowMultiplier = 1f;
                }
            }
            enemy.velocity.set(coreTarget).sub(enemy.position).nor().scl(enemy.speed * enemy.slowMultiplier * delta);
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
                    dispatchItemEvent(new CoreDamagedEvent(enemy.damage, coreHealth));
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
        target.health -= bullet.attack.damage;
        damageNumbers.add(new DamageNumber(new Vector2(target.position.x - 8f, target.position.y + 34f),
            bullet.attack.critical ? "CRIT " + bullet.attack.damage : Integer.toString(bullet.attack.damage), bullet.attack.critical));
        bullets.removeIndex(bulletIndex);
        boolean killed = target.health <= 0;
        dispatchItemEvent(new BulletHitEnemyEvent(bullet.attack.damage, killed, target.isBoss, bullet.attack.critical), bullet.attack, target);

        if (killed) {
            int killGold = getEnemyKillGold(target);
            gold += killGold;
            enemies.removeValue(target, true);
            dispatchItemEvent(new EnemyKilledEvent(target.isBoss, killGold, bullet.attack.critical), bullet.attack, target);
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

    private void updateVisualEffects(float delta) {
        for (int i = explosionEffects.size - 1; i >= 0; i--) {
            ExplosionEffect effect = explosionEffects.get(i);
            effect.remaining -= delta;
            if (effect.remaining <= 0f) {
                explosionEffects.removeIndex(i);
            }
        }
        for (int i = chainEffects.size - 1; i >= 0; i--) {
            ChainEffect effect = chainEffects.get(i);
            effect.remaining -= delta;
            if (effect.remaining <= 0f) {
                chainEffects.removeIndex(i);
            }
        }
        for (int i = corePulseEffects.size - 1; i >= 0; i--) {
            CorePulseEffect effect = corePulseEffects.get(i);
            effect.remaining -= delta;
            if (effect.remaining <= 0f) {
                corePulseEffects.removeIndex(i);
            }
        }
    }

    private void updateQueuedProcs(float delta) {
        for (int i = queuedProcActions.size - 1; i >= 0; i--) {
            QueuedProcAction action = queuedProcActions.get(i);
            action.remainingDelay -= delta;
            if (action.remainingDelay <= 0f) {
                queuedProcActions.removeIndex(i);
                action.runnable.run();
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

    private AttackData fireWordShot() {
        Enemy target = findNearestEnemy();
        if (target == null) return null;

        AttackData attack = createAttack(getCurrentBaseDamage(), "word", null);
        bullets.add(new Bullet(new Vector2(coreX, CORE_Y), target, attack, false));
        SimpleSfx.playShot(game.soundVolume);
        return attack;
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

    private Enemy findStrongestEnemy() {
        Enemy strongest = null;
        int bestHealth = Integer.MIN_VALUE;
        float bestDistance = Float.MAX_VALUE;
        for (Enemy enemy : enemies) {
            float distance = enemy.position.dst2(coreX, CORE_Y);
            if (enemy.health > bestHealth || (enemy.health == bestHealth && distance < bestDistance)) {
                strongest = enemy;
                bestHealth = enemy.health;
                bestDistance = distance;
            }
        }
        return strongest;
    }

    private Array<Enemy> findNearbyEnemies(Enemy centerEnemy, float radius, int maxTargets, boolean excludeCenter) {
        Array<Enemy> candidates = new Array<Enemy>();
        if (centerEnemy == null || maxTargets <= 0) {
            return candidates;
        }
        float radiusSquared = radius * radius;
        for (Enemy enemy : enemies) {
            if (excludeCenter && enemy == centerEnemy) {
                continue;
            }
            if (enemy.position.dst2(centerEnemy.position) <= radiusSquared) {
                candidates.add(enemy);
            }
        }
        candidates.sort((a, b) -> Float.compare(a.position.dst2(centerEnemy.position), b.position.dst2(centerEnemy.position)));
        while (candidates.size > maxTargets) {
            candidates.pop();
        }
        return candidates;
    }

    private void advanceChar() {
        String line = visibleLines.get(currentLineIndex);
        char typedCharacter = line.charAt(currentCharIndex);
        currentCharIndex++;
        dispatchItemEvent(new CharacterTypedCorrectEvent(typedCharacter, completedWords, currentCharIndex));

        if (currentCharIndex > 0 && currentCharIndex <= line.length() && Character.isWhitespace(line.charAt(currentCharIndex - 1))) {
            completedWords++;
            String completedWord = extractCompletedWord(line, currentCharIndex);
            AttackData wordAttack = fireWordShot();
            dispatchItemEvent(new WordCompletedEvent(completedWord, completedWords, errorFlash <= 0f), wordAttack);
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

        int roundReward = getRoundClearGold();
        gold += roundReward;
        dispatchItemEvent(new RoundClearedEvent(roundNumber, getPhaseIndex(), roundReward));
        if (bossRound) {
            int bossReward = getBossClearGold();
            gold += bossReward;
            dispatchItemEvent(new BossClearedEvent(roundNumber, getPhaseIndex(), bossReward));
            bossesDefeated++;
        }
        pendingRoundNumber = roundNumber + 1;
        coreHealth = coreMaxHealth;
        intermissionActive = true;
        updateMusicForCurrentState();
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
        rerollsUsedThisShop = 0;
        shopOffers.clear();
        updateMusicForCurrentState();
        dispatchItemEvent(new RoundStartedEvent(roundNumber, getPhaseIndex(), bossRound));
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

    private int getCurrentBaseDamage() {
        return playerBaseDamage + itemSystem.getStats().baseDamage;
    }

    private int getCurrentCoreMaxHealth() {
        return Math.max(1, Math.round(BASE_CORE_MAX_HEALTH * itemSystem.getStats().coreMaxHealthMultiplier));
    }

    private float getShopPhaseCostMultiplier() {
        switch (getPhaseIndex()) {
            case 1: return 1.00f;
            case 2: return 1.10f;
            case 3: return 1.25f;
            case 4: return 1.45f;
            case 5: return 1.70f;
            default: return 2.00f;
        }
    }

    private int getShopOfferCost(ItemDefinition definition) {
        return Math.max(1, Math.round(getBaseItemCost(definition.rarity) * getShopPhaseCostMultiplier()));
    }

    private int getCurrentRerollCost() {
        float scaledBase = BASE_COMMON_ITEM_COST * BASE_REROLL_COST_FACTOR * getShopPhaseCostMultiplier();
        return Math.max(1, Math.round((float)(scaledBase * Math.pow(REROLL_COST_GROWTH, rerollsUsedThisShop))));
    }

    private int getBaseItemCost(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return BASE_COMMON_ITEM_COST;
            case UNCOMMON:
                return BASE_UNCOMMON_ITEM_COST;
            case RARE:
                return BASE_RARE_ITEM_COST;
            case LEGENDARY:
            default:
                return BASE_LEGENDARY_ITEM_COST;
        }
    }

    private void dispatchItemEvent(ItemEvent event) {
        dispatchItemEvent(event, null, null);
    }

    private void dispatchItemEvent(ItemEvent event, AttackData sourceAttack) {
        dispatchItemEvent(event, sourceAttack, null);
    }

    private void dispatchItemEvent(ItemEvent event, AttackData sourceAttack, Enemy sourceTarget) {
        lastItemEventLabel = event.type().name();
        AttackData previousAttack = currentItemEventAttack;
        Enemy previousTarget = currentItemEventTarget;
        currentItemEventAttack = sourceAttack;
        currentItemEventTarget = sourceTarget;
        try {
            itemSystem.dispatch(event, itemContext);
        } finally {
            currentItemEventAttack = previousAttack;
            currentItemEventTarget = previousTarget;
        }
    }

    private AttackData createAttack(int baseDamage, String source, AttackData parentAttack) {
        int resolvedBaseDamage = Math.max(1, baseDamage);
        if (parentAttack != null) {
            AttackData child = parentAttack.createChild(resolvedBaseDamage, source, null);
            int damage = child.critical ? Math.max(1, Math.round(resolvedBaseDamage * child.critMultiplier)) : resolvedBaseDamage;
            return new AttackData(child.rootAttackId, child.chainDepth, resolvedBaseDamage, damage, child.critical, child.critMultiplier,
                source, child.copyTriggeredItems());
        }

        boolean critical = MathUtils.random() < MathUtils.clamp(itemSystem.getStats().critChance, 0f, 1f);
        int damage = critical ? Math.max(1, Math.round(resolvedBaseDamage * CRIT_MULTIPLIER)) : resolvedBaseDamage;
        return new AttackData(nextAttackId++, 0, resolvedBaseDamage, damage, critical, CRIT_MULTIPLIER, source, EnumSet.noneOf(ItemId.class));
    }

    private AttackData createChildAttack(int baseDamage, String source, AttackData parentAttack, ItemId sourceItemId) {
        int resolvedBaseDamage = Math.max(1, baseDamage);
        if (parentAttack == null) {
            return createAttack(resolvedBaseDamage, source, null);
        }
        AttackData child = parentAttack.createChild(resolvedBaseDamage, source, sourceItemId);
        int damage = child.critical ? Math.max(1, Math.round(resolvedBaseDamage * child.critMultiplier)) : resolvedBaseDamage;
        return new AttackData(child.rootAttackId, child.chainDepth, resolvedBaseDamage, damage, child.critical, child.critMultiplier,
            source, child.copyTriggeredItems());
    }

    private AttackData createChildAttackWithExactDamage(int finalDamage, String source, AttackData parentAttack, ItemId sourceItemId) {
        int resolvedDamage = Math.max(1, finalDamage);
        if (parentAttack == null) {
            return createAttack(resolvedDamage, source, null);
        }
        AttackData child = parentAttack.createChild(resolvedDamage, source, sourceItemId);
        return new AttackData(child.rootAttackId, child.chainDepth, resolvedDamage, resolvedDamage, child.critical, child.critMultiplier,
            source, child.copyTriggeredItems());
    }

    private float reserveProcDelay(AttackData attack) {
        if (attack == null) {
            return 0f;
        }
        int index = Math.floorMod(attack.rootAttackId, nextProcChainTimes.length);
        float reservedDelay = nextProcChainTimes[index];
        nextProcChainTimes[index] += PROC_CHAIN_STEP_DELAY;
        return reservedDelay;
    }

    private int getEnemyKillGold(Enemy enemy) {
        return enemy.isBoss ? 10 * getPhaseIndex() + itemSystem.getStats().enemyKillGoldBonus
            : 1 + itemSystem.getStats().enemyKillGoldBonus;
    }

    private int getRoundClearGold() {
        return 10 + 5 * getPhaseIndex() + itemSystem.getStats().roundClearGoldBonus;
    }

    private int getBossClearGold() {
        return 25 + 10 * getPhaseIndex() + itemSystem.getStats().bossClearGoldBonus;
    }

    private void refillVisibleLines() {
        visibleLines.clear();
        for (int i = 0; i < VISIBLE_LINES; i++) {
            visibleLines.add(nextVisibleLine());
        }
        currentCharIndex = 0;
    }

    private void resetRunState() {
        itemSystem.reset();
        currentLineIndex = 0;
        currentCharIndex = 0;
        completedWords = 0;
        errorFlash = 0f;
        spawnTimer = ROUND_START_DELAY;
        roundMessageTimer = 0f;
        currentSpawnInterval = BASE_SPAWN_INTERVAL;
        coreMaxHealth = getCurrentCoreMaxHealth();
        coreHealth = coreMaxHealth;
        gold = STARTING_GOLD;
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
        infiniteGoldEnabled = false;
        itemMenuEnabled = false;
        playerBaseDamage = 1;
        autoFireWpm = DEFAULT_AUTO_WPM;
        rerollsUsedThisShop = 0;
        autoFireTimer = 0f;
        lastItemEventLabel = "None";
        currentItemEventAttack = null;
        currentItemEventTarget = null;
        bullets.clear();
        damageNumbers.clear();
        enemies.clear();
        shopOffers.clear();
        queuedProcActions.clear();
        explosionEffects.clear();
        chainEffects.clear();
        corePulseEffects.clear();
        for (int i = 0; i < nextProcChainTimes.length; i++) {
            nextProcChainTimes[i] = 0f;
        }
        lastSpawnBatch = 1;
        refillVisibleLines();
        startRound(roundNumber);
    }

    private void jumpToRound(int targetRound) {
        int clampedRound = MathUtils.clamp(targetRound, 1, TOTAL_ROUNDS);
        roundNumber = clampedRound;
        bossesDefeated = getBossesDefeatedBeforeRound(clampedRound);
        coreMaxHealth = getCurrentCoreMaxHealth();
        coreHealth = coreMaxHealth;
        errorFlash = 0f;
        roundMessageTimer = 1.2f;
        runComplete = false;
        intermissionActive = false;
        pendingRoundNumber = -1;
        bullets.clear();
        damageNumbers.clear();
        enemies.clear();
        queuedProcActions.clear();
        explosionEffects.clear();
        chainEffects.clear();
        corePulseEffects.clear();
        rerollsUsedThisShop = 0;
        for (int i = 0; i < nextProcChainTimes.length; i++) {
            nextProcChainTimes[i] = 0f;
        }
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
        for (int i = 0; i < SETTINGS_BUTTON_COUNT; i++) {
            float buttonY = getSettingsButtonY(i);
            if (worldX >= buttonX && worldX <= buttonX + SETTINGS_BUTTON_WIDTH
                && worldY >= buttonY && worldY <= buttonY + SETTINGS_BUTTON_HEIGHT) {
                activateSettingsButton(i);
                return true;
            }
        }

        float sliderX = settingsPanelX + (SETTINGS_PANEL_WIDTH - SETTINGS_SLIDER_WIDTH) * 0.5f;
        for (int i = 0; i < SETTINGS_SLIDER_COUNT; i++) {
            float sliderY = getSettingsSliderTrackY(i) - 12f;
            if (worldX >= sliderX && worldX <= sliderX + SETTINGS_SLIDER_WIDTH
                && worldY >= sliderY && worldY <= sliderY + SETTINGS_BUTTON_HEIGHT) {
                float value = MathUtils.clamp((worldX - sliderX) / SETTINGS_SLIDER_WIDTH, 0f, 1f);
                if (i == 0) {
                    game.setMusicVolume(value);
                } else {
                    game.setSoundVolume(value);
                }
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

        float rerollButtonX = shopPanelX + SHOP_PANEL_WIDTH - buttonWidth * 2f - 40f;
        int rerollCost = getCurrentRerollCost();
        if (worldX >= rerollButtonX && worldX <= rerollButtonX + buttonWidth && worldY >= buttonY && worldY <= buttonY + buttonHeight) {
            if (infiniteGoldEnabled || gold >= rerollCost) {
                if (!infiniteGoldEnabled) {
                    gold -= rerollCost;
                }
                rerollsUsedThisShop++;
                rollShopOffers();
            }
            return true;
        }

        for (int i = 0; i < shopOffers.size; i++) {
            ShopOffer offer = shopOffers.get(i);
            float cardX = getShopCardX(i);
            float cardY = getShopCardY();
            if (worldX >= cardX && worldX <= cardX + SHOP_CARD_WIDTH && worldY >= cardY && worldY <= cardY + SHOP_CARD_HEIGHT) {
                purchaseOffer(offer);
                return true;
            }
        }
        return true;
    }

    private void rollShopOffers() {
        shopOffers.clear();
        Array<ItemDefinition> pool = new Array<ItemDefinition>();
        for (ItemDefinition definition : itemRegistry.getAll()) {
            pool.add(definition);
        }

        while (shopOffers.size < SHOP_OFFER_COUNT && pool.size > 0) {
            boolean spawnMysteryBox = MathUtils.randomBoolean(MYSTERY_BOX_CHANCE);
            if (spawnMysteryBox) {
                ItemDefinition hiddenReward = removeWeightedDefinition(pool);
                if (hiddenReward == null) {
                    break;
                }
                shopOffers.add(ShopOffer.createMystery(hiddenReward, getShopOfferCost(hiddenReward)));
            } else {
                ItemDefinition definition = removeWeightedDefinition(pool);
                if (definition == null) {
                    break;
                }
                shopOffers.add(ShopOffer.createNormal(definition, getShopOfferCost(definition)));
            }
        }
    }

    private void purchaseOffer(ShopOffer offer) {
        if (offer.purchased || (!infiniteGoldEnabled && gold < offer.cost)) {
            return;
        }
        if (!infiniteGoldEnabled) {
            gold -= offer.cost;
        }
        offer.purchased = true;
        itemSystem.addItem(offer.rewardItemId, 1);
        coreMaxHealth = getCurrentCoreMaxHealth();
        if (offer.rewardItemId == ItemId.CORE_PLATING) {
            coreHealth = Math.min(coreMaxHealth, coreHealth + 1);
        } else if (coreHealth > coreMaxHealth) {
            coreHealth = coreMaxHealth;
        }
        dispatchItemEvent(new ItemPurchasedEvent(offer.rewardItemId, offer.cost, itemSystem.getStacks(offer.rewardItemId)));
        SimpleSfx.playPurchase(game.soundVolume);
    }

    private void spawnExplosionEffect(float x, float y, float radius) {
        explosionEffects.add(new ExplosionEffect(new Vector2(x, y), radius, 0.22f));
    }

    private void spawnChainEffect(float fromX, float fromY, float toX, float toY) {
        chainEffects.add(new ChainEffect(fromX, fromY, toX, toY, 0.12f));
    }

    private void spawnCorePulseEffect(float radius) {
        corePulseEffects.add(new CorePulseEffect(radius, 0.22f));
    }

    private ItemDefinition removeWeightedDefinition(Array<ItemDefinition> pool) {
        if (pool.size == 0) {
            return null;
        }
        int totalWeight = 0;
        for (ItemDefinition definition : pool) {
            totalWeight += Math.max(1, definition.shopWeight);
        }
        int roll = MathUtils.random(totalWeight - 1);
        for (int i = 0; i < pool.size; i++) {
            ItemDefinition definition = pool.get(i);
            roll -= Math.max(1, definition.shopWeight);
            if (roll < 0) {
                return pool.removeIndex(i);
            }
        }
        return pool.pop();
    }

    private void damageNearestEnemy(int damage) {
        damageNearestEnemy(damage, null);
    }

    private void damageNearestEnemy(int damage, ItemId sourceItemId) {
        Enemy nearest = findNearestEnemy();
        if (nearest == null) {
            return;
        }
        AttackData attack = sourceItemId == null
            ? createAttack(damage, "direct-nearest", currentItemEventAttack)
            : createChildAttack(damage, "direct-nearest", currentItemEventAttack, sourceItemId);
        float delay = reserveProcDelay(attack);
        Runnable damageAction = () -> applyDirectDamage(nearest, attack);
        if (delay <= 0f) {
            damageAction.run();
        } else {
            queuedProcActions.add(new QueuedProcAction(delay, damageAction));
        }
    }

    private void damageEnemiesNearCore(float radius, int damage) {
        float radiusSquared = radius * radius;
        AttackData attack = createAttack(damage, "direct-core-aoe", currentItemEventAttack);
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.position.dst2(coreX, CORE_Y) <= radiusSquared) {
                applyDirectDamage(enemy, attack);
            }
        }
    }

    private void slowNearestEnemy(float amount, float duration) {
        Enemy nearest = findNearestEnemy();
        if (nearest == null) {
            return;
        }
        applySlow(nearest, amount, duration);
    }

    private void slowEnemiesNearCore(float radius, float amount, float duration) {
        float radiusSquared = radius * radius;
        for (Enemy enemy : enemies) {
            if (enemy.position.dst2(coreX, CORE_Y) <= radiusSquared) {
                applySlow(enemy, amount, duration);
            }
        }
    }

    private void applyDirectDamage(Enemy enemy, AttackData attack) {
        if (!enemies.contains(enemy, true)) {
            return;
        }
        enemy.health -= attack.damage;
        damageNumbers.add(new DamageNumber(new Vector2(enemy.position.x - 8f, enemy.position.y + 34f),
            attack.critical ? "CRIT " + attack.damage : Integer.toString(attack.damage), attack.critical));
        if (enemy.health <= 0) {
            int killGold = getEnemyKillGold(enemy);
            gold += killGold;
            enemies.removeValue(enemy, true);
            dispatchItemEvent(new EnemyKilledEvent(enemy.isBoss, killGold, attack.critical), attack);
        }
    }

    private void applySlow(Enemy enemy, float amount, float duration) {
        enemy.slowMultiplier = Math.min(enemy.slowMultiplier, MathUtils.clamp(1f - amount, 0.1f, 1f));
        enemy.slowTimer = Math.max(enemy.slowTimer, duration);
    }

    private String extractCompletedWord(String line, int currentIndex) {
        int end = Math.max(0, currentIndex - 1);
        int start = end;
        while (start > 0 && !Character.isWhitespace(line.charAt(start - 1))) {
            start--;
        }
        return line.substring(start, end);
    }

    private String getInventorySummary() {
        if (itemSystem.getInventory().isEmpty()) {
            return "none";
        }
        StringBuilder builder = new StringBuilder(64);
        for (InventoryEntry entry : itemSystem.getInventory()) {
            ItemDefinition definition = itemRegistry.get(entry.itemId);
            if (definition == null) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(definition.name).append(" x").append(entry.stacks);
        }
        return builder.toString();
    }

    private String getPassiveSummary() {
        return "dmg+" + itemSystem.getStats().baseDamage
            + ", hp x" + String.format("%.2f", itemSystem.getStats().coreMaxHealthMultiplier)
            + ", kill+" + itemSystem.getStats().enemyKillGoldBonus
            + ", round+" + itemSystem.getStats().roundClearGoldBonus
            + ", boss+" + itemSystem.getStats().bossClearGoldBonus;
    }

    private final class ScreenGameContext implements GameContext {
        @Override
        public io.github.TyPit.listenerTemplate.items.RunStats stats() {
            return itemSystem.getStats();
        }

        @Override
        public AttackData currentAttack() {
            return currentItemEventAttack;
        }

        @Override
        public boolean canCurrentChainTrigger(ItemId itemId) {
            return currentItemEventAttack == null || currentItemEventAttack.canTrigger(itemId);
        }

        @Override
        public int getGold() {
            return gold;
        }

        @Override
        public int getRoundNumber() {
            return roundNumber;
        }

        @Override
        public int getPhaseIndex() {
            return SecondScreen.this.getPhaseIndex();
        }

        @Override
        public int getCompletedWords() {
            return completedWords;
        }

        @Override
        public int getCoreHealth() {
            return coreHealth;
        }

        @Override
        public int getCoreMaxHealth() {
            return coreMaxHealth;
        }

        @Override
        public void fireBonusBulletAtNearest(int damage) {
            fireBonusBulletAtNearest(damage, null);
        }

        @Override
        public void fireBonusBulletAtNearest(int damage, ItemId sourceItemId) {
            AttackData attack = sourceItemId == null
                ? createAttack(damage, "bonus-nearest", currentItemEventAttack)
                : createChildAttack(damage, "bonus-nearest", currentItemEventAttack, sourceItemId);
            float delay = reserveProcDelay(attack);
            Runnable spawnAction = () -> {
                Enemy target = findNearestEnemy();
                if (target == null) {
                    return;
                }
                bullets.add(new Bullet(
                    new Vector2(coreX + MathUtils.random(-14f, 14f), CORE_Y + MathUtils.random(-10f, 10f)), target, attack, true));
            };
            if (delay <= 0f) {
                spawnAction.run();
            } else {
                queuedProcActions.add(new QueuedProcAction(delay, spawnAction));
            }
        }

        @Override
        public void fireBonusBulletAtStrongest(int damage) {
            fireBonusBulletAtStrongest(damage, null);
        }

        @Override
        public void fireBonusBulletAtStrongest(int damage, ItemId sourceItemId) {
            AttackData attack = sourceItemId == null
                ? createAttack(damage, "bonus-strongest", currentItemEventAttack)
                : createChildAttack(damage, "bonus-strongest", currentItemEventAttack, sourceItemId);
            float delay = reserveProcDelay(attack);
            Runnable spawnAction = () -> {
                Enemy target = findStrongestEnemy();
                if (target == null) {
                    return;
                }
                bullets.add(new Bullet(
                    new Vector2(coreX + MathUtils.random(-14f, 14f), CORE_Y + MathUtils.random(-10f, 10f)), target, attack, true));
            };
            if (delay <= 0f) {
                spawnAction.run();
            } else {
                queuedProcActions.add(new QueuedProcAction(delay, spawnAction));
            }
        }

        @Override
        public void damageNearestEnemy(int damage) {
            SecondScreen.this.damageNearestEnemy(damage);
        }

        @Override
        public void damageNearestEnemy(int damage, ItemId sourceItemId) {
            SecondScreen.this.damageNearestEnemy(damage, sourceItemId);
        }

        @Override
        public void damageEnemiesNearCore(float radius, int damage) {
            SecondScreen.this.damageEnemiesNearCore(radius, damage);
        }

        @Override
        public void damageNearbyEnemiesFromCurrentTarget(float radius, int maxTargets, int damage, ItemId sourceItemId) {
            if (currentItemEventTarget == null) {
                return;
            }
            if (sourceItemId != null && currentItemEventAttack != null && !currentItemEventAttack.canTrigger(sourceItemId)) {
                return;
            }
            AttackData chainAttack = createChildAttackWithExactDamage(damage, "chain-near-target", currentItemEventAttack, sourceItemId);
            Array<Enemy> nearbyEnemies = findNearbyEnemies(currentItemEventTarget, radius, maxTargets, true);
            for (int i = 0; i < nearbyEnemies.size; i++) {
                Enemy enemy = nearbyEnemies.get(i);
                float delay = reserveProcDelay(chainAttack);
                Runnable damageAction = () -> applyDirectDamage(enemy, chainAttack);
                if (delay <= 0f) {
                    damageAction.run();
                } else {
                    queuedProcActions.add(new QueuedProcAction(delay, damageAction));
                }
            }
        }

        @Override
        public void damageAreaAroundCurrentTarget(float radius, int damage, ItemId sourceItemId) {
            if (currentItemEventTarget == null) {
                return;
            }
            if (sourceItemId != null && currentItemEventAttack != null && !currentItemEventAttack.canTrigger(sourceItemId)) {
                return;
            }
            AttackData blastAttack = createChildAttackWithExactDamage(damage, "area-current-target", currentItemEventAttack, sourceItemId);
            Array<Enemy> affectedEnemies = findNearbyEnemies(currentItemEventTarget, radius, Integer.MAX_VALUE, false);
            for (int i = 0; i < affectedEnemies.size; i++) {
                Enemy enemy = affectedEnemies.get(i);
                float delay = reserveProcDelay(blastAttack);
                Runnable damageAction = () -> applyDirectDamage(enemy, blastAttack);
                if (delay <= 0f) {
                    damageAction.run();
                } else {
                    queuedProcActions.add(new QueuedProcAction(delay, damageAction));
                }
            }
        }

        @Override
        public void slowNearestEnemy(float amount, float duration) {
            SecondScreen.this.slowNearestEnemy(amount, duration);
        }

        @Override
        public void slowEnemiesNearCore(float radius, float amount, float duration) {
            SecondScreen.this.slowEnemiesNearCore(radius, amount, duration);
        }

        @Override
        public void grantGold(int amount) {
            gold += amount;
        }

        @Override
        public void healCore(int amount) {
            coreHealth = Math.min(coreMaxHealth, coreHealth + amount);
        }

        @Override
        public void spawnExplosionEffectAroundCurrentTarget(float radius) {
            if (currentItemEventTarget != null) {
                SecondScreen.this.spawnExplosionEffect(currentItemEventTarget.position.x, currentItemEventTarget.position.y, radius);
            }
        }

        @Override
        public void spawnChainEffectFromCurrentTargetToNearest(float radius) {
            if (currentItemEventTarget == null) {
                return;
            }
            Array<Enemy> nearbyEnemies = findNearbyEnemies(currentItemEventTarget, radius, 1, true);
            if (nearbyEnemies.size == 0) {
                return;
            }
            Enemy target = nearbyEnemies.first();
            SecondScreen.this.spawnChainEffect(currentItemEventTarget.position.x, currentItemEventTarget.position.y, target.position.x, target.position.y);
        }

        @Override
        public void spawnCorePulseEffect(float radius) {
            SecondScreen.this.spawnCorePulseEffect(radius);
        }

        @Override
        public void playShotSound() {
            SimpleSfx.playShot(game.soundVolume);
        }

        @Override
        public void playExplosionSound() {
            SimpleSfx.playExplosion(game.soundVolume);
        }

        @Override
        public void playZapSound() {
            SimpleSfx.playZap(game.soundVolume);
        }

        @Override
        public void playCoreHitSound() {
            SimpleSfx.playCoreHit(game.soundVolume);
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

    private void updateMusicForCurrentState() {
        if (intermissionActive) {
            game.playMusic(Drop.MUSIC_SHOP);
        } else if (bossRound) {
            game.playMusic(Drop.MUSIC_BOSS);
        } else {
            game.playMusic(Drop.MUSIC_GAMEPLAY);
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

    private boolean isInsideDebugPanelHeader(float worldX, float worldY) {
        if (!game.debugMenuUnlocked || coreHealth <= 0) {
            return false;
        }
        float panelHeight = getDebugPanelHeight();
        float headerHeight = 30f;
        float headerTop = debugPanelY + panelHeight;
        float headerBottom = headerTop - headerHeight;
        return worldX >= debugPanelX && worldX <= debugPanelX + DEBUG_PANEL_WIDTH
            && worldY >= headerBottom && worldY <= headerTop;
    }

    private boolean handleDebugPanelClick(float worldX, float worldY) {
        if (!game.debugMenuUnlocked || coreHealth <= 0) {
            return false;
        }
        float panelHeight = getDebugPanelHeight();
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
        if (isInsideDebugToggle(worldX, worldY, 6)) {
            infiniteGoldEnabled = !infiniteGoldEnabled;
            return true;
        }
        if (isInsideDebugToggle(worldX, worldY, 7)) {
            itemMenuEnabled = !itemMenuEnabled;
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
            if (MathUtils.random() < MathUtils.clamp(itemSystem.getStats().wordReplaceWithAChance, 0f, 1f)) {
                token = "a";
            }
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

    private boolean handleItemMenuClick(float worldX, float worldY) {
        if (!itemMenuEnabled || !game.debugMenuUnlocked || coreHealth <= 0) {
            return false;
        }
        float menuWidth = 860f;
        float menuHeight = 560f;
        float menuX = coreX - menuWidth * 0.5f;
        float menuY = CORE_Y - menuHeight * 0.5f;
        int index = 0;
        float cardWidth = 190f;
        float cardHeight = 100f;
        float padding = 16f;
        float startX = menuX + 26f;
        float startY = menuY + menuHeight - 64f;

        for (ItemDefinition def : itemRegistry.getAll()) {
            int col = index % 4;
            int row = index / 4;
            float cardX = startX + col * (cardWidth + padding);
            float cardY = startY - cardHeight - row * (cardHeight + padding);
            float btnWidth = 70f;
            float btnHeight = 28f;
            float btnY = cardY + 8f;
            float plusX = cardX + 8f;
            float minusX = cardX + cardWidth - btnWidth - 8f;

            if (worldX >= plusX && worldX <= plusX + btnWidth && worldY >= btnY && worldY <= btnY + btnHeight) {
                itemSystem.addItem(def.id, 1);
                coreMaxHealth = getCurrentCoreMaxHealth();
                coreHealth = def.id == ItemId.CORE_PLATING ? Math.min(coreMaxHealth, coreHealth + 1) : Math.min(coreMaxHealth, coreHealth);
                return true;
            }
            if (worldX >= minusX && worldX <= minusX + btnWidth && worldY >= btnY && worldY <= btnY + btnHeight) {
                if (itemSystem.getStacks(def.id) > 0) {
                    itemSystem.removeItem(def.id, 1);
                    coreMaxHealth = getCurrentCoreMaxHealth();
                    coreHealth = Math.min(coreMaxHealth, coreHealth);
                }
                return true;
            }
            index++;
        }
        if (worldX >= menuX && worldX <= menuX + menuWidth && worldY >= menuY && worldY <= menuY + menuHeight) {
            return true;
        }
        return false;
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
                if (MathUtils.random() < MathUtils.clamp(itemSystem.getStats().mistypeForgiveChance, 0f, 1f)) {
                    advanceChar();
                    return true;
                }
                errorFlash = ERROR_FLASH_DURATION;
                dispatchItemEvent(new CharacterTypedWrongEvent(character, expected, completedWords, currentCharIndex));
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

            if (isInsideDebugPanelHeader(tempTouch.x, tempTouch.y)) {
                isDraggingDebugPanel = true;
                debugPanelDragOffsetX = tempTouch.x - debugPanelX;
                debugPanelDragOffsetY = tempTouch.y - debugPanelY;
                return true;
            }

            if (handleSettingsClick(tempTouch.x, tempTouch.y)) {
                return true;
            }
            if (handleItemMenuClick(tempTouch.x, tempTouch.y)) {
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

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (isDraggingDebugPanel) {
                game.viewport.unproject(tempTouch.set(screenX, screenY, 0f));
                debugPanelX = tempTouch.x - debugPanelDragOffsetX;
                debugPanelY = tempTouch.y - debugPanelDragOffsetY;

                // Clamp to screen bounds
                debugPanelX = Math.max(0f, Math.min(debugPanelX, game.viewport.getWorldWidth() - DEBUG_PANEL_WIDTH));
                debugPanelY = Math.max(0f, Math.min(debugPanelY, game.viewport.getWorldHeight() - getDebugPanelHeight()));
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (isDraggingDebugPanel) {
                isDraggingDebugPanel = false;
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
        shopPanelX = coreX - SHOP_PANEL_WIDTH * 0.5f;
        shopPanelY = CORE_Y - 280f;
        settingsPanelX = coreX - SETTINGS_PANEL_WIDTH * 0.5f;
        settingsPanelY = CORE_Y - getSettingsPanelHeight() * 0.5f;
        textWrapWidth = BOX_WIDTH - PADDING * 2f;
        debugPanelX = game.viewport.getWorldWidth() - DEBUG_PANEL_WIDTH - 24f;
        debugPanelY = game.viewport.getWorldHeight() - getDebugPanelHeight() - 24f;
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
            "The", "quick", "brown", "fox.", "Jumps", "over", "a", "lazy", "river.",
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
        private float slowMultiplier = 1f;
        private float slowTimer;

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
        private final AttackData attack;
        private final boolean bonusShot;

        private Bullet(Vector2 position, Enemy target, AttackData attack, boolean bonusShot) {
            this.position = position;
            this.target = target;
            this.attack = attack;
            this.bonusShot = bonusShot;
        }
    }

    private static final class DamageNumber {
        private final Vector2 position;
        private final String text;
        private final boolean critical;
        private float remaining = DAMAGE_NUMBER_LIFETIME;

        private DamageNumber(Vector2 position, String text, boolean critical) {
            this.position = position;
            this.text = text;
            this.critical = critical;
        }
    }

    private static final class QueuedProcAction {
        private float remainingDelay;
        private final Runnable runnable;

        private QueuedProcAction(float remainingDelay, Runnable runnable) {
            this.remainingDelay = remainingDelay;
            this.runnable = runnable;
        }
    }

    private static final class ExplosionEffect {
        private final Vector2 position;
        private final float radius;
        private final float duration;
        private float remaining;

        private ExplosionEffect(Vector2 position, float radius, float duration) {
            this.position = position;
            this.radius = radius;
            this.duration = duration;
            this.remaining = duration;
        }
    }

    private static final class ChainEffect {
        private final float fromX;
        private final float fromY;
        private final float toX;
        private final float toY;
        private final float duration;
        private float remaining;

        private ChainEffect(float fromX, float fromY, float toX, float toY, float duration) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.duration = duration;
            this.remaining = duration;
        }
    }

    private static final class CorePulseEffect {
        private final float radius;
        private final float duration;
        private float remaining;

        private CorePulseEffect(float radius, float duration) {
            this.radius = radius;
            this.duration = duration;
            this.remaining = duration;
        }
    }

    private static final class ShopOffer {
        private final ItemId rewardItemId;
        private final String name;
        private final String description;
        private final int cost;
        private final boolean isMystery;
        private final ItemRarity rarity;
        private boolean purchased;

        private ShopOffer(ItemId rewardItemId, String name, String description, int cost, boolean isMystery, ItemRarity rarity) {
            this.rewardItemId = rewardItemId;
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.isMystery = isMystery;
            this.rarity = rarity;
            this.purchased = false;
        }

        private static ShopOffer createNormal(ItemDefinition definition, int cost) {
            return new ShopOffer(definition.id, definition.name, definition.description, cost, false, definition.rarity);
        }

        private static ShopOffer createMystery(ItemDefinition rewardDefinition, int cost) {
            String description = "Unwrap a hidden item rolled from the normal shop pool. Rarity odds follow the same weighted rules as visible offers.";
            return new ShopOffer(rewardDefinition.id, "Mystery Box", description, cost, true, rewardDefinition.rarity);
        }
    }
}
