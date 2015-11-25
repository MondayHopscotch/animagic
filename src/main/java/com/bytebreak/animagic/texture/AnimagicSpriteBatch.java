package com.bytebreak.animagic.texture;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class AnimagicSpriteBatch extends SpriteBatch {

    @Override
    public void draw(TextureRegion region, float x, float y) {
        super.draw(region, x, y);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float width, float height) {
        super.draw(region, x, y, width, height);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
    }

    @Override
    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        super.draw(region, width, height, transform);
    }
}
