package com.bytebreak.animagic.integration.spritebatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.bytebreak.animagic.texture.AnimagicSpriteBatch;
import com.bytebreak.animagic.texture.AnimagicTextureRegion;

public class ExampleGame extends Game {
    AnimagicSpriteBatch spriteBatchA;
    SpriteBatch spriteBatchB;
    AnimagicTextureRegion texture;
    Camera camera;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.lookAt(0, 0, 0);
        spriteBatchA = new AnimagicSpriteBatch(camera);
        spriteBatchB = new SpriteBatch();


        texture = new AnimagicTextureRegion(new Texture("textures/spark.png"), new Texture("textures/spark_n.png"), 0, 0, 180, 180, -90, -90);
    }

    @Override
    public void render() {
        super.render();

        camera.update();
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        spriteBatchA.begin();
        spriteBatchA.setAmbientColor(Color.WHITE);
        spriteBatchA.setAmbientIntensity(0.3f);
        spriteBatchA.setNextLight(mousePos.x, mousePos.y, 0.1f, 0.9f, Color.RED);
        spriteBatchA.setNextLight(-mousePos.x, -mousePos.y, 0.5f, 1, Color.GREEN);

        spriteBatchA.draw(texture, 0, 0);
        spriteBatchA.end();

        spriteBatchB.begin();
        spriteBatchB.draw(texture, Gdx.graphics.getWidth() - texture.getTexture().getWidth(), Gdx.graphics.getHeight() - texture.getTexture().getHeight());
        spriteBatchB.end();
    }
}
