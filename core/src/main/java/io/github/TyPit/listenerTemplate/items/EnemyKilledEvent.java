package io.github.TyPit.listenerTemplate.items;

public final class EnemyKilledEvent implements ItemEvent {
    public final boolean boss;
    public final int goldAwarded;
    public final boolean critical;

    public EnemyKilledEvent(boolean boss, int goldAwarded, boolean critical) {
        this.boss = boss;
        this.goldAwarded = goldAwarded;
        this.critical = critical;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.ENEMY_KILLED;
    }
}
