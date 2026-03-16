package io.github.TyPit.listenerTemplate.items;

public interface GameContext {
    RunStats stats();
    AttackData currentAttack();

    int getGold();
    int getRoundNumber();
    int getPhaseIndex();
    int getCompletedWords();
    int getCoreHealth();
    int getCoreMaxHealth();

    boolean canCurrentChainTrigger(ItemId itemId);
    void fireBonusBulletAtNearest(int damage);
    void fireBonusBulletAtNearest(int damage, ItemId sourceItemId);
    void fireBonusBulletAtStrongest(int damage);
    void fireBonusBulletAtStrongest(int damage, ItemId sourceItemId);
    void damageNearestEnemy(int damage);
    void damageNearestEnemy(int damage, ItemId sourceItemId);
    void damageEnemiesNearCore(float radius, int damage);
    void damageNearbyEnemiesFromCurrentTarget(float radius, int maxTargets, int damage, ItemId sourceItemId);
    void damageAreaAroundCurrentTarget(float radius, int damage, ItemId sourceItemId);
    void slowNearestEnemy(float amount, float duration);
    void slowEnemiesNearCore(float radius, float amount, float duration);
    void grantGold(int amount);
    void healCore(int amount);
    void spawnExplosionEffectAroundCurrentTarget(float radius);
    void spawnChainEffectFromCurrentTargetToNearest(float radius);
    void spawnCorePulseEffect(float radius);
    void playShotSound();
    void playExplosionSound();
    void playZapSound();
    void playCoreHitSound();
}
