package com.bytebreakstudios.animagic.texture;

import java.util.ArrayList;

public class AnimagicAnimationData extends ArrayList<AnimagicTextureData> {
    public AnimagicAnimationData() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimagicAnimationData that = (AnimagicAnimationData) o;

        if (size() == that.size()) {
            for (int i = 0; i < size(); i++) {
                if (!get(i).equals(that.get(i))) return false;
            }
            return true;
        }
        return false;
    }
}
