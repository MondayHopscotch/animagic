package com.bytebreak.animagic.integration.texture;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.bytebreak.animagic.utils.FileUtils;
import com.bytebreak.animagic.texture.*;

/**
 * Created by Monday on 11/30/2015.
 */
public class TextureTest extends Game {
    public static final String ASSET_DIR = "sprites";
    AssetManager assetManager;
    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(AnimagicTextureAtlas.class, new AnimagicTextureAtlasLoader(new InternalFileHandleResolver()));

        assetManager.load("packed/character.atlas", AnimagicTextureAtlas.class);
        assetManager.finishLoading();

        saveTestMeta();
        loadTestMetaAllFrames();
        loadTestMetaOneFrame();

        Gdx.app.exit();
    }

    public void saveTestMeta() {
        AnimagicAnimationData animationData = new AnimagicAnimationData();
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegionsWithoutMeta("kick");
        for (int i = 0; i < kickFrames.size; i++) {
            AnimagicTextureData frameMeta = new AnimagicTextureData();
            frameMeta.xOffset = i;
            frameMeta.yOffset = i;
            animationData.frameData.add(frameMeta);
        }
        FileUtils.saveToFile(animationData, "packed/meta/character.kick.meta");
    }

    public void loadTestMetaAllFrames() {
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegionsWithMeta("kick");
        for (int i = 0; i < kickFrames.size; i++) {
            AnimagicTextureRegion frame = kickFrames.get(i);
            if (frame.getOffset().x != i) {
                throw new RuntimeException("xOffset is wrong: " + frame.getOffset().x + " instead of expected " + i);
            }
            if (frame.getOffset().y != i) {
                throw new RuntimeException("yOffset is wrong: " + frame.getOffset().y + " instead of expected " + i);
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
            if (frame.getOffset().x != offset) {
                throw new RuntimeException("xOffset is wrong: " + frame.getOffset().x + " instead of expected " + offset);
            }
            if (frame.getOffset().y != offset) {
                throw new RuntimeException("yOffset is wrong: " + frame.getOffset().y + " instead of expected " + offset);
            }
        }
    }
}
