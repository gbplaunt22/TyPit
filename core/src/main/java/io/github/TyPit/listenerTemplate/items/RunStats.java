package io.github.TyPit.listenerTemplate.items;

public final class RunStats {
    public int baseDamage;
    public float coreMaxHealthMultiplier;
    public int enemyKillGoldBonus;
    public int roundClearGoldBonus;
    public int bossClearGoldBonus;
    public float bonusBulletChance;
    public float critChance;
    public float streakProcChance;
    public float wordReplaceWithAChance;
    public float mistypeForgiveChance;
    public boolean targetStrongestEnemy;
    public boolean bulletsPierce;

    public void reset() {
        baseDamage = 0;
        coreMaxHealthMultiplier = 1f;
        enemyKillGoldBonus = 0;
        roundClearGoldBonus = 0;
        bossClearGoldBonus = 0;
        bonusBulletChance = 0f;
        critChance = 0f;
        streakProcChance = 0f;
        wordReplaceWithAChance = 0f;
        mistypeForgiveChance = 0f;
        targetStrongestEnemy = false;
        bulletsPierce = false;
    }
}
