package com.bytebreakstudios.animagic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.junit.Assert;
import org.junit.Test;

public class AnimationBlendTest {

    public AnimationBlend newAnimationBlend(){
        AnimationBlend b = new AnimationBlend(3);
        b.addAnimation(new Animation("one", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{new TextureRegion(), new TextureRegion(), new TextureRegion()}));
        b.addAnimation(new Animation("two", Animation.AnimationPlayState.ONCE, FrameRate.total(15), new TextureRegion[]{new TextureRegion(), new TextureRegion(), new TextureRegion()}));
        b.addAnimation(new Animation("three", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{new TextureRegion(), new TextureRegion(), new TextureRegion()}));
        return b;
    }

    @Test
    public void testAddAnimation(){
        AnimationBlend b = newAnimationBlend();
        try{
            b.addAnimation(null);
            Assert.fail("Should have failed when animation is null");
        } catch (AnimagicException e){
            Assert.assertTrue(e.getMessage().contains("null animation"));
        }
        try{
            b.addAnimation(new Animation("four", Animation.AnimationPlayState.ONCE, FrameRate.total(10), new TextureRegion[]{new TextureRegion(), new TextureRegion()}));
            Assert.fail("Should have failed when animation has the wrong number of frames");
        } catch (AnimagicException e){
            Assert.assertTrue(e.getMessage().contains("frames"));
        }
    }

    @Test
    public void testHasAnimation(){
        AnimationBlend b = newAnimationBlend();
        Assert.assertTrue(b.hasAnimation("one"));
        Assert.assertTrue(b.hasAnimation("two"));
        Assert.assertTrue(b.hasAnimation("three"));
        Assert.assertFalse(b.hasAnimation("four"));
    }

    @Test
    public void testSwitchAnimation(){
        AnimationBlend b = newAnimationBlend();
        b.switchToAnimation("one");
        b.switchToAnimation("three");
        b.switchToAnimation("two");
        try{
            b.switchToAnimation("four");
            Assert.fail();
        } catch (AnimagicException e){
            Assert.assertTrue(e.getMessage().contains("does not have"));
        }
    }

    @Test
    public void testGetFrameIndex(){
        AnimationBlend b = newAnimationBlend();
        Assert.assertEquals(0, b.getFrameIndex());
        b.update(2);
        Assert.assertEquals(0, b.getFrameIndex());
        b.switchToAnimation("one");
        Assert.assertEquals(0, b.getFrameIndex());
        b.update(4);
        Assert.assertEquals(1, b.getFrameIndex());
        b.switchToAnimation("three");
        Assert.assertEquals(1, b.getFrameIndex());
        b.update(2);
        Assert.assertEquals(1, b.getFrameIndex());
        b.switchToAnimation("two");
        Assert.assertEquals(1, b.getFrameIndex());
        b.update(4);
        Assert.assertEquals(1, b.getFrameIndex());
        b.update(5);
        Assert.assertEquals(2, b.getFrameIndex());
    }
}
