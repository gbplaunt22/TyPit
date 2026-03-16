package io.github.TyPit.listenerTemplate.items;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemSystem {
    private final ItemRegistry registry;
    private final LinkedHashMap<ItemId, InventoryEntry> inventory = new LinkedHashMap<ItemId, InventoryEntry>();
    private final EnumMap<ItemId, ItemRuntimeState> runtimeStates = new EnumMap<ItemId, ItemRuntimeState>(ItemId.class);
    private final RunStats cachedStats = new RunStats();

    public ItemSystem(ItemRegistry registry) {
        this.registry = registry;
        rebuildPassives();
    }

    public void addItem(ItemId id, int stacks) {
        if (stacks <= 0) {
            return;
        }
        ItemDefinition definition = registry.get(id);
        if (definition == null) {
            return;
        }

        InventoryEntry entry = inventory.get(id);
        if (entry == null) {
            inventory.put(id, new InventoryEntry(id, Math.min(stacks, definition.maxStacks)));
        } else {
            entry.stacks = Math.min(definition.maxStacks, entry.stacks + stacks);
        }
        rebuildPassives();
    }

    public void removeItem(ItemId id, int stacks) {
        if (stacks <= 0) {
            return;
        }

        InventoryEntry entry = inventory.get(id);
        if (entry == null) {
            return;
        }

        entry.stacks -= stacks;
        if (entry.stacks <= 0) {
            inventory.remove(id);
            ItemRuntimeState state = runtimeStates.remove(id);
            if (state != null) {
                state.clear();
            }
        }
        rebuildPassives();
    }

    public int getStacks(ItemId id) {
        InventoryEntry entry = inventory.get(id);
        return entry == null ? 0 : entry.stacks;
    }

    public Collection<InventoryEntry> getInventory() {
        return Collections.unmodifiableCollection(inventory.values());
    }

    public RunStats getStats() {
        return cachedStats;
    }

    public void rebuildPassives() {
        cachedStats.reset();
        for (InventoryEntry entry : inventory.values()) {
            ItemDefinition definition = registry.get(entry.itemId);
            if (definition != null) {
                definition.effect.applyPassives(entry.stacks, cachedStats);
            }
        }
    }

    public void dispatch(ItemEvent event, GameContext context) {
        for (InventoryEntry entry : inventory.values()) {
            ItemDefinition definition = registry.get(entry.itemId);
            if (definition == null) {
                continue;
            }
            ItemRuntimeState state = runtimeStates.get(entry.itemId);
            if (state == null) {
                state = new ItemRuntimeState();
                runtimeStates.put(entry.itemId, state);
            }
            definition.effect.onEvent(event, entry.stacks, state, context);
        }
    }

    public void reset() {
        inventory.clear();
        for (Map.Entry<ItemId, ItemRuntimeState> entry : runtimeStates.entrySet()) {
            entry.getValue().clear();
        }
        runtimeStates.clear();
        rebuildPassives();
    }
}
