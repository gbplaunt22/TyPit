package io.github.TyPit.listenerTemplate.items;

public final class BulletHitEnemyEvent implements ItemEvent {
    public final int damage;
    public final boolean killed;
    public final boolean bossTarget;
    public final boolean critical;

    public BulletHitEnemyEvent(int damage, boolean killed, boolean bossTarget, boolean critical) {
        this.damage = damage;
        this.killed = killed;
        this.bossTarget = bossTarget;
        this.critical = critical;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.BULLET_HIT_ENEMY;
    }
}
