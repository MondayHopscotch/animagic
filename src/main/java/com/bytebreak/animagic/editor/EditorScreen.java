package com.bytebreak.animagic.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    OrthographicCamera camera;
    private final Stage stage;
    private final Skin skin;

    private final SpriteBatch batch;

    private Actor animationPanel;
    private Animation currentAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f), new TextureRegion[] {new TextureRegion(new Texture(0, 0, Pixmap.Format.RGBA8888))});

    private TextureRegion testTexture;

    public EditorScreen() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch(1);

        testTexture = new TextureRegion(new Texture(Gdx.files.internal("skins/ui.png")));

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
        buildLoadSavePanel();
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

    private void buildLoadSavePanel() {
        Table parentMenu = new Table();
        parentMenu.setFillParent(true);
        parentMenu.setOrigin(Align.topLeft);
        parentMenu.align(Align.topLeft);

        Table menu = new Table();

        Table itemTable = new Table();
        TextButton loadButton = new TextButton("load", skin, "button");
        itemTable.add(loadButton);

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
        itemTable.add(saveButton);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SAVING");
            }
        });

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

        parentMenu.add(scrollPane);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.setUncheckLast(true);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);

            for (TextureRegion frame : currentAnimation.getAllFrames()) {
                Table itemTable = new Table();
                TextureRegionDrawable upDrawable = new TextureRegionDrawable(frame);
                SpriteDrawable downSprite = upDrawable.tint(Color.GREEN);
                ImageButton button = new ImageButton(upDrawable, downSprite, downSprite);
                buttonGroup.add(button);
                itemTable.add(button).padBottom(40).padTop(20);
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
        parentMenu.align(Align.right);

        Table menu = new Table();
        menu.setSize(200, 300);

        Table itemTable = new Table();
        TextButton button = new TextButton("", skin, "button");
        itemTable.add(button).width(200).height(300);

        animationPanel = button;

        menu.add(itemTable);
        menu.row();
        menu.padBottom(-20);
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
            Vector2 center = new Vector2(Gdx.graphics.getWidth(), 0);
            Vector2 size = new Vector2(animationPanel.getWidth(), animationPanel.getHeight());
            center.sub(animationPanel.localToStageCoordinates(new Vector2(0, 0)).sub(size.scl(.5f)));

            camera.position.set(-1 * (center.x), -1 * (center.y), 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            Rectangle scissors = new Rectangle();
            Rectangle clipBounds = new Rectangle(-100, -150, 200, 300);
            ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
            ScissorStack.pushScissors(scissors);
            currentAnimation.update(delta);

            batch.begin();
            AnimagicTextureRegion frame = currentAnimation.getFrame();
            batch.draw(frame, frame.meta.xOffset, frame.meta.yOffset);
            batch.flush();
            batch.end();
            ScissorStack.popScissors();
        }

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
        System.out.println(camera.unproject(new Vector3(screenX, screenY, 0)));
        return false;
    }
}
