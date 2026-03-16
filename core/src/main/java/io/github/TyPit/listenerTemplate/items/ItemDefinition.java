package io.github.TyPit.listenerTemplate.items;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class ItemDefinition {
    public final ItemId id;
    public final String name;
    public final String description;
    public final ItemRarity rarity;
    public final Set<ItemTag> tags;
    public final int maxStacks;
    public final int shopCost;
    public final int shopWeight;
    public final ItemEffect effect;

    public ItemDefinition(
        ItemId id,
        String name,
        String description,
        ItemRarity rarity,
        EnumSet<ItemTag> tags,
        int maxStacks,
        int shopCost,
        int shopWeight,
        ItemEffect effect
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.tags = Collections.unmodifiableSet(tags);
        this.maxStacks = maxStacks;
        this.shopCost = shopCost;
        this.shopWeight = shopWeight;
        this.effect = effect;
    }
}
