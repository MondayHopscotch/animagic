package com.bytebreakstudios.animagic.integration.animation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlas;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlasLoader;

public class AnimationExampleGame extends Game {
    AssetManager assetManager;
    SpriteBatch spriteBatch;

    Character character;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(AnimagicTextureAtlas.class, new AnimagicTextureAtlasLoader(new InternalFileHandleResolver()));

        assetManager.load("packed/character.atlas", AnimagicTextureAtlas.class);
        assetManager.finishLoading();

        character = new Character(assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class));

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render() {
        super.render();

        character.update(1f / 60f);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        character.draw(spriteBatch);
        spriteBatch.end();
    }
}
