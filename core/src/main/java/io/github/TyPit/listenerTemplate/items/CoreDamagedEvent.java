package io.github.TyPit.listenerTemplate.items;

public final class CoreDamagedEvent implements ItemEvent {
    public final int damageTaken;
    public final int remainingHealth;

    public CoreDamagedEvent(int damageTaken, int remainingHealth) {
        this.damageTaken = damageTaken;
        this.remainingHealth = remainingHealth;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.CORE_DAMAGED;
    }
}
