package com.bytebreak.animagic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.junit.Assert;
import org.junit.Test;

public class AnimationTest {

    @Test
    public void testAnimationConstructors(){
        try {
            new Animation(null, Animation.AnimationPlayState.ONCE, 0, null, null);
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when name is null", e.getMessage().contains("Animation.name"));
        }
        try {
            new Animation("", Animation.AnimationPlayState.ONCE, 0, null, null);
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when name is empty", e.getMessage().contains("Animation.name"));
        }
        try {
            new Animation("animationName", Animation.AnimationPlayState.ONCE, 0, null, null);
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when textures is null", e.getMessage().contains("Animation.textures"));
        }
        try {
            new Animation("animationName", Animation.AnimationPlayState.ONCE, 0, new TextureRegion[0], null);
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when textures is empty", e.getMessage().contains("Animation.textures"));
        }
        try {
            TextureRegion t = new TextureRegion();
            new Animation("animationName", Animation.AnimationPlayState.ONCE, 0, new TextureRegion[]{ t }, null);
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when keyframes is null", e.getMessage().contains("Animation.keyframes"));
        }
    }

    @Test
    public void testAnimationGetFrameONCE(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, 10, new TextureRegion[]{t1, t2, t3});

        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 1
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(2); // 3
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 4
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(2); // 6
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(3); // 9
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(1); // 10
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(1); // 11 -> 10
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(1000); // 1010 -> 10
        Assert.assertEquals(2, a.getFrameIndex());
    }

    @Test
    public void testAnimationGetFrameREPEAT(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.REPEAT, 10, new TextureRegion[]{t1, t2, t3});

        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 1
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(2); // 3
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 4
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(2); // 6
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(3); // 9
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(1); // 10
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 11 -> 0
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(5); // 5
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(1000); // 1005 -> 0
        Assert.assertEquals(0, a.getFrameIndex());
    }

    @Test
    public void testAnimationGetFramePINGPONG(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.PINGPONG, 10, new TextureRegion[]{t1, t2, t3});

        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 1
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(2); // 3
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(1); // 4
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(2); // 6
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(3); // 9
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(1); // 10
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(1); // 9
        Assert.assertEquals(2, a.getFrameIndex());
        a.update(5); // 4
        Assert.assertEquals(1, a.getFrameIndex());
        a.update(1000); // 1005 -> 0
        Assert.assertEquals(0, a.getFrameIndex());
    }

    @Test
    public void testAnimationGetFrameReset(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, 10, new TextureRegion[]{t1, t2, t3});

        Assert.assertEquals(0, a.getFrameIndex());
        a.update(10);
        Assert.assertEquals(2, a.getFrameIndex());
        a.reset();
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(4);
        Assert.assertEquals(1, a.getFrameIndex());
    }
}
