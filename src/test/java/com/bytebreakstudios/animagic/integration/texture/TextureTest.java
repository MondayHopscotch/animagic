package com.bytebreakstudios.animagic.integration.texture;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlas;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlasLoader;
import com.bytebreakstudios.animagic.texture.AnimagicTextureRegion;

/**
 * Test class for Animation Metadata. Done inside a Game instance to make use of the assetManager
 * Created by Monday on 11/30/2015.
 */
public class TextureTest extends Game {
    AssetManager assetManager;
    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(AnimagicTextureAtlas.class, new AnimagicTextureAtlasLoader(new InternalFileHandleResolver()));

        assetManager.load("packed/character.atlas", AnimagicTextureAtlas.class);
        assetManager.finishLoading();

        loadTestMetaAllFrames();
        loadTestMetaOneFrame();

        Gdx.app.exit();
    }

    public void loadTestMetaAllFrames() {
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegionsWithMeta("kick");
        for (int i = 0; i < kickFrames.size; i++) {
            AnimagicTextureRegion frame = kickFrames.get(i);
            if (frame.getTextureRegionOriginX() != i) {
                throw new RuntimeException("xOrigin is wrong: " + frame.getTextureRegionOriginX() + " instead of expected " + i);
            }
            if (frame.getTextureRegionOriginY() != i) {
                throw new RuntimeException("yOrigin is wrong: " + frame.getTextureRegionOriginY() + " instead of expected " + i);
            }
        }
    }

    public void loadTestMetaOneFrame() {
        int frameNumber = 5;
        int offset = frameNumber - 1;
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegionsWithMeta("kick/" + frameNumber);
        for (int i = 0; i < kickFrames.size; i++) {
            AnimagicTextureRegion frame = kickFrames.get(i);
            if (frame.getTextureRegionOriginX() != offset) {
                throw new RuntimeException("xOrigin is wrong: " + frame.getTextureRegionOriginX() + " instead of expected " + offset);
            }
            if (frame.getTextureRegionOriginY() != offset) {
                throw new RuntimeException("yOrigin is wrong: " + frame.getTextureRegionOriginY() + " instead of expected " + offset);
            }
        }
    }
}
