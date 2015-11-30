package com.bytebreak.animagic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class Animator {
    private final String name;
    private final List<IFrameByFrameAnimation> animations = new ArrayList<>();
    private String currentAnimationName = null;

    public Animator(String name) {
        if (name == null) throw new AnimagicException("Animator name cannot be null");
        if (name.trim().equals("")) throw new AnimagicException("Animator name cannot be empty");
        this.name = name;
    }

    public Animator addAnimation(IFrameByFrameAnimation animation) {
        if (animation == null) throw new AnimagicException(name + ": Cannot add a null animation");
        animations.add(animation);
        return this;
    }

    public boolean hasAnimation(String animationName){
        for (IFrameByFrameAnimation animation : animations){
            if (animation instanceof Animation){
                if (((Animation)animation).name().equalsIgnoreCase(animationName)) return true;
            } else if (animation instanceof AnimationBlend){
                if (((AnimationBlend)animation).hasAnimation(animationName)) return true;
            }
        }
        return false;
    }

    public Animator switchToAnimation(String animationName){
        if (!hasAnimation(animationName))
            throw new AnimagicException(name + ": Animator does not have an animation by the name: " + animationName);
        IFrameByFrameAnimation old = null;
        if (currentAnimationName != null) old = getAnimation();
        currentAnimationName = animationName;
        IFrameByFrameAnimation curAnimation = getAnimation();
        if (curAnimation instanceof Animation){
            curAnimation.reset();
        } else if (curAnimation instanceof AnimationBlend) {
            ((AnimationBlend)curAnimation).switchToAnimation(animationName);
            if (old != curAnimation) curAnimation.reset();
        }
        return this;
    }

    public Animator update(float delta){
        getAnimation().update(delta);
        return this;
    }
    public Animator draw(SpriteBatch spriteBatch){
        // TODO: somehow the actual size of the thing must get to this point, or maybe i just return a texture region?
        spriteBatch.draw(getFrame(), 0, 0);
        return this;
    }

    public TextureRegion getFrame() {
        return getAnimation().getFrame();
    }

    public int getFrameIndex() {
        return getAnimation().getFrameIndex();
    }

    public String currentAnimationName() {
        return currentAnimationName;
    }

    private IFrameByFrameAnimation getAnimation(){
        if (currentAnimationName == null)
            throw new AnimagicException(name + ": You must call switchToAnimation at least once before doing anything with Animator");
        for (IFrameByFrameAnimation animation : animations){
            if (animation instanceof Animation){
                if (((Animation)animation).name().equalsIgnoreCase(currentAnimationName)) return animation;
            } else if (animation instanceof AnimationBlend){
                if (((AnimationBlend)animation).hasAnimation(currentAnimationName)) return animation;
            }
        }
        throw new AnimagicException(name + ": This should never happen, it means the current animation name doesn't map to an animation");
    }
}
