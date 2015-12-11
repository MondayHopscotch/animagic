package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;

public class AnimagicTextureRegion extends TextureRegion {
    private final AnimagicTextureData meta;
    private final TextureRegion normals;
    public final AnimagicTextureRegionShaderData shaderData;

    private AnimagicTextureRegion(Texture texture, int x, int y, int width, int height, Texture normals, int xN, int yN, int widthN, int heightN, AnimagicTextureData meta) {
        super(texture, x, y, width, height);
        this.normals = new TextureRegion(normals, xN, yN, widthN, heightN);
        this.meta = new AnimagicTextureData(meta);
        shaderData = new AnimagicTextureRegionShaderData(this);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight(), normals, 0, 0, normals.getWidth(), normals.getHeight(), null);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, AnimagicTextureData meta) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight(), normals, 0, 0, normals.getWidth(), normals.getHeight(), meta);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height) {
        this(texture, x, y, width, height, normals, x, y, width, height, null);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height, AnimagicTextureData meta) {
        this(texture, x, y, width, height, normals, x, y, width, height, meta);
    }

    public AnimagicTextureRegion(TextureRegion texture, TextureRegion normals) {
        this(texture.getTexture(), texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight(), normals.getTexture(), normals.getRegionX(), normals.getRegionY(), normals.getRegionWidth(), normals.getRegionHeight(), null);
    }

    public AnimagicTextureRegion(TextureRegion texture, TextureRegion normals, AnimagicTextureData meta) {
        this(texture.getTexture(), texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight(), normals.getTexture(), normals.getRegionX(), normals.getRegionY(), normals.getRegionWidth(), normals.getRegionHeight(), meta);
    }

    public AnimagicTextureRegion(TextureRegion texture, Texture normals) {
        this(texture.getTexture(), texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight(), normals, texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight(), null);
    }

    public AnimagicTextureRegion(TextureRegion texture, Texture normals, AnimagicTextureData meta) {
        this(texture.getTexture(), texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight(), normals, texture.getRegionX(), texture.getRegionY(), texture.getRegionWidth(), texture.getRegionHeight(), meta);
    }

    public TextureRegion getNormalTextureRegion() {
        return normals;
    }


    public Vector2 getOriginPercentage() {
        return new Vector2(getOriginPercentageX(), getOriginPercentageY());
    }


    public float getOriginPercentageX() {
        int w = getRegionWidth();
        if (w == 0) return 0;
        else return (float) meta.originX / (float) w;
    }


    public float getOriginPercentageY() {
        int h = getRegionHeight();
        if (h == 0) return 0;
        else return (float) meta.originY / (float) h;
    }

    public Vector2 getRegionOrigin() {
        return new Vector2(getRegionOriginX(), getRegionOriginY());
    }


    public int getRegionOriginX() {
        return meta.originX;
    }


    public int getRegionOriginY() {
        return meta.originY;
    }

    public AnimagicTextureData meta() {
        return meta;
    }

    public static final class AnimagicTextureRegionShaderData {
        public final float xCoordMin;
        public final float xCoordDiff;
        public final float yCoordMin;
        public final float yCoordDiff;
        public final float xNorCoordMin;
        public final float xNorCoordDiff;
        public final float yNorCoordMin;
        public final float yNorCoordDiff;

        public AnimagicTextureRegionShaderData(AnimagicTextureRegion region) {
            float width = region.getTexture().getWidth();
            float height = region.getTexture().getHeight();
            float x = region.getRegionX();
            float y = region.getRegionY();
            float w = region.getRegionWidth();
            float h = region.getRegionHeight();
            float width_n = region.getNormalTextureRegion().getTexture().getWidth();
            float height_n = region.getNormalTextureRegion().getTexture().getHeight();
            float x_n = region.getNormalTextureRegion().getRegionX();
            float y_n = region.getNormalTextureRegion().getRegionY();
            float w_n = region.getNormalTextureRegion().getRegionWidth();
            float h_n = region.getNormalTextureRegion().getRegionHeight();


            xCoordMin = x / width;
            float xCoordMax = (x + w) / width;
            xCoordDiff = xCoordMax - xCoordMin;

            yCoordMin = y / height;
            float yCoordMax = (y + h) / height;
            yCoordDiff = yCoordMax - yCoordMin;

            xNorCoordMin = x_n / width_n;
            float xNorCoordMax = (x_n + w_n) / width_n;
            xNorCoordDiff = xNorCoordMax - xNorCoordMin;

            yNorCoordMin = y_n / height_n;
            float yNorCoordMax = (y_n + h_n) / height_n;
            yNorCoordDiff = yNorCoordMax - yNorCoordMin;
        }

        public AnimagicTextureRegionShaderData(float xCoordMin, float xCoordDiff, float yCoordMin, float yCoordDiff, float xNorCoordMin, float xNorCoordDiff, float yNorCoordMin, float yNorCoordDiff) {
            this.xCoordMin = xCoordMin;
            this.xCoordDiff = xCoordDiff;
            this.yCoordMin = yCoordMin;
            this.yCoordDiff = yCoordDiff;
            this.xNorCoordMin = xNorCoordMin;
            this.xNorCoordDiff = xNorCoordDiff;
            this.yNorCoordMin = yNorCoordMin;
            this.yNorCoordDiff = yNorCoordDiff;
        }
    }
}

