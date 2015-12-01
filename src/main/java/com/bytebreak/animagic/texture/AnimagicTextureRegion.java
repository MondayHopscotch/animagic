package com.bytebreak.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AnimagicTextureRegion extends TextureRegion {
    public AnimagicTextureData meta;
    private Texture normals;
    private float offsetX = 0;
    private float offsetY = 0;

    public AnimagicTextureRegion(Texture texture, Texture normals) {
        super(texture);
        init(normals, 0, 0);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int width, int height) {
        super(texture, width, height);
        init(normals, 0, 0);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
        init(normals, 0, 0);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region) {
        super(region);
        init(region.getNormalTexture(), 0, 0);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals) {
        super(region);
        init(normals, 0, 0);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, int x, int y, int width, int height) {
        super(region, x, y, width, height);
        init(region.getNormalTexture(), 0, 0);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals, int x, int y, int width, int height) {
        super(region, x, y, width, height);
        init(normals, 0, 0);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height, float offsetX, float offsetY) {
        super(texture, x, y, width, height);
        init(normals, offsetX, offsetY);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, int x, int y, int width, int height, float offsetX, float offsetY) {
        super(region, x, y, width, height);
        init(region.getNormalTexture(), offsetX, offsetY);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals, int x, int y, int width, int height, float offsetX, float offsetY) {
        super(region, x, y, width, height);
        init(normals, offsetX, offsetY);
    }

    private void init(Texture normals, float offsetX, float offsetY) {
        if (normals == null) throw new RuntimeException("Normals texture cannot be null");
        this.normals = normals;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Texture getNormalTexture() {
        return this.normals;
    }


    public Vector2 getOffset() {
        return new Vector2(offsetX, offsetY);
    }
}

