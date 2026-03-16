# TyPit Item API

This file documents the current item framework surface for TyPit.

It is intentionally practical:
- what item definitions can contain
- what hooks currently exist
- what data each hook provides
- what item code can currently do through `GameContext`
- what passive stats exist right now
- what is not available yet

This is the current design surface for creating real items.

## Core Model

### `ItemId`
Current item IDs:
- `SHARPENED_ROUNDS`
- `CORE_PLATING`
- `BOUNTY_LEDGER`
- `ROUND_DIVIDEND`
- `BOSS_CONTRACT`
- `ECHO_MAGAZINE`
- `CHAIN_INK`

Add new IDs here before creating new items.

### `ItemRarity`
Current rarities:
- `COMMON`
- `UNCOMMON`
- `RARE`
- `LEGENDARY`

### `ItemTag`
Current taxonomy tags:
- `TYPING_OUTPUT`
- `KILL_EFFECT`
- `STREAK_RHYTHM`
- `DEFENSIVE_CORE`
- `ECONOMY`
- `UTILITY_CONTROL`
- `META_SCALING`

### `ItemDefinition`
Each item definition currently contains:
- `id`
- `name`
- `description`
- `rarity`
- `tags`
- `maxStacks`
- `shopCost`
- `shopWeight`
- `effect`

Definitions live in:
- [`ItemRegistry.java`](D:/Desktop/TyPit2/core/src/main/java/io/github/TyPit/listenerTemplate/items/ItemRegistry.java)

## Runtime Effect Interface

### `ItemEffect`
Every item effect can use two entry points:

```java
default void applyPassives(int stacks, RunStats stats)
default void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context)
```

Use `applyPassives(...)` for always-on bonuses.

Use `onEvent(...)` for triggered behavior.

### `ItemRuntimeState`
Each owned item gets mutable runtime state through:
- integer values
- float values
- boolean values

This is where counters, streak tracking, cooldown flags, and internal proc state should live.

Examples:
- every 5th word
- no-miss streak count
- once-per-round trigger used
- current charge amount

## Passive Stats

Current passive stat snapshot:

```java
public int baseDamage;
public int coreMaxHealthBonus;
public int enemyKillGoldBonus;
public int roundClearGoldBonus;
public int bossClearGoldBonus;
public float bonusBulletChance;
public float critChance;
public float streakProcChance;
public boolean targetStrongestEnemy;
public boolean bulletsPierce;
```

Important note:
- only the first five are actively used by gameplay right now
- the float/boolean fields exist but are not yet wired into the combat loop

Currently active passives:
- `baseDamage`
- `coreMaxHealthBonus`
- `enemyKillGoldBonus`
- `roundClearGoldBonus`
- `bossClearGoldBonus`

Reserved but not yet integrated:
- `bonusBulletChance`
- `critChance`
- `streakProcChance`
- `targetStrongestEnemy`
- `bulletsPierce`

## Event Hooks

All events implement:

```java
public interface ItemEvent {
    ItemEventType type();
}
```

### `CharacterTypedCorrectEvent`
Fires when the player types the correct next character.

Fields:
- `character`
- `completedWords`
- `currentCharIndex`

Use cases:
- every typed letter
- specific typed characters
- charge-on-correct-input items
- streak systems built from correct input

### `CharacterTypedWrongEvent`
Fires when the player types an incorrect character.

Fields:
- `typedCharacter`
- `expectedCharacter`
- `completedWords`
- `currentCharIndex`

Use cases:
- mistake punishment
- mistake-trigger utility
- streak break logic
- typo-based defensive pulses

### `WordCompletedEvent`
Fires when a whitespace-delimited word is completed.

Fields:
- `word`
- `completedWords`
- `noMissSinceLastWord`

Important note:
- `word` is the actual completed token, including punctuation/casing exactly as typed
- this is not the whole line

Use cases:
- every completed word
- every Nth word
- word-family checks
- no-miss word chains

### `BulletHitEnemyEvent`
Fires when a bullet actually hits an enemy.

Fields:
- `damage`
- `killed`
- `bossTarget`

Use cases:
- on-hit effects
- boss-hit effects
- hit-based procs
- “if hit kills” branching

Important note:
- this is currently bullet-hit only
- non-bullet direct damage does not fire this event

### `EnemyKilledEvent`
Fires when an enemy dies.

Fields:
- `boss`
- `goldAwarded`

Use cases:
- kill explosions
- chain effects
- economy on kill
- boss-vs-normal kill branching

### `CoreDamagedEvent`
Fires when an enemy reaches the core and deals damage.

Fields:
- `damageTaken`
- `remainingHealth`

Use cases:
- emergency defense items
- reactive slows
- retaliation pulses
- “when low health” logic

### `RoundStartedEvent`
Fires when a round begins.

Fields:
- `roundNumber`
- `phaseIndex`
- `bossRound`

Use cases:
- per-round reset logic
- once-per-round charge/refill
- scaling setup
- boss-round special handling

### `RoundClearedEvent`
Fires when a normal or boss round clears before intermission.

Fields:
- `roundNumber`
- `phaseIndex`
- `goldAwarded`

Use cases:
- round clear rewards
- build-up items
- “next round” preparation

### `BossClearedEvent`
Fires when a boss round clears.

Fields:
- `roundNumber`
- `phaseIndex`
- `goldAwarded`

Use cases:
- boss reward scaling
- phase-transition items
- major milestone effects

### `ItemPurchasedEvent`
Fires when the player buys an item from the shop.

Fields:
- `itemId`
- `cost`
- `stacksAfterPurchase`

Use cases:
- shop economy effects
- purchase-triggered scaling
- “first item each shop” style logic later

## GameContext

This is the safe bridge layer item code can use to affect gameplay.

### Query Methods
Current read access:
- `stats()`
- `getGold()`
- `getRoundNumber()`
- `getPhaseIndex()`
- `getCompletedWords()`
- `getCoreHealth()`
- `getCoreMaxHealth()`

### Action Methods
Current write/action access:
- `fireBonusBulletAtNearest(int damage)`
- `fireBonusBulletAtStrongest(int damage)`
- `damageNearestEnemy(int damage)`
- `damageEnemiesNearCore(float radius, int damage)`
- `slowNearestEnemy(float amount, float duration)`
- `slowEnemiesNearCore(float radius, float amount, float duration)`
- `grantGold(int amount)`
- `healCore(int amount)`

## What These Actions Actually Mean

### `fireBonusBulletAtNearest(int damage)`
- spawns a bullet from the core
- targets the nearest enemy
- uses the current projectile system

### `fireBonusBulletAtStrongest(int damage)`
- spawns a bullet from the core
- targets the living enemy with the highest health
- ties break by nearest to core

### `damageNearestEnemy(int damage)`
- directly damages the nearest enemy
- not a bullet
- can kill enemies
- killed enemies still award gold and fire `EnemyKilledEvent`

### `damageEnemiesNearCore(float radius, int damage)`
- directly damages every enemy within `radius` of the core
- useful for defensive pulse items

### `slowNearestEnemy(float amount, float duration)`
- slows the nearest enemy
- `amount` is interpreted as a percentage-like reduction
- internally this becomes a movement multiplier clamp

### `slowEnemiesNearCore(float radius, float amount, float duration)`
- slows all enemies within `radius` of the core

### `grantGold(int amount)`
- directly adds gold to the run

### `healCore(int amount)`
- heals core health
- cannot exceed current max core health

## Current Taxonomy Coverage

### Typing Output
Supported well by:
- `CharacterTypedCorrectEvent`
- `CharacterTypedWrongEvent`
- `WordCompletedEvent`

### Kill Effect
Supported well by:
- `BulletHitEnemyEvent`
- `EnemyKilledEvent`

### Streak / Rhythm
Supported through:
- `CharacterTypedCorrectEvent`
- `CharacterTypedWrongEvent`
- `WordCompletedEvent`
- `ItemRuntimeState`

No dedicated streak event exists yet.

### Defensive / Core
Supported well by:
- `CoreDamagedEvent`
- `RoundStartedEvent`
- `healCore(...)`
- `damageEnemiesNearCore(...)`
- `slowEnemiesNearCore(...)`

### Economy
Supported by:
- `EnemyKilledEvent`
- `RoundClearedEvent`
- `BossClearedEvent`
- `ItemPurchasedEvent`
- `grantGold(...)`

### Utility / Control
Supported by:
- targeting helpers
- slow helpers
- nearest/core-area damage helpers

### Meta Scaling / Conversion
Partially supported by:
- round/boss/purchase events
- passive stats
- `ItemRuntimeState`

This category is possible, but likely to want more hooks later.

## What Is Not Available Yet

These do not currently exist:
- `EnemySpawnedEvent`
- `BulletFiredEvent`
- `ShopEnteredEvent`
- `GoldEarnedEvent`
- `GoldSpentEvent`
- explicit streak-changed events
- explicit enemy reference objects in event payloads
- direct access to enemy positions from item code
- generic AoE anywhere on screen
- projectile pierce/retarget APIs
- status effect systems beyond the current simple slow

These are the main limitations on item creativity right now.

## Design Guidance

Right now, the framework is best suited for:
- basic passives
- every-N typing triggers
- on-kill effects
- on-hit effects
- defensive core reactions
- economy bonuses
- simple control effects

If an item idea needs:
- exact enemy targeting beyond nearest/strongest
- arbitrary world-position explosions
- projectile mutation rules
- deep streak state shared across items
- gold-spend triggers

then the framework likely needs another API expansion first.

## Suggested Design Workflow

When inventing a new item:

1. Decide whether it is passive or triggered.
2. Identify the hook it reacts to.
3. Identify any persistent state it needs.
4. Identify the `GameContext` action it needs.
5. If no current hook/action fits, note that as a framework gap instead of hacking around it.

That is the intended purpose of this file.
