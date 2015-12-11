package com.bytebreakstudios.animagic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bytebreakstudios.animagic.animation.Animation;
import com.bytebreakstudios.animagic.animation.AnimationBlend;
import com.bytebreakstudios.animagic.animation.Animator;
import com.bytebreakstudios.animagic.animation.FrameRate;
import com.bytebreakstudios.animagic.utils.AnimagicException;
import org.junit.Assert;
import org.junit.Test;

public class AnimatorTest {

    @Test
    public void testAnimatorConstructor() {
        try {
            new Animator(null);
            Assert.fail();
        } catch (AnimagicException e) {
            Assert.assertTrue(e.getMessage().contains("not be null"));
        }
        try {
            new Animator("");
            Assert.fail();
        } catch (AnimagicException e) {
            Assert.assertTrue(e.getMessage().contains("not be empty"));
        }
    }

    @Test
    public void testAnimatorAddAnimation() {
        Animator animator = new Animator("player");
        try {
            animator.addAnimation(null);
            Assert.fail();
        } catch (AnimagicException e) {
            Assert.assertTrue(e.getMessage().contains("player: "));
            Assert.assertTrue(e.getMessage().contains("null animation"));
        }
        Animation a = new Animation("run", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion()});
        animator.addAnimation(a);
    }

    @Test
    public void testAnimatorHasAnimation() {
        Animator animator = new Animator("player");
        animator.addAnimation(new Animation("run", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion()}));
        animator.addAnimation(new Animation("walk", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion()}));
        Assert.assertTrue(animator.hasAnimation("run"));
        Assert.assertTrue(animator.hasAnimation("walk"));
        Assert.assertFalse(animator.hasAnimation("idle"));
    }

    @Test
    public void testAnimatorMethodsBeforeSwitchAnimation() {
        Animator animator = new Animator("player");
        animator.addAnimation(new Animation("run", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion()}));
        animator.addAnimation(new Animation("walk", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion()}));
        try {
            animator.update(1);
            Assert.fail();
        } catch (AnimagicException e) {
            Assert.assertTrue(e.getMessage().contains("must call switchToAnimation"));
        }
        try {
            animator.getFrame();
            Assert.fail();
        } catch (AnimagicException e) {
            Assert.assertTrue(e.getMessage().contains("must call switchToAnimation"));
        }
    }


    @Test
    public void testAnimatorGetFrameWithSwitchAnimation() {
        Animator animator = new Animator("player");

        TextureRegion sprint1 = new TextureRegion();
        TextureRegion sprint2 = new TextureRegion();
        TextureRegion sprint3 = new TextureRegion();
        TextureRegion run1 = new TextureRegion();
        TextureRegion run2 = new TextureRegion();
        TextureRegion run3 = new TextureRegion();
        TextureRegion walk1 = new TextureRegion();
        TextureRegion walk2 = new TextureRegion();
        TextureRegion walk3 = new TextureRegion();

        Animation a = new Animation("sprint", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{sprint1, sprint2, sprint3});
        AnimationBlend b = new AnimationBlend(3);
        b.addAnimation(new Animation("run", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{run1, run2, run3}));
        b.addAnimation(new Animation("walk", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{walk1, walk2, walk3}));

        animator.addAnimation(a);
        animator.addAnimation(b);

        animator.switchToAnimation("run");
        Assert.assertEquals(run1, animator.getFrame());
        animator.switchToAnimation("sprint");
        Assert.assertEquals(sprint1, animator.getFrame());
        animator.switchToAnimation("walk");
        Assert.assertEquals(walk1, animator.getFrame());
    }

    @Test
    public void testAnimatorGetFrameWithSwitchAnimationAndUpdate() {
        Animator animator = new Animator("player");
        Animation a = new Animation("sprint", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion(), new TextureRegion(), new TextureRegion()});
        AnimationBlend b = new AnimationBlend(3);
        b.addAnimation(new Animation("run", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion(), new TextureRegion(), new TextureRegion()}));
        b.addAnimation(new Animation("walk", Animation.AnimationPlayState.REPEAT, FrameRate.total(3), new TextureRegion[]{new TextureRegion(), new TextureRegion(), new TextureRegion()}));

        animator.addAnimation(a);
        animator.addAnimation(b);

        animator.switchToAnimation("walk");
        Assert.assertEquals(0, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(1, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(2, animator.getFrameIndex());

        animator.switchToAnimation("run");
        Assert.assertEquals(2, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(0, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(1, animator.getFrameIndex());

        animator.switchToAnimation("sprint");
        Assert.assertEquals(0, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(1, animator.getFrameIndex());

        animator.switchToAnimation("sprint");
        Assert.assertEquals(0, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(1, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(2, animator.getFrameIndex());

        animator.switchToAnimation("walk");
        Assert.assertEquals(0, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(1, animator.getFrameIndex());
        animator.update(1);
        Assert.assertEquals(2, animator.getFrameIndex());
    }
}
