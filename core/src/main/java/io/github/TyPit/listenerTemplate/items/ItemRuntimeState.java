package io.github.TyPit.listenerTemplate.items;

import java.util.HashMap;

public final class ItemRuntimeState {
    private final HashMap<String, Integer> ints = new HashMap<String, Integer>();
    private final HashMap<String, Float> floats = new HashMap<String, Float>();
    private final HashMap<String, Boolean> bools = new HashMap<String, Boolean>();

    public int getInt(String key) {
        Integer value = ints.get(key);
        return value == null ? 0 : value;
    }

    public void setInt(String key, int value) {
        ints.put(key, value);
    }

    public float getFloat(String key) {
        Float value = floats.get(key);
        return value == null ? 0f : value;
    }

    public void setFloat(String key, float value) {
        floats.put(key, value);
    }

    public boolean getBool(String key) {
        Boolean value = bools.get(key);
        return value != null && value;
    }

    public void setBool(String key, boolean value) {
        bools.put(key, value);
    }

    public void clear() {
        ints.clear();
        floats.clear();
        bools.clear();
    }
}
