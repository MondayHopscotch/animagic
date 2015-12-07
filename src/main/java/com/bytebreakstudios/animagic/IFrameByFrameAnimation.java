package com.bytebreakstudios.animagic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface IFrameByFrameAnimation {

    void update(float delta);

    int getFrameIndex();

    TextureRegion getFrame();

    void reset();
}
