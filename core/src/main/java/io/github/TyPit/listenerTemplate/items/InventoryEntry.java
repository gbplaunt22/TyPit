package io.github.TyPit.listenerTemplate.items;

public final class InventoryEntry {
    public final ItemId itemId;
    public int stacks;

    public InventoryEntry(ItemId itemId, int stacks) {
        this.itemId = itemId;
        this.stacks = stacks;
    }
}
