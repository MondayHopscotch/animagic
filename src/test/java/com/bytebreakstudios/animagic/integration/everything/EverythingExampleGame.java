package com.bytebreakstudios.animagic.integration.everything;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.bytebreakstudios.animagic.animation.Animation;
import com.bytebreakstudios.animagic.animation.FrameRate;
import com.bytebreakstudios.animagic.texture.AnimagicSpriteBatch;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlas;
import com.bytebreakstudios.animagic.texture.AnimagicTextureAtlasLoader;

public class EverythingExampleGame extends Game {
    AnimagicSpriteBatch spriteBatch;
    Animation animation;
    Camera camera;
    AssetManager assetManager;

    float x = 0;
    float y = 0;
    float w = 200;
    float h = 200;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.lookAt(0, 0, 0);
        spriteBatch = new AnimagicSpriteBatch(camera);

        assetManager = new AssetManager();
        assetManager.setLoader(AnimagicTextureAtlas.class, new AnimagicTextureAtlasLoader(new InternalFileHandleResolver()));

        assetManager.load("packed/character.atlas", AnimagicTextureAtlas.class);
        assetManager.finishLoading();

        animation = new Animation("kick", Animation.AnimationPlayState.REPEAT, FrameRate.total(1), assetManager.get("packed/character.atlas", AnimagicTextureAtlas.class).findRegions("kick").toArray(TextureRegion.class));
    }

    @Override
    public void render() {
        super.render();

        animation.update(0.01f);
        camera.update();
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        spriteBatch.begin();

        spriteBatch.setAmbientColor(Color.WHITE);
        spriteBatch.setAmbientIntensity(0.3f);
        spriteBatch.setNextLight(mousePos.x, mousePos.y, 0.1f, 0.9f, Color.RED);
        spriteBatch.setNextLight(-mousePos.x, -mousePos.y, 0.5f, 1, Color.GREEN);

        spriteBatch.draw(animation.getFrame(), x - w / 2, y - h / 2, w, h);
        spriteBatch.end();
    }
}
