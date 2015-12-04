package com.bytebreak.animagic.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
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
import com.bytebreak.animagic.Animation;
import com.bytebreak.animagic.FrameRate;
import com.bytebreak.animagic.texture.AnimagicAnimationData;
import com.bytebreak.animagic.texture.AnimagicTextureRegion;
import com.bytebreak.animagic.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Monday on 12/2/2015.
 */
public class EditorScreen extends InputAdapter implements Screen {

    /**
     * filter to only return directories
     */
    FileFilter pngFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getPath().toLowerCase().endsWith(".png");
        }
    };

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

    private Animation currentAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f), new TextureRegion[] {new TextureRegion(new Texture(0, 0, Pixmap.Format.RGBA8888))});

    public EditorScreen() {
        animationCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textureEditCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch(1);
        shaper = new ShapeRenderer();

        TextureAtlas menuAtlas = new TextureAtlas(Gdx.files.internal("skins/ui.atlas"));
        stage = new Stage();
        stage.setDebugAll(true);
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

        buildUI();
    }

    private void buildUI() {
        maybeLoadTexture();
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
                for (File imageFile :animationDir.listFiles(pngFilter)){
                    frames.add(new AnimagicTextureRegion(new TextureRegion(new Texture(Gdx.files.absolute(imageFile.getAbsolutePath()))), new Texture(0, 0, Pixmap.Format.RGBA8888)));
                }
                File metaFile = new File(animationDir, "meta");
                if (metaFile.exists()) {
                    AnimagicAnimationData metaData = FileUtils.loadFileAs(AnimagicAnimationData.class, metaFile);
                    for (int i = 0; i < frames.size(); i++) {
                        if (metaData.frameData.size() > i) {
                            frames.get(i).meta = metaData.frameData.get(i);
                        }
                    }
                }
            }
        }
        System.out.println("Found " + frames.size() + " frames");
        if (frames.size() > 0) {
            newAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f), frames.toArray(new TextureRegion[frames.size()]));
        } else {
            newAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f), new TextureRegion[] {new TextureRegion(new Texture(0, 0, Pixmap.Format.RGBA8888))});
        }
        currentAnimation = newAnimation;
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
                    for (AnimagicTextureRegion frame : currentAnimation.getAllFrames()) {
                        animationData.frameData.add(frame.meta);
                    }
                    FileUtils.saveToFile(animationData, Paths.get(currentAnimationDir, "meta").toString());
                }
            }
        });

        TextButton centerButton = new TextButton("center", skin, "button");
        centerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for(AnimagicTextureRegion frame : currentAnimation.getAllFrames()) {
                    frame.meta.xOffset = -frame.getRegionWidth()/2;
                    frame.meta.yOffset = -frame.getRegionHeight()/2;
                }
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

            for (AnimagicTextureRegion frame : currentAnimation.getAllFrames()) {
                Table itemTable = new Table();
                TextureRegionDrawable upDrawable = new TextureRegionDrawable(frame);
                SpriteDrawable downSprite = upDrawable.tint(Color.GREEN);
                ImageButton button = new ImageButton(upDrawable, downSprite, downSprite);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectedRegion = frame;
                    }
                });
                buttonGroup.add(button);
                itemTable.add(button).width(64).height(64).padBottom(40).padTop(20);
                itemTable.row();

                menu.add(itemTable).padRight(20);
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
        AnimagicTextureRegion frame = currentAnimation.getFrame();
        batch.draw(frame, frame.meta.xOffset, frame.meta.yOffset);
        batch.flush();
        batch.end();
        ScissorStack.popScissors();

        shaper.setProjectionMatrix(animationCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.WHITE);
        shaper.rect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        shaper.setColor(Color.PINK);
        shaper.circle(0, 0, 1);
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
        batch.draw(selectedRegion, selectedRegion.meta.xOffset, selectedRegion.meta.yOffset);
        batch.flush();
        batch.end();
        ScissorStack.popScissors();

        shaper.setProjectionMatrix(textureEditCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.WHITE);
        shaper.rect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        shaper.setColor(Color.PINK);
        shaper.circle(0, 0, 1);
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
        if (editorPanelArea.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
            System.out.println("CLICKED THE EDITOR");
        }
        if (animatorPanelArea.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
            System.out.println("CLICKED THE ANIMATOR");
        }
        System.out.println(animationCamera.unproject(new Vector3(screenX, screenY, 0)));
        return false;
    }
}
