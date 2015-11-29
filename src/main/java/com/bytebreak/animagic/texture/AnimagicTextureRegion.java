package com.bytebreak.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimagicTextureRegion extends TextureRegion {
    public AnimagicTextureData meta;
    private Texture normals;

    public AnimagicTextureRegion(Texture texture, Texture normals) {
        super(texture);
        init(normals);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int width, int height) {
        super(texture, width, height);
        init(normals);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height) {
        super(texture, x, y, width, height);
        init(normals);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, float u, float v, float u2, float v2) {
        super(texture, u, v, u2, v2);
        init(normals);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region) {
        super(region);
        init(region.getNormalTexture());
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals) {
        super(region);
        init(normals);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, int x, int y, int width, int height) {
        super(region, x, y, width, height);
        init(region.getNormalTexture());
    }

    public AnimagicTextureRegion(TextureRegion region, Texture normals, int x, int y, int width, int height) {
        super(region, x, y, width, height);
        init(normals);
    }

    private void init(Texture normals) {
        if (normals == null) throw new RuntimeException("Normals texture cannot be null");
        this.normals = normals;
    }

    public Texture getNormalTexture() {
        return this.normals;
    }


}

