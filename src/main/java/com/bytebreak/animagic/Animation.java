package com.bytebreak.animagic;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bytebreak.animagic.texture.AnimagicTextureRegion;

import java.util.ArrayList;
import java.util.List;

public class Animation implements IFrameByFrameAnimation {
    private String name;
    private List<AnimationListener> listeners = new ArrayList<>();
    private AnimagicTextureRegion[] textures;
    private int[] keyframes;
    private final float totalFrameRateDuration;
    private final float perFrameRateDuration;
    private float currentDuration = 0;
    private AnimationPlayState playState = AnimationPlayState.ONCE;
    private float playDirectionModifier = 1;
    private boolean finishedPlaying = false;
    private float percentagePerFrame;

    public Animation(String name, AnimationPlayState playState, FrameRate frameRate, TextureRegion[] textureArray, int[] keyframes) {
        if (name == null) throw new AnimagicException("Animation.name cannot be null");
        if (name.trim().equalsIgnoreCase("")) throw new AnimagicException("Animation.name cannot be ''");
        if (frameRate == null) throw new AnimagicException("Animation.frameRate cannot be null");
        if (textureArray == null) throw new AnimagicException("Animation.textures cannot be null");
        if (textureArray.length == 0) throw new AnimagicException("Animation.textures cannot be empty");
        if (keyframes == null) throw new AnimagicException("Animation.keyframes cannot be null");

        this.name = name;
        this.playState = playState;

        List<AnimagicTextureRegion> animTextures = new ArrayList<>();
        for (TextureRegion region : textureArray) {
            if (region instanceof AnimagicTextureRegion) {
                animTextures.add((AnimagicTextureRegion) region);
            } else {
                animTextures.add(new AnimagicTextureRegion(region, new Texture(0, 0, Pixmap.Format.RGBA8888)));
            }
        }
        this.textures = animTextures.toArray(new AnimagicTextureRegion[animTextures.size()]);
        this.keyframes = keyframes;

        if (frameRate.total()) {
            this.totalFrameRateDuration = frameRate.seconds();
            this.perFrameRateDuration = frameRate.seconds() / textures.length;
        } else {
            this.totalFrameRateDuration = frameRate.seconds() * textures.length;
            this.perFrameRateDuration = frameRate.seconds();
        }

        percentagePerFrame = 1f / textures.length;
    }

    public Animation(String name, AnimationPlayState playState, FrameRate frameRate, TextureRegion[] textureArray) {
        this(name, playState, frameRate, textureArray, new int[0]);
    }

    public String name(){
        return name;
    }

    public float totalDuration() {
        return totalFrameRateDuration;
    }

    public float perFrameDuration() {
        return perFrameRateDuration;
    }

    public int totalFrames(){
        return textures.length;
    }

    @Override
    public void reset(){
        currentDuration = 0;
        finishedPlaying = false;
    }

    public Animation setFrameIndex(int index) {
        if (index < 0) throw new AnimagicException("Cannot set the frame index to less than 0");
        if (index >= totalFrames()) throw new AnimagicException("Cannot set the frame index to more than the totalFrames(" + totalFrames() + ") - 1");
        currentDuration = (percentagePerFrame * index) * totalDuration();
        return this;
    }

    @Override
    public void update(float delta){
        currentDuration += delta * playDirectionModifier;
        if (currentDuration >= totalDuration() || currentDuration <= 0) {
            switch (playState){
                case ONCE:
                    currentDuration = totalDuration();
                    if (!finishedPlaying){
                        notify(AnimationListenerState.FINISHED);
                    }
                    finishedPlaying = true;
                    break;
                case REPEAT:
                    currentDuration = 0;
                    notify(AnimationListenerState.REPEATED);
                    break;
                case PINGPONG:
                    if (playDirectionModifier == 1){
                        currentDuration = totalDuration();
                        notify(AnimationListenerState.PINGED);
                    } else {
                        currentDuration = 0;
                        notify(AnimationListenerState.PONGED);
                    }
                    playDirectionModifier *= -1;
                    break;
            }
        }

        if (keyframes != null){
            int curFrame = getFrameIndex();
            boolean isKeyframe = false;
            for (int keyframe : keyframes){
                if (keyframe == curFrame){
                    isKeyframe = true;
                    break;
                }
            }
            if (isKeyframe) notify(AnimationListenerState.KEYFRAME);
        }
    }

    @Override
    public int getFrameIndex(){
        if (percentagePerFrame <= 0) return 0;
        float percComp = percentComplete();
        if (percComp == 1) return totalFrames() - 1;
        return (int)Math.floor(percComp / percentagePerFrame);
    }

    @Override
    public AnimagicTextureRegion getFrame(){
        return textures[getFrameIndex()];
    }

    public float percentComplete(){
        if (currentDuration == 0) return 0;
        else if (currentDuration >= totalDuration()) return 1;
        else return currentDuration / totalDuration();
    }

    public AnimagicTextureRegion[] getAllFrames() {
        return textures;
    }

    public Animation listen(AnimationListener listener) {
        if (listener == null){
            throw new AnimagicException("Animation listener cannot be null");
        }
        listeners.add(listener);
        return this;
    }

    private void notify(AnimationListenerState listenerState){
        for (AnimationListener listener : listeners) listener.animationNotification(this, listenerState);
    }

    public enum AnimationPlayState {
        REPEAT,
        PINGPONG,
        ONCE
    }

    public enum AnimationListenerState {
        FINISHED,
        PINGED,
        PONGED,
        REPEATED,
        KEYFRAME
    }
}
