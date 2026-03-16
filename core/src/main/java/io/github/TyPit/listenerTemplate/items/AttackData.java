package io.github.TyPit.listenerTemplate.items;

import java.util.EnumSet;

public final class AttackData {
    public final int rootAttackId;
    public final int chainDepth;
    public final int baseDamage;
    public final int damage;
    public final boolean critical;
    public final float critMultiplier;
    public final String source;
    private final EnumSet<ItemId> triggeredItems;

    public AttackData(int rootAttackId, int chainDepth, int baseDamage, int damage, boolean critical, float critMultiplier,
        String source, EnumSet<ItemId> triggeredItems) {
        this.rootAttackId = rootAttackId;
        this.chainDepth = chainDepth;
        this.baseDamage = baseDamage;
        this.damage = damage;
        this.critical = critical;
        this.critMultiplier = critMultiplier;
        this.source = source;
        this.triggeredItems = triggeredItems.clone();
    }

    public AttackData withSource(String nextSource) {
        return new AttackData(rootAttackId, chainDepth, baseDamage, damage, critical, critMultiplier, nextSource, triggeredItems);
    }

    public boolean canTrigger(ItemId itemId) {
        return !triggeredItems.contains(itemId);
    }

    public AttackData createChild(int nextBaseDamage, String nextSource, ItemId sourceItemId) {
        EnumSet<ItemId> nextTriggeredItems = triggeredItems.clone();
        if (sourceItemId != null) {
            nextTriggeredItems.add(sourceItemId);
        }
        return new AttackData(rootAttackId, chainDepth + 1, nextBaseDamage, nextBaseDamage, critical, critMultiplier, nextSource, nextTriggeredItems);
    }

    public EnumSet<ItemId> copyTriggeredItems() {
        return triggeredItems.clone();
    }
}
