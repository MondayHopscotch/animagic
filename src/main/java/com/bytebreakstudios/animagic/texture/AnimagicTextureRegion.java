package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AnimagicTextureRegion extends TextureRegion {
    private AnimagicTextureData meta;
    private Texture normals;

    public AnimagicTextureRegion(Texture texture, Texture normals) {
        super(texture);
        meta = new AnimagicTextureData(0, 0);
        init(normals);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int width, int height) {
        super(texture, width, height);
        meta = new AnimagicTextureData(0, 0);
        init(normals);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
        meta = new AnimagicTextureData(0, 0);
        init(normals);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height, AnimagicTextureData meta) {
        super(texture, x, y, width, height);
        init(normals, meta);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region) {
        super(region);
        init(region.getNormalTexture(), region.meta);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, AnimagicTextureData meta) {
        super(region);
        init(region.getNormalTexture(), meta);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals) {
        super(region);
        init(normals);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals, AnimagicTextureData meta) {
        super(region);
        init(normals, meta);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, int x, int y, int width, int height) {
        super(region, x, y, width, height);
        init(region.getNormalTexture());
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, int x, int y, int width, int height, AnimagicTextureData meta) {
        super(region, x, y, width, height);
        init(region.getNormalTexture(), meta);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals, int x, int y, int width, int height) {
        super(region, x, y, width, height);
        init(normals);
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals, int x, int y, int width, int height, AnimagicTextureData meta) {
        super(region, x, y, width, height);
        init(normals, meta);
    }

    private void init(Texture normals) {
        init(normals, null);
    }

    private void init(Texture normals, AnimagicTextureData meta) {
        if (normals == null) throw new RuntimeException("Normals texture cannot be null");
        this.normals = normals;
        if (meta == null) meta = new AnimagicTextureData(0, 0);
        this.meta = meta;
    }

    public Texture getNormalTexture() {
        return this.normals;
    }


    public Vector2 getRelativeOrigin() {
        return new Vector2(getRelativeOriginX(), getRelativeOriginY());
    }


    public float getRelativeOriginX() {
        int w = getRegionWidth();
        if (w == 0) return 0;
        else return (float) meta.originX / (float) w;
    }


    public float getRelativeOriginY() {
        int h = getRegionHeight();
        if (h == 0) return 0;
        else return (float) meta.originY / (float) h;
    }

    public Vector2 getTextureRegionOrigin() {
        return new Vector2(getTextureRegionOriginX(), getTextureRegionOriginY());
    }


    public int getTextureRegionOriginX() {
        return meta.originX;
    }


    public int getTextureRegionOriginY() {
        return meta.originY;
    }
}

