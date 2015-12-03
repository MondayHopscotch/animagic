package com.bytebreak.animagic.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.bytebreak.animagic.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;

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

    private final TextureRegion testTexture;

    public EditorScreen() {
        camera = new OrthographicCamera(800, 600);
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
        inputMux.addProcessor(stage);
        inputMux.addProcessor(this);
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void show() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        buildUI();
    }

    private void buildUI() {
        stage.clear();
        buildAnimationPanel();
        buildFrameScrollPanel();
        buildLoadSavePanel();
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

        if (currentAnimationDir != null) {
            for (File imageFile : new File(currentAnimationDir).listFiles(pngFilter)) {
                Table itemTable = new Table();
                TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.absolute(imageFile.getAbsolutePath()))));
                SpriteDrawable downSprite = upDrawable.tint(Color.GREEN);
                ImageButton button = new ImageButton(upDrawable, downSprite, downSprite);
                buttonGroup.add(button);
                itemTable.add(button).padBottom(40).padTop(20);
                itemTable.row();

                menu.add(itemTable).padRight(20);
            }
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
        TextButton button = new TextButton("hello", skin, "button");
        itemTable.add(button);
        itemTable.row();
        Label label = new Label("hello label", skin);
        itemTable.add(label).padBottom(20);

        menu.add(itemTable).padRight(20);
        menu.row();
        menu.padBottom(-20);
        parentMenu.add(menu);

        stage.addActor(parentMenu);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(-200, -50, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(testTexture, 0, 0);
        batch.end();

        stage.act(delta);
        stage.draw();
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
}
