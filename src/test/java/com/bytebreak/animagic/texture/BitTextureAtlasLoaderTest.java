package com.bytebreak.animagic.texture;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.junit.Test;

/**
 * Created by Admin on 11/29/2015.
 */
public class BitTextureAtlasLoaderTest {
    @Test
    public void testThings(){
        AssetManager assetManager = new AssetManager();
        assetManager.setLoader(BitTextureAtlas.class, new BitTextureAtlasLoader(new InternalFileHandleResolver()));
        assetManager.load("packed/character.atlas", BitTextureAtlas.class);
        assetManager.finishLoading();
    }
}
