package io.github.TyPit.listenerTemplate.items;

import com.badlogic.gdx.math.MathUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public final class ItemRegistry {
    private static final int UNLIMITED_STACKS = Integer.MAX_VALUE;
    private final Map<ItemId, ItemDefinition> definitions;

    public ItemRegistry() {
        EnumMap<ItemId, ItemDefinition> defs = new EnumMap<ItemId, ItemDefinition>(ItemId.class);

        register(defs, new ItemDefinition(
            ItemId.SILVER_CHAIN_LINK,
            "Silver Chain Link",
            "25% chance on hit to arc 100% total damage to up to 3 nearby enemies; +2 targets per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            135,
            65,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof BulletHitEnemyEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.SILVER_CHAIN_LINK)) {
                        return;
                    }
                    if (MathUtils.random() >= 0.60f) {
                        return;
                    }

                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.damage : 1;
                    int maxTargets = 10 + Math.max(0, stacks - 1) * 4;
                    context.damageNearbyEnemiesFromCurrentTarget(420f, maxTargets, inheritedDamage, ItemId.SILVER_CHAIN_LINK);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.TENNISON_GAMBIT,
            "Tennison Gambit",
            "10% chance on hit to fire a missile for 300% total damage in an area; +300% total damage per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            130,
            60,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof BulletHitEnemyEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.TENNISON_GAMBIT)) {
                        return;
                    }
                    if (MathUtils.random() >= 0.10f) {
                        return;
                    }

                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.damage : 1;
                    int missileDamage = Math.max(1, inheritedDamage * (6 * Math.max(1, stacks)));
                    context.spawnExplosionEffectAroundCurrentTarget(420f);
                    context.playExplosionSound();
                    context.damageAreaAroundCurrentTarget(420f, missileDamage, ItemId.TENNISON_GAMBIT);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.BLAST_SCRIPT,
            "Blast Script",
            "12% chance on hit to explode for 150% total damage in an area; +150% total damage per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            120,
            62,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof BulletHitEnemyEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.BLAST_SCRIPT)) {
                        return;
                    }
                    if (MathUtils.random() >= 0.40f) {
                        return;
                    }
                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.damage : 1;
                    int explosionDamage = Math.max(1, inheritedDamage * (5 * Math.max(1, stacks)));
                    context.spawnExplosionEffectAroundCurrentTarget(380f);
                    context.playExplosionSound();
                    context.damageAreaAroundCurrentTarget(380f, explosionDamage, ItemId.BLAST_SCRIPT);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.SHOOTING_GLASSES,
            "Shooting Glasses",
            "Attacks have a 10% chance per stack to critically strike for double damage",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL),
            UNLIMITED_STACKS,
            95,
            100,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.critChance += 0.10f * stacks;
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.SHARPENED_ROUNDS,
            "Sharpened Rounds",
            "+1 base damage",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT),
            UNLIMITED_STACKS,
            100,
            100,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.baseDamage += stacks;
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.CORE_PLATING,
            "Core Plating",
            "Increase max core health by 1.5x",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.DEFENSIVE_CORE),
            UNLIMITED_STACKS,
            90,
            90,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.coreMaxHealthMultiplier *= (float)Math.pow(1.5, stacks);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.MONOTYPE_RIBBON,
            "Monotype Ribbon",
            "Replaces 10% of words with \"a\"; +8% per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL),
            UNLIMITED_STACKS,
            115,
            58,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.wordReplaceWithAChance += 0.10f + Math.max(0, stacks - 1) * 0.08f;
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.SLIPSTREAM,
            "Slipstream",
            "Incorrect typed characters have a 10% chance to count as correct; +8% chance per stack",
            ItemRarity.RARE,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            145,
            48,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.mistypeForgiveChance += 0.10f + Math.max(0, stacks - 1) * 0.08f;
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.BOUNTY_LEDGER,
            "Bounty Ledger",
            "+1 gold from every kill",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.ECONOMY),
            UNLIMITED_STACKS,
            120,
            80,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.enemyKillGoldBonus += stacks;
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.ROUND_DIVIDEND,
            "Round Dividend",
            "+10 round clear gold",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.ECONOMY),
            UNLIMITED_STACKS,
            110,
            75,
            new ItemEffect() {
                @Override
                public void applyPassives(int stacks, RunStats stats) {
                    stats.roundClearGoldBonus += stacks * 10;
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.MAFIA_CONTRACT,
            "Mafia Contract",
            "20% chance per stack on kill to award 1 gold",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.KILL_EFFECT, ItemTag.ECONOMY, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            140,
            55,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof EnemyKilledEvent)) {
                        return;
                    }
                    if (MathUtils.random() < 0.20f * stacks) {
                        context.grantGold(1);
                    }
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.CARBON_COPY,
            "Carbon Copy",
            "15% chance per stack on completed word to fire a bonus bullet at the nearest enemy",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL),
            UNLIMITED_STACKS,
            100,
            100,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof WordCompletedEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.CARBON_COPY)) {
                        return;
                    }
                    if (MathUtils.random() < 0.45f * stacks) {
                        AttackData attack = context.currentAttack();
                        int inheritedDamage = attack != null ? attack.baseDamage : Math.max(1, context.stats().baseDamage + 1);
                        context.fireBonusBulletAtNearest(inheritedDamage, ItemId.CARBON_COPY);
                    }
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.MARGIN_NOTES,
            "Margin Notes",
            "12% chance per stack on completed word to fire a bonus bullet at the strongest enemy",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL),
            UNLIMITED_STACKS,
            100,
            88,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof WordCompletedEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.MARGIN_NOTES)) {
                        return;
                    }
                    if (MathUtils.random() < 0.40f * stacks) {
                        AttackData attack = context.currentAttack();
                        int inheritedDamage = attack != null ? attack.baseDamage : Math.max(1, context.stats().baseDamage + 1);
                        context.fireBonusBulletAtStrongest(inheritedDamage, ItemId.MARGIN_NOTES);
                    }
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.OVERFLOW_SCRIPT,
            "Overflow Script",
            "8% chance on completed word to fire 3 bonus bullets; +1 bullet per stack",
            ItemRarity.RARE,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            150,
            44,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof WordCompletedEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.OVERFLOW_SCRIPT)) {
                        return;
                    }
                    if (MathUtils.random() >= 0.28f) {
                        return;
                    }
                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.baseDamage : Math.max(1, context.stats().baseDamage + 1);
                    int bulletCount = 10 + Math.max(0, stacks - 1) * 4;
                    for (int i = 0; i < bulletCount; i++) {
                        context.fireBonusBulletAtNearest(inheritedDamage, ItemId.OVERFLOW_SCRIPT);
                    }
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.FINAL_DRAFT,
            "Final Draft",
            "Kills zap the nearest enemy for 1 damage per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.KILL_EFFECT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            125,
            65,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof EnemyKilledEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.FINAL_DRAFT)) {
                        return;
                    }
                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.damage : 1;
                    int zapDamage = Math.max(1, inheritedDamage * 2);
                    context.spawnChainEffectFromCurrentTargetToNearest(360f);
                    context.playZapSound();
                    context.damageNearestEnemy(zapDamage, ItemId.FINAL_DRAFT);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.DEAD_LETTER_OFFICE,
            "Dead Letter Office",
            "20% chance on kill to explode for 100% total damage in an area; +100% total damage per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.KILL_EFFECT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            128,
            58,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof EnemyKilledEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.DEAD_LETTER_OFFICE)) {
                        return;
                    }
                    if (MathUtils.random() >= 0.60f) {
                        return;
                    }
                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.damage : 1;
                    int explosionDamage = Math.max(1, inheritedDamage * (3 * Math.max(1, stacks)));
                    context.spawnExplosionEffectAroundCurrentTarget(420f);
                    context.playExplosionSound();
                    context.damageAreaAroundCurrentTarget(420f, explosionDamage, ItemId.DEAD_LETTER_OFFICE);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.STATIC_MARGIN,
            "Static Margin",
            "18% chance on hit to zap the nearest enemy for 50% total damage; +50% per stack",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.UTILITY_CONTROL, ItemTag.META_SCALING),
            UNLIMITED_STACKS,
            122,
            57,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof BulletHitEnemyEvent)) {
                        return;
                    }
                    if (!context.canCurrentChainTrigger(ItemId.STATIC_MARGIN)) {
                        return;
                    }
                    if (MathUtils.random() >= 0.45f) {
                        return;
                    }
                    AttackData attack = context.currentAttack();
                    int inheritedDamage = attack != null ? attack.damage : 1;
                    int zapDamage = Math.max(1, inheritedDamage * (2 * Math.max(1, stacks)));
                    context.spawnChainEffectFromCurrentTargetToNearest(360f);
                    context.playZapSound();
                    context.damageNearestEnemy(zapDamage, ItemId.STATIC_MARGIN);
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.PANIC_SHIELD,
            "Panic Shield",
            "15% chance per stack on mistype to slow the nearest enemy by 50% for 1.5s",
            ItemRarity.UNCOMMON,
            EnumSet.of(ItemTag.TYPING_OUTPUT, ItemTag.DEFENSIVE_CORE, ItemTag.UTILITY_CONTROL),
            UNLIMITED_STACKS,
            118,
            54,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof CharacterTypedWrongEvent)) {
                        return;
                    }
                    if (MathUtils.random() < 0.35f * stacks) {
                        context.spawnCorePulseEffect(120f);
                        context.playZapSound();
                        context.slowNearestEnemy(0.75f, 2.5f);
                    }
                }
            }
        ));

        register(defs, new ItemDefinition(
            ItemId.EMERGENCY_PULSE,
            "Emergency Pulse",
            "When the core is hit, deal 2 damage near the core; +2 damage per stack",
            ItemRarity.COMMON,
            EnumSet.of(ItemTag.DEFENSIVE_CORE, ItemTag.UTILITY_CONTROL),
            UNLIMITED_STACKS,
            95,
            72,
            new ItemEffect() {
                @Override
                public void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
                    if (!(event instanceof CoreDamagedEvent)) {
                        return;
                    }
                    int pulseDamage = 40 * Math.max(1, stacks);
                    context.spawnCorePulseEffect(420f);
                    context.playCoreHitSound();
                    context.damageEnemiesNearCore(420f, pulseDamage);
                }
            }
        ));

        definitions = Collections.unmodifiableMap(defs);
    }

    public ItemDefinition get(ItemId id) {
        return definitions.get(id);
    }

    public Collection<ItemDefinition> getAll() {
        return definitions.values();
    }

    private static void register(Map<ItemId, ItemDefinition> defs, ItemDefinition definition) {
        defs.put(definition.id, definition);
    }
}
