package com.bytebreakstudios.animagic.texture.data;

import com.bytebreakstudios.animagic.utils.SerializationUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimagicTextureData {
    public final int originX;
    public final int originY;

    @JsonCreator
    public AnimagicTextureData(@JsonProperty("originX") int originX, @JsonProperty("originY") int originY) {
        this.originX = originX;
        this.originY = originY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimagicTextureData that = (AnimagicTextureData) o;
        return originX == that.originX && originY == that.originY;
    }

    @Override
    public int hashCode() {
        int result = originX;
        result = 31 * result + originY;
        return result;
    }

    @Override
    public String toString() {
        return SerializationUtils.toJson(this);
    }
}
