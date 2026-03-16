package io.github.TyPit.listenerTemplate.items;

public final class ItemPurchasedEvent implements ItemEvent {
    public final ItemId itemId;
    public final int cost;
    public final int stacksAfterPurchase;

    public ItemPurchasedEvent(ItemId itemId, int cost, int stacksAfterPurchase) {
        this.itemId = itemId;
        this.cost = cost;
        this.stacksAfterPurchase = stacksAfterPurchase;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.ITEM_PURCHASED;
    }
}
