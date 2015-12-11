package com.bytebreakstudios.animagic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bytebreakstudios.animagic.animation.Animation;
import com.bytebreakstudios.animagic.animation.FrameRate;
import com.bytebreakstudios.animagic.utils.AnimagicException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AnimationTest {

    @Test
    public void testAnimationConstructors(){
        try {
            new Animation(null, Animation.AnimationPlayState.ONCE, FrameRate.total(0), null, null);
            Assert.fail();
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when name is null", e.getMessage().contains("Animation.name"));
        }
        try {
            new Animation("", Animation.AnimationPlayState.ONCE, FrameRate.total(0), null, null);
            Assert.fail();
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when name is empty", e.getMessage().contains("Animation.name"));
        }
        try {
            new Animation("animationName", Animation.AnimationPlayState.ONCE, FrameRate.total(0), null, null);
            Assert.fail();
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when textures is null", e.getMessage().contains("Animation.textures"));
        }
        try {
            new Animation("animationName", Animation.AnimationPlayState.ONCE, FrameRate.total(0), new TextureRegion[0], null);
            Assert.fail();
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when textures is empty", e.getMessage().contains("Animation.textures"));
        }
        try {
            TextureRegion t = new TextureRegion();
            new Animation("animationName", Animation.AnimationPlayState.ONCE, FrameRate.total(0), new TextureRegion[]{t}, null);
            Assert.fail();
        } catch (AnimagicException e){
            Assert.assertTrue("throw error when keyframes is null", e.getMessage().contains("Animation.keyframes"));
        }
    }

    @Test
    public void testAnimationGetFrameONCE(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});

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
        Animation a = new Animation("name", Animation.AnimationPlayState.REPEAT, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});

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
        Animation a = new Animation("name", Animation.AnimationPlayState.PINGPONG, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});

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
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});

        Assert.assertEquals(0, a.getFrameIndex());
        a.update(10);
        Assert.assertEquals(2, a.getFrameIndex());
        a.reset();
        Assert.assertEquals(0, a.getFrameIndex());
        a.update(4);
        Assert.assertEquals(1, a.getFrameIndex());
    }

    @Test
    public void testAnimationListenerFINISHED(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});
        final List<Animation.AnimationListenerState> states = new ArrayList<>();
        a.listen((self, state) -> states.add(state));
        a.update(20);
        Assert.assertTrue(states.contains(Animation.AnimationListenerState.FINISHED));
    }

    @Test
    public void testAnimationListenerREPEATED(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.REPEAT, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});
        final List<Animation.AnimationListenerState> states = new ArrayList<>();
        a.listen((self, state) -> states.add(state));
        a.update(20);
        Assert.assertTrue(states.contains(Animation.AnimationListenerState.REPEATED));
    }

    @Test
    public void testAnimationListenerPINGPONGED(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.PINGPONG, FrameRate.total(10), new TextureRegion[]{t1, t2, t3});
        final List<Animation.AnimationListenerState> states = new ArrayList<>();
        a.listen((self, state) -> states.add(state));
        a.update(20);
        a.update(20);
        Assert.assertTrue(states.contains(Animation.AnimationListenerState.PINGED));
        Assert.assertTrue(states.contains(Animation.AnimationListenerState.PONGED));
    }

    @Test
    public void testAnimationListenerKEYFRAME(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{t1, t2, t3}, new int[]{2});
        final List<Animation.AnimationListenerState> states = new ArrayList<>();
        a.listen((self, state) -> states.add(state));
        for (int i = 0; i < 10; i++) a.update(1);
        Assert.assertTrue(states.contains(Animation.AnimationListenerState.KEYFRAME));
    }

    @Test
    public void testAnimationSetFrameIndex(){
        TextureRegion t1 = new TextureRegion();
        TextureRegion t2 = new TextureRegion();
        TextureRegion t3 = new TextureRegion();
        Animation a = new Animation("name", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{t1, t2, t3}, new int[]{2});

        a.setFrameIndex(0);
        Assert.assertEquals(0, a.getFrameIndex());
        a.setFrameIndex(2);
        Assert.assertEquals(2, a.getFrameIndex());
        a.setFrameIndex(1);
        Assert.assertEquals(1, a.getFrameIndex());
        try {
            a.setFrameIndex(-1);
            Assert.fail("Should have thrown an error on setFrameIndex(-1)");
        } catch (AnimagicException e){
            Assert.assertTrue(e.getMessage().contains("less than 0"));
        }
        try {
            a.setFrameIndex(3);
            Assert.fail("Should have thrown an error on setFrameIndex(3)");
        } catch (AnimagicException e){
            Assert.assertTrue(e.getMessage().contains("more than the totalFrames"));
        }
    }
}
