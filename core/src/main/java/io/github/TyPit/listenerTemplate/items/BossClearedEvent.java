package io.github.TyPit.listenerTemplate.items;

public final class BossClearedEvent implements ItemEvent {
    public final int roundNumber;
    public final int phaseIndex;
    public final int goldAwarded;

    public BossClearedEvent(int roundNumber, int phaseIndex, int goldAwarded) {
        this.roundNumber = roundNumber;
        this.phaseIndex = phaseIndex;
        this.goldAwarded = goldAwarded;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.BOSS_CLEARED;
    }
}
