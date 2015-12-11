package com.bytebreakstudios.animagic.texture.data;

import com.bytebreakstudios.animagic.utils.SerializationUtils;

import java.util.HashMap;

public class AnimagicAtlasData extends HashMap<String, AnimagicAnimationData> {

    public AnimagicAtlasData() {
        super();
    }

    @Override
    public AnimagicAnimationData get(Object key) {
        if (containsKey(key)) return super.get(key);
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimagicAtlasData that = (AnimagicAtlasData) o;
        return toString().equals(that.toString());
    }

    @Override
    public String toString() {
        return SerializationUtils.toJson(this);
    }
}
