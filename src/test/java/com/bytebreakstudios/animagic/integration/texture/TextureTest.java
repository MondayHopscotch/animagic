package com.bytebreakstudios.animagic.integration.texture;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.bytebreakstudios.animagic.texture.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Animation Metadata. Done inside a Game instance to make use of the assetManager
 */
public class TextureTest extends Game {
    AssetManager assetManager;
    AnimagicAnimationData data;

    List<String> failures = new ArrayList<>();

    public void setupTextureData() {
        data = new AnimagicAnimationData();
        for (int i = 0; i < 9; i++) data.add(new AnimagicTextureData(i, i));
    }


    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(AnimagicTextureAtlas.class, new AnimagicTextureAtlasLoader(new InternalFileHandleResolver()));

        assetManager.load("packed/character.atlas", AnimagicTextureAtlas.class);
        assetManager.finishLoading();

        setupTextureData();

        loadTestMetaAllFrames();
        loadTestMetaOneFrame();

        System.err.println("Failures: " + failures.size());
        failures.forEach(System.err::println);

        Gdx.app.exit();
    }

    public void loadTestMetaAllFrames() {
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegionsWithMeta("kick");
        AnimagicAnimationData realData = new AnimagicAnimationData();
        kickFrames.forEach(frame -> realData.add(new AnimagicTextureData(frame.getTextureRegionOriginX(), frame.getTextureRegionOriginY())));
        if (!data.equals(realData)) {
            failures.add("loadTestMetaAllFrames: ");
            failures.add(data.toString());
            failures.add(realData.toString());
        }
    }

    public void loadTestMetaOneFrame() {
        int frameNumber = 5;
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegionsWithMeta("kick/" + frameNumber);
        AnimagicAnimationData realData = new AnimagicAnimationData();
        kickFrames.forEach(frame -> realData.add(new AnimagicTextureData(frame.getTextureRegionOriginX(), frame.getTextureRegionOriginY())));
        if (!data.get(frameNumber).equals(realData.get(0))) {
            failures.add("loadTestMetaOneFrame: ");
            failures.add(data.get(frameNumber).toString());
            failures.add(realData.get(0).toString());
        }
    }
}
