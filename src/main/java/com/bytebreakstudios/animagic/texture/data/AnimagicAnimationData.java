package com.bytebreakstudios.animagic.texture.data;

import com.bytebreakstudios.animagic.utils.SerializationUtils;

import java.util.HashMap;

public class AnimagicAnimationData extends HashMap<Integer, AnimagicTextureData> {


    public AnimagicAnimationData putting(Integer key, AnimagicTextureData value) {
        super.put(key, value);
        return this;
    }

    public AnimagicTextureData get(int index) {
        if (containsKey(index)) return super.get(index);
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimagicAnimationData that = (AnimagicAnimationData) o;
        return toString().equals(that.toString());
    }

    @Override
    public String toString() {
        return SerializationUtils.toJson(this);
    }
}
