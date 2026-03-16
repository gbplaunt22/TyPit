package io.github.TyPit.listenerTemplate.items;

public final class RoundStartedEvent implements ItemEvent {
    public final int roundNumber;
    public final int phaseIndex;
    public final boolean bossRound;

    public RoundStartedEvent(int roundNumber, int phaseIndex, boolean bossRound) {
        this.roundNumber = roundNumber;
        this.phaseIndex = phaseIndex;
        this.bossRound = bossRound;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.ROUND_STARTED;
    }
}
