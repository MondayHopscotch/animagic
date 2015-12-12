package com.bytebreakstudios.animagic.editor;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.bytebreakstudios.animagic.animation.Animation;
import com.bytebreakstudios.animagic.animation.FrameRate;
import com.bytebreakstudios.animagic.texture.AnimagicTextureRegion;
import com.bytebreakstudios.animagic.texture.data.AnimagicAnimationData;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;
import com.bytebreakstudios.animagic.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.ArrayList;

public class EditorScreen extends InputAdapter implements Screen {

    /**
     * filter to only return directories
     */
    FileFilter pngFilter = pathname -> pathname.getPath().toLowerCase().endsWith(".png");

    private String currentAnimationDir = null;
    private AnimagicTextureRegion selectedRegion = null;

    OrthographicCamera animationCamera;
    OrthographicCamera textureEditCamera;
    private final Stage stage;
    private final Skin skin;

    private final SpriteBatch batch;
    private final ShapeRenderer shaper;


    private Actor animationPanel;
    private Actor scrollPanel;
    private Actor buttonPanel;

    private Rectangle animatorPanelArea = new Rectangle();
    private Rectangle editorPanelArea = new Rectangle();

    private Animation currentAnimation = null;

    private float frameScale = 1;
    private float animationScale = 1;
    private float animationSpeedScale = 1;

    public EditorScreen() {
        animationCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textureEditCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch(1);
        shaper = new ShapeRenderer();

        TextureAtlas menuAtlas = new TextureAtlas(Gdx.files.internal("skins/ui.atlas"));
        stage = new Stage();
        //stage.setDebugAll(true);
        skin = new Skin(Gdx.files.internal("skins/menu-skin.json"), menuAtlas);

        setInputControls();
    }

    private void setInputControls() {
        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(this);
        inputMux.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void show() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        maybeLoadTexture();
        buildUI();
    }

    private void buildUI() {
        stage.clear();
        buildAnimationPanel();
        buildFrameScrollPanel();
        buildButtons();
    }

    private void maybeLoadTexture() {
        Animation newAnimation;
        java.util.List<AnimagicTextureRegion> frames = new ArrayList<>();
        if (currentAnimationDir != null) {
            File animationDir = new File(currentAnimationDir);
            if (animationDir.exists() && animationDir.isDirectory()) {
                File metaFile = new File(animationDir, "meta");
                AnimagicAnimationData metaData = null;
                if (metaFile.exists()) metaData = FileUtils.loadFileAs(AnimagicAnimationData.class, metaFile);

                for (File imageFile :animationDir.listFiles(pngFilter)){
                    frames.add(new AnimagicTextureRegion(new TextureRegion(new Texture(Gdx.files.absolute(imageFile.getAbsolutePath()))), null, (metaData != null ? metaData.get(frames.size()) : null)));
                }
            }
        }
        System.out.println("Found " + frames.size() + " frames");
        if (frames.size() > 0) {
            newAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f * animationSpeedScale), frames.toArray(new AnimagicTextureRegion[frames.size()]));
        } else {
            newAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f * animationSpeedScale), new AnimagicTextureRegion[]{new AnimagicTextureRegion(new Texture(0, 0, Pixmap.Format.RGBA8888), null)});
        }
        currentAnimation = newAnimation;
        if (currentAnimation.totalFrames() > 0)
            selectedRegion = (AnimagicTextureRegion) currentAnimation.getFrames()[0];
    }

    private void buildButtons() {
        Table parentMenu = new Table();
        parentMenu.setFillParent(true);
        parentMenu.setOrigin(Align.topLeft);
        parentMenu.align(Align.topLeft);

        Table menu = new Table();

        Table itemTable = new Table();
        TextButton loadButton = new TextButton("load", skin, "button");

        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("LOADING");
                String loadDir = FileUtils.selectDirectory();
                System.out.println(loadDir);
                if (loadDir != null) {
                    loadAnimation(loadDir);
                }
            }
        });

        TextButton saveButton = new TextButton("save", skin, "button");
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SAVING");
                if (currentAnimationDir != null) {
                    AnimagicAnimationData animationData = new AnimagicAnimationData();
                    for (int i = 0; i < currentAnimation.totalFrames(); i++){
                        animationData.put(i, ((AnimagicTextureRegion) currentAnimation.getFrames()[i]).meta());
                    }
                    FileUtils.saveToFile(animationData, Paths.get(currentAnimationDir, "meta").toString());
                }
            }
        });

        TextButton centerButton = new TextButton("center", skin, "button");
        centerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked CENTER button");
                int lastSelected = 0;
                AnimagicAnimationData meta = new AnimagicAnimationData();
                for (int i = 0; i < currentAnimation.totalFrames(); i++) {
                    TextureRegion frame = currentAnimation.getFrames()[i];
                    if (frame == selectedRegion) lastSelected = i;
                    meta.put(i, new AnimagicTextureData(frame.getRegionWidth() / 2, frame.getRegionHeight() / 2));
                }
                refreshCurrentAnimation(meta, lastSelected);
            }
        });

        itemTable.add(loadButton);
        itemTable.add(saveButton).padRight(10);
        itemTable.add(centerButton);

        buttonPanel = itemTable;

        menu.add(itemTable);
        menu.padBottom(-20);
        parentMenu.add(menu);

        stage.addActor(parentMenu);
    }

    private void loadAnimation(String loadDir) {
        currentAnimationDir = loadDir;
        maybeLoadTexture();
        buildUI();
    }

    private void buildFrameScrollPanel() {
        Table parentMenu = new Table();
        parentMenu.setFillParent(true);
        parentMenu.setOrigin(Align.bottomLeft);
        parentMenu.align(Align.bottom);

        Table menu = new Table();

        ScrollPane scrollPane = new ScrollPane(menu, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);
        scrollPanel = scrollPane;

        parentMenu.add(scrollPane);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.setUncheckLast(true);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);

        for (TextureRegion frame : currentAnimation.getFrames()) {
            Table itemTable = new Table();
            TextureRegionDrawable upDrawable = new TextureRegionDrawable(frame);
            SpriteDrawable downSprite = upDrawable.tint(Color.GREEN);
            ImageButton button = new ImageButton(upDrawable, downSprite, downSprite);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedRegion = (AnimagicTextureRegion) frame;
                }
            });
            buttonGroup.add(button);
            itemTable.add(button).width(64).height(64).padBottom(40).padTop(20);
            itemTable.row();

            menu.add(itemTable).padRight(20);

            if (frame == selectedRegion) button.setChecked(true);
        }
        menu.padBottom(-20);

        stage.addActor(parentMenu);
    }

    private void buildAnimationPanel() {
        Table parentMenu = new Table();
        parentMenu.setFillParent(true);
        parentMenu.setOrigin(Align.topRight);
        parentMenu.align(Align.topRight);

        Table menu = new Table();
        menu.setSize(200, 300);

        Table itemTable = new Table();
        TextButton button = new TextButton("", skin, "button");
        itemTable.add(button).width(200).height(300).padBottom(20);

        animationPanel = button;

        menu.add(itemTable);
        menu.row();
//        menu.padBottom(-20);
        parentMenu.add(menu);

        stage.addActor(parentMenu);
        System.out.println(button.localToStageCoordinates(new Vector2(0, 0)));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) frameScale *= 1.1f;
        else if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) frameScale *= .9f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT_BRACKET)) animationScale *= 1.1f;
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET)) animationScale *= .9f;

        if (animationPanel != null) {
            renderAnimationPanel(delta);
        }

        if (selectedRegion != null) {
            renderSelectedRegion(delta);
        }

    }

    private void renderAnimationPanel(float delta) {
        Vector2 cameraLoc = new Vector2(Gdx.graphics.getWidth(), 0);
        Vector2 size = new Vector2(animationPanel.getWidth(), animationPanel.getHeight());
        Vector2 halfSize = new Vector2(size).scl(.5f);
        cameraLoc.sub(animationPanel.localToStageCoordinates(new Vector2(0, 0)).sub(halfSize));

        animationCamera.position.set(-1 * (cameraLoc.x), 1 * (cameraLoc.y), 0);
        animationCamera.update();
        batch.setProjectionMatrix(animationCamera.combined);
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(-halfSize.x, -halfSize.y, size.x, size.y);
        ScissorStack.calculateScissors(animationCamera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        animatorPanelArea.set(scissors);
        currentAnimation.update(delta);

        batch.begin();
        AnimagicTextureRegion frame = (AnimagicTextureRegion) currentAnimation.getFrame();
        batch.draw(frame, -frame.meta().originX * animationScale, -frame.meta().originY * animationScale, frame.getRegionWidth() * animationScale, frame.getRegionHeight() * animationScale);
        batch.flush();
        batch.end();
        ScissorStack.popScissors();

        shaper.setProjectionMatrix(animationCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
//        shaper.setColor(Color.WHITE);
//        shaper.rect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        shaper.setColor(Color.PINK);
        shaper.circle(0, 0, animationScale);
        shaper.end();
    }

    private void renderSelectedRegion(float delta) {
        Vector2 animatorPanelLoc = animationPanel.localToStageCoordinates(new Vector2(0, 0));
        Vector2 scrollPanelLoc = scrollPanel.localToStageCoordinates(new Vector2(0, 0));
        scrollPanelLoc.add(0, scrollPanel.getHeight());
        Vector2 buttonPanelLoc = buttonPanel.localToStageCoordinates(new Vector2(0, 0));

        Vector2 cameraAreaSize = new Vector2();
        cameraAreaSize.y = Math.abs(buttonPanelLoc.y - scrollPanelLoc.y); // distance from top of scroll panel to bottom of button panel
        cameraAreaSize.x = Math.abs(animatorPanelLoc.x);

//        System.out.println("Edit Panel Size: " + cameraAreaSize);


        Vector2 regionCamCenterPoint = new Vector2(0, 0);
        regionCamCenterPoint.x = cameraAreaSize.x / 2; // this camera is rendering up against the left side of the screen
        regionCamCenterPoint.y = scrollPanel.getHeight() + cameraAreaSize.y/2;

//        System.out.println("Cam origin location: " + regionCamCenterPoint);

        Vector2 texturePanelLocation = new Vector2(-1 * (Gdx.graphics.getWidth()/2 - regionCamCenterPoint.x), -1 * (Gdx.graphics.getHeight()/2 - regionCamCenterPoint.y));

        textureEditCamera.position.set(-1 * texturePanelLocation.x, -1 * texturePanelLocation.y, 0);
        textureEditCamera.update();
        batch.setProjectionMatrix(textureEditCamera.combined);
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(-cameraAreaSize.x/2, -cameraAreaSize.y/2, cameraAreaSize.x, cameraAreaSize.y);
        ScissorStack.calculateScissors(textureEditCamera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);

        editorPanelArea.set(scissors);

        batch.begin();
        //batch.draw(selectedRegion, selectedRegion.meta.xOffset, selectedRegion.meta.yOffset);
        batch.draw(selectedRegion, -selectedRegion.getRegionWidth() * frameScale / 2, -selectedRegion.getRegionHeight() * frameScale / 2, selectedRegion.getRegionWidth() * frameScale, selectedRegion.getRegionHeight() * frameScale);
        batch.flush();
        batch.end();
        ScissorStack.popScissors();

        shaper.setProjectionMatrix(textureEditCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Filled);
//        shaper.setColor(Color.WHITE);
//        shaper.rect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        shaper.setColor(Color.PINK);
        shaper.circle(selectedRegion.meta().originX * frameScale - selectedRegion.getRegionWidth() * frameScale / 2, selectedRegion.meta().originY * frameScale - selectedRegion.getRegionHeight() * frameScale / 2, frameScale);
        shaper.end();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 editWorldMousePos = new Vector2(textureEditCamera.unproject(new Vector3(screenX, screenY, 0)).x, textureEditCamera.unproject(new Vector3(screenX, screenY, 0)).y);
        Vector2 animationWorldMousePos = new Vector2(animationCamera.unproject(new Vector3(screenX, screenY, 0)).x, animationCamera.unproject(new Vector3(screenX, screenY, 0)).y);
        if (editorPanelArea.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
            System.out.println("CLICKED THE EDITOR " + editWorldMousePos);
            Vector2 editSpriteMousePos = editWorldMousePos.add(selectedRegion.getRegionWidth() * frameScale / 2, selectedRegion.getRegionHeight() * frameScale / 2).scl(1f / frameScale);
            System.out.println("Mouse on Sprite: " + editSpriteMousePos);
            setFrameOrigin(editSpriteMousePos);
        }
        if (animatorPanelArea.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
            System.out.println("CLICKED THE ANIMATOR" + animationWorldMousePos + " " + button + " " + pointer);
            if (button == 1) {
                animationSpeedScale *= 1.2f;
                refreshCurrentAnimation();
            } else if (button == 0) {
                animationSpeedScale *= .8f;
                refreshCurrentAnimation();
            }
        }
        return false;
    }

    public void setFrameOrigin(Vector2 spritePos) {
        int lastSelected = 0;
        AnimagicAnimationData meta = new AnimagicAnimationData();
        for (int i = 0; i < currentAnimation.totalFrames(); i++) {
            if (selectedRegion == currentAnimation.getFrames()[i]) {
                lastSelected = i;
                meta.put(i, new AnimagicTextureData((int) spritePos.x, (int) spritePos.y));
            } else meta.put(i, ((AnimagicTextureRegion) currentAnimation.getFrames()[i]).meta());
        }
        refreshCurrentAnimation(meta, lastSelected);
    }

    public void refreshCurrentAnimation(AnimagicAnimationData meta, int lastSelected) {
        java.util.List<AnimagicTextureRegion> refreshedRegions = new ArrayList<>();

        for (int i = 0; i < currentAnimation.totalFrames(); i++) {
            AnimagicTextureData frameMeta = meta.get(i);
            if (frameMeta == null) frameMeta = ((AnimagicTextureRegion) currentAnimation.getFrames()[i]).meta();
            refreshedRegions.add(new AnimagicTextureRegion((AnimagicTextureRegion) currentAnimation.getFrames()[i], frameMeta));
        }

        currentAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f * animationSpeedScale), refreshedRegions.toArray(new TextureRegion[refreshedRegions.size()]));

        selectedRegion = (AnimagicTextureRegion) currentAnimation.getFrames()[lastSelected];
        buildUI();
    }

    public void refreshCurrentAnimation() {
        int lastSelected = 0;
        AnimagicAnimationData meta = new AnimagicAnimationData();
        for (int i = 0; i < currentAnimation.totalFrames(); i++) {
            if (selectedRegion == currentAnimation.getFrames()[i]) {
                lastSelected = i;
            }
            meta.put(i, ((AnimagicTextureRegion) currentAnimation.getFrames()[i]).meta());
        }
        refreshCurrentAnimation(meta, lastSelected);
    }
}
