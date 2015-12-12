package com.bytebreakstudios.animagic.integration.texture;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlas;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlasLoader;
import com.bytebreakstudios.animagic.texture.AnimagicTextureRegion;
import com.bytebreakstudios.animagic.texture.data.AnimagicAnimationData;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;

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
        for (int i = 0; i < 9; i++) data.put(i, new AnimagicTextureData(i, i));
        data.put(2, new AnimagicTextureData(20, 20));
        data.put(5, new AnimagicTextureData(25, 25));
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
        Array<AnimagicTextureRegion> kickFrames = atlas.findRegions("kick");
        AnimagicAnimationData realData = new AnimagicAnimationData();
        for (int i = 0; i < kickFrames.size; i++) realData.put(i, kickFrames.get(i).meta());
        if (!data.equals(realData)) {
            failures.add("loadTestMetaAllFrames: ");
            failures.add(data.toString());
            failures.add(realData.toString());
        }
    }

    public void loadTestMetaOneFrame() {
        int frameNumber = 1;
        int frameIndex = frameNumber - 1;
        AnimagicTextureAtlas atlas = assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class);
        AnimagicTextureRegion kickFrame = atlas.findRegion("kick", frameNumber, false);

        AnimagicTextureData expectedData = data.get(frameIndex);
        AnimagicTextureData actualData = kickFrame.meta();
        if (!expectedData.equals(actualData)) {
            failures.add("loadTestMetaOneFrame: ");
            failures.add(data.get(frameIndex).toString());
            failures.add(kickFrame.meta().toString());
        }
    }
}
