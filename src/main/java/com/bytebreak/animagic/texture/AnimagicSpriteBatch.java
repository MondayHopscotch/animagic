package com.bytebreak.animagic.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sun.istack.internal.NotNull;

public class AnimagicSpriteBatch extends SpriteBatch {

    public final static int MAX_LIGHTS = 10;
    private static final Color BLACK = Color.BLACK.cpy();

    private Camera camera;


    public AnimagicSpriteBatch(@NotNull Camera camera){
        super();
        this.setCamera(camera);
        this.setShader(createShader());
    }

    public AnimagicSpriteBatch setCamera(@NotNull Camera camera){
        if (camera == null) throw new RuntimeException("Cannot set the AnimagicSpriteBatch.camera with a null camera");
        this.camera = camera;
        return this;
    }

    private ShaderProgram createShader() {
        FileHandle vertex = Gdx.files.internal("vertex.glsl");
        FileHandle fragment = Gdx.files.internal("fragment.glsl");
        ShaderProgram program = new ShaderProgram(vertex, fragment);
        // u_proj and u_trans will not be active but SpriteBatch will still try to set them...
        ShaderProgram.pedantic = false;
        if (!program.isCompiled())
            throw new RuntimeException("Could not compile Animagic shader: "
                    + program.getLog());

        // we are only using this many uniforms for testing purposes...!!
        program.begin();
        program.setUniformi("u_texture", 0);
        program.setUniformi("u_normals", 1);
        program.setUniformf("strength", 1);
        //program.setUniformf("ambientIntensity", 0.1f);
        program.setUniformf("ambientColor", new Vector3(1f, 1f, 1f));
        program.setUniformf("resolution", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        program.setUniformi("useShadow", true ? 1 : 0);
        program.setUniformi("useNormals", true ? 1 : 0);
        program.setUniformi("yInvert", false ? 1 : 0);
        program.end();

        return program;
    }

    public AnimagicSpriteBatch setAmbientColor(Color ambientColor){
        getShader().setUniformf("ambientColor", new Vector3(ambientColor.r, ambientColor.g, ambientColor.b));
        return this;
    }

    public AnimagicSpriteBatch setLight(int index, float x, float y, float z, float intensity, float attenuationX, float attenuationY, float attenuationZ, Color color){
        if (index < 0) throw new RuntimeException("Light index cannot be less than 0");
        else if (index >= MAX_LIGHTS) throw new RuntimeException("Light index cannot be greater or equal to " + MAX_LIGHTS);

        ShaderProgram program = getShader();
        Vector3 p = new Vector3(x, y, z);
        Vector3 pScreen = camera.project(p);
        pScreen.z = z;
        program.setUniformf("light" + index, pScreen);
        program.setUniformf("lightColor" + index, new Vector3(color.r, color.g, color.b));
        program.setUniformf("intensity" + index, intensity);
        program.setUniformf("attenuation" + index, new Vector3(attenuationX, attenuationY, attenuationZ));
        return this;
    }

    private void setLightsToDefault(){
        for (int i = 0; i < MAX_LIGHTS; i++){
            setLight(i, 0, 0, 0, 0, 0, 0, 0, Color.BLACK);
        }
    }

    @Override
    public void begin() {
        setProjectionMatrix(camera.combined);
        super.begin();
        setLightsToDefault();
    }

    private void preDraw(AnimagicTextureRegion region){
        if (region.getNormalTexture() != null) {
            this.flush();
            region.getNormalTexture().bind(1);
        }
        region.getTexture().bind(0);
    }

    public void draw(AnimagicTextureRegion region, float x, float y) {
        preDraw(region);
        super.draw(region, x, y);
    }


    public void draw(AnimagicTextureRegion region, float x, float y, float width, float height) {
        preDraw(region);
        super.draw(region, x, y, width, height);
    }


    public void draw(AnimagicTextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        preDraw(region);
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }


    public void draw(AnimagicTextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        preDraw(region);
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
    }


    public void draw(AnimagicTextureRegion region, float width, float height, Affine2 transform) {
        preDraw(region);
        super.draw(region, width, height, transform);
    }


}
