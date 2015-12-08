package com.bytebreak.animagic.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
    OrthographicCamera mainCamera;

    FrameBuffer animationBuffer = getNewFrameBuffer();
    FrameBuffer textureEditBuffer = getNewFrameBuffer();

    private final Stage stage;
    private final Skin skin;

    private final SpriteBatch batch;
    private final ShapeRenderer shaper;


    private Actor animationPanel;
    private Actor scrollPanel;
    private Actor buttonPanel;

    private Rectangle animatorPanelArea = new Rectangle();
    private Rectangle editorPanelArea = new Rectangle();

    private Vector2 animatorPanelAreaOffset = new Vector2();
    private Vector2 editorPanelAreaOffset = new Vector2();

    private Animation currentAnimation = new Animation("editorAnimation", Animation.AnimationPlayState.REPEAT, FrameRate.perFrame(.1f), new TextureRegion[] {new TextureRegion(new Texture(0, 0, Pixmap.Format.RGBA8888))});

    public EditorScreen() {
        animationCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textureEditCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mainCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mainCamera.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0);
        mainCamera.update();

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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        currentAnimation.update(delta);

        if (animationPanel != null) {
            renderAnimationPanel();
        }

        if (selectedRegion != null) {
            renderSelectedRegion();
        }

        renderFinal();
    }

    private void renderFinal() {
        batch.setProjectionMatrix(mainCamera.combined);
        batch.begin();
        if (animationBuffer != null) {
            batch.draw(animationBuffer.getColorBufferTexture(), animatorPanelAreaOffset.x, Gdx.graphics.getHeight() + animatorPanelAreaOffset.y, Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        }
        if (textureEditBuffer != null) {
            batch.draw(textureEditBuffer.getColorBufferTexture(), editorPanelAreaOffset.x, Gdx.graphics.getHeight() + editorPanelAreaOffset.y, Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        }
        batch.end();

        shaper.setProjectionMatrix(mainCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.GREEN);
        shaper.rect(animatorPanelArea.x, animatorPanelArea.y, animatorPanelArea.width, animatorPanelArea.height);
        shaper.rect(editorPanelArea.x, editorPanelArea.y, editorPanelArea.width, editorPanelArea.height);
        shaper.end();
    }

    private void renderAnimationPanel() {
        Vector2 animatorPanelLoc = animationPanel.localToStageCoordinates(new Vector2(0, 0));

        Vector2 cameraAreaSize = new Vector2();
        cameraAreaSize.y = Math.abs(animationPanel.getHeight()); // distance from top of scroll panel to bottom of button panel
        cameraAreaSize.x = Math.abs(animationPanel.getWidth());

        Vector2 regionCamCenterPoint = new Vector2(0, 0);
        regionCamCenterPoint.x = Gdx.graphics.getWidth() - cameraAreaSize.x / 2; // this camera is rendering up against the right side of the screen
        regionCamCenterPoint.y = animatorPanelLoc.y + cameraAreaSize.y/2;

        Vector2 texturePanelLocation = new Vector2(-1 * (Gdx.graphics.getWidth()/2 - regionCamCenterPoint.x), -1 * (Gdx.graphics.getHeight()/2 - regionCamCenterPoint.y));
        animatorPanelAreaOffset.set(texturePanelLocation.x, texturePanelLocation.y);

        batch.setProjectionMatrix(animationCamera.combined);
        Rectangle scissors = new Rectangle();
        Vector3 bottomLeft = animationCamera.unproject(new Vector3(-cameraAreaSize.x / 2, -cameraAreaSize.y / 2, 0).add(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0));
        Vector3 topRight = animationCamera.unproject(new Vector3(cameraAreaSize.x / 2, cameraAreaSize.y / 2, 0).add(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0));
        Rectangle clipBounds = new Rectangle(bottomLeft.x, bottomLeft.y, topRight.x - bottomLeft.x, topRight.y - bottomLeft.y);
        ScissorStack.calculateScissors(animationCamera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);

        animatorPanelArea.set(scissors).setCenter(regionCamCenterPoint);

        animationBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
        batch.begin();
        AnimagicTextureRegion frame = currentAnimation.getFrame();
        batch.draw(frame, frame.meta.xOffset, frame.meta.yOffset);
        batch.flush();
        batch.end();

        shaper.setProjectionMatrix(animationCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.WHITE);
        shaper.rect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        shaper.setColor(Color.PINK);
        shaper.rect(0, 0, 1, 1);
        shaper.end();
        ScissorStack.popScissors();
        animationBuffer.end();
    }

    private void renderSelectedRegion() {
        Vector2 animatorPanelLoc = animationPanel.localToStageCoordinates(new Vector2(0, 0));
        Vector2 buttonPanelLoc = buttonPanel.localToStageCoordinates(new Vector2(0, 0));

        Vector2 cameraAreaSize = new Vector2();
        cameraAreaSize.y = Math.abs(buttonPanelLoc.y - scrollPanel.getHeight()); // distance from top of scroll panel to bottom of button panel
        cameraAreaSize.x = Math.abs(animatorPanelLoc.x);

        Vector2 regionCamCenterPoint = new Vector2(0, 0);
        regionCamCenterPoint.x = cameraAreaSize.x / 2; // this camera is rendering up against the left side of the screen
        regionCamCenterPoint.y = scrollPanel.getHeight() + cameraAreaSize.y/2;

        Vector2 texturePanelLocation = new Vector2(-1 * (Gdx.graphics.getWidth()/2 - regionCamCenterPoint.x), -1 * (Gdx.graphics.getHeight()/2 - regionCamCenterPoint.y));
        editorPanelAreaOffset.set(texturePanelLocation.x, texturePanelLocation.y);

        textureEditBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(textureEditCamera.combined);
        Rectangle scissors = new Rectangle();
        Vector3 bottomLeft = textureEditCamera.unproject(new Vector3(-cameraAreaSize.x / 2, -cameraAreaSize.y / 2, 0).add(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0));
        Vector3 topRight = textureEditCamera.unproject(new Vector3(cameraAreaSize.x / 2, cameraAreaSize.y / 2, 0).add(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0));
        Rectangle clipBounds = new Rectangle(bottomLeft.x, bottomLeft.y, topRight.x - bottomLeft.x, topRight.y - bottomLeft.y);
        ScissorStack.calculateScissors(textureEditCamera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);

        editorPanelArea.set(scissors).setCenter(regionCamCenterPoint);
        batch.begin();
        batch.draw(selectedRegion, 0, 0);
        batch.flush();
        batch.end();

        shaper.setProjectionMatrix(textureEditCamera.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.WHITE);
        shaper.rect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        shaper.rect(selectedRegion.getOffset().x, selectedRegion.getOffset().y, selectedRegion.getRegionWidth(), selectedRegion.getRegionHeight());
        shaper.setColor(Color.PINK);
        shaper.rect(selectedRegion.getOffset().x, selectedRegion.getOffset().y, 1, 1);
        shaper.end();
        ScissorStack.popScissors();
        textureEditBuffer.end();
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

    private Vector2 mousePosition = new Vector2();

    private Vector2 touchPoint = new Vector2();
    private Vector2 editorTouchPoint = new Vector2();
    private Vector2 animationTouchPoint = new Vector2();

    private Vector2 lastPosition = new Vector2();

    private Vector2 effectiveEditorTouchPoint = new Vector2();

    private Vector3 initialCamPosition = new Vector3();
    private Vector3 touchedCameraPosition = new Vector3();

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set(screenToMainCamCoords(screenX, screenY));
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateTouchPoints(screenX, screenY);
//        Vector2 twoDimension = new Vector2(screenX, screenY).add(editorPanelArea.x, editorPanelArea.y).sub(editorPanelAreaOffset);
        Vector2 twoDimension = new Vector2(screenX, screenY).sub(editorPanelArea.getCenter(new Vector2()));
//        Vector2 twoDimension = new Vector2(screenX, screenY).sub(editorPanelAreaOffset);
        Vector3 unProject = textureEditCamera.unproject(new Vector3(twoDimension, 0));
        effectiveEditorTouchPoint.set(unProject.x, unProject.y);
        System.out.println("TouchPoint " + touchPoint);
        lastPosition.set(touchPoint);

        if (editorPanelArea.contains(touchPoint)) {
            initialCamPosition.set(textureEditCamera.position);
            touchedCameraPosition.set(editorTouchPoint, 0);
        }
        if (animatorPanelArea.contains(touchPoint)) {
            initialCamPosition.set(animationCamera.position);
            touchedCameraPosition.set(animationTouchPoint, 0);
        }
        return false;
    }

    private void updateTouchPoints(int screenX, int screenY) {
        touchPoint.set(screenToMainCamCoords(screenX, screenY));
        editorTouchPoint.set(screenToEditorCamCoords(screenX, screenY));
        animationTouchPoint.set(screenToAnimationCamCoords(screenX, screenY));
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (editorPanelArea.contains(touchPoint)) {
            if (selectedRegion != null) {
                Vector2 releasePoint = screenToEditorCamCoords(screenX, screenY);
                if (releasePoint.sub(touchPoint).len() <= 1) {
                    selectedRegion.meta.xOffset = (int) effectiveEditorTouchPoint.x;
                    selectedRegion.meta.yOffset = (int) effectiveEditorTouchPoint.y;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        OrthographicCamera camToZoom = null;
//        if (editorPanelArea.contains(mousePosition)) {
//            camToZoom = textureEditCamera;
//        } else if (animatorPanelArea.contains(mousePosition)) {
//            camToZoom = animationCamera;
//        }
//        if (camToZoom != null) {
//            camToZoom.position.set(initialCamPosition);
//            camToZoom.update();
//            Vector3 currentCamCoords = camToZoom.unproject(new Vector3(screenX, screenY, 0));
//            Vector3 diff = new Vector3(touchedCameraPosition).sub(currentCamCoords);
////            System.out.println(initialCamPosition);
////            System.out.println("  " + currentCamCoords);
////            System.out.println("    " + diff);
//            camToZoom.position.add(diff);
//            camToZoom.update();
//        }
        return false;
    }

    private Vector2 screenToMainCamCoords(int screenX, int screenY) {
        Vector3 mainCamTouchPoint = mainCamera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(mainCamTouchPoint.x, mainCamTouchPoint.y);
    }

    private Vector2 screenToEditorCamCoords(int screenX, int screenY) {
        Vector2 offset = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        Vector2 correctedCenter = editorPanelArea.getCenter(new Vector2());
        correctedCenter.y = Gdx.graphics.getHeight() - correctedCenter.y;
        offset.sub(correctedCenter);
        System.out.println("screenCoords: " + new Vector2(screenX, screenY));
        System.out.println("editorOffset: " + offset);
        screenX += offset.x;
        screenY += offset.y;
        System.out.println("finalPosition: " + new Vector2(screenX, screenY));
        Vector3 editCamCoords = animationCamera.unproject(new Vector3(screenX, screenY, 0));
        System.out.println("unprojected: " + new Vector2(editCamCoords.x, editCamCoords.y));
//        editCamCoords.add(editorPanelAreaOffset.x, editorPanelAreaOffset.y, 0);
        return new Vector2(editCamCoords.x, editCamCoords.y);
    }

    private Vector2 screenToAnimationCamCoords(int screenX, int screenY) {
        screenX += animatorPanelAreaOffset.x;
        screenY += animatorPanelAreaOffset.y;
        Vector3 animCamCoords = animationCamera.unproject(new Vector3(screenX, screenY, 0));
//        animCamCoords.add(animatorPanelAreaOffset.x, animatorPanelAreaOffset.y, 0);
        return new Vector2(animCamCoords.x, animCamCoords.y);
    }

    public FrameBuffer getNewFrameBuffer() {
        return new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    @Override
    public boolean scrolled(int amount) {
        OrthographicCamera camToZoom = null;
        if (editorPanelArea.contains(mousePosition)) {
            camToZoom = textureEditCamera;
        } else if (animatorPanelArea.contains(mousePosition)) {
            camToZoom = animationCamera;
        }

        if (camToZoom != null) {
            camToZoom.zoom += amount / 10f;
            camToZoom.zoom = Math.max(.05f, camToZoom.zoom);
            camToZoom.update();
        }
        return false;
    }
}
