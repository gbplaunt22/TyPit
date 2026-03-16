package io.github.TyPit.listenerTemplate.items;

public interface ItemEffect {
    default void applyPassives(int stacks, RunStats stats) {
    }

    default void onEvent(ItemEvent event, int stacks, ItemRuntimeState state, GameContext context) {
    }
}
