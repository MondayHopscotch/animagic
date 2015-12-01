package com.bytebreak.animagic.integration.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bytebreak.animagic.*;
import com.bytebreak.animagic.texture.BitTextureAtlas;

public class Character implements AnimationListener {
    float x = 0;
    float y = 0;
    float velx = 0;
    float vely = 0;
    Animator animator;
    boolean facingLeft = false;
    State currentState = State.NEUTRAL;
    String lastAttack = null;

    String[] combos = new String[]{"kick", "thrust", "bash", "cut", "slash", "smash"};

    public Character(BitTextureAtlas atlas) {
        animator = new Animator("character");

        animator.addAnimation(new Animation("kick", Animation.AnimationPlayState.ONCE, FrameRate.total(0.75f), atlas.findRegionsWithMeta("kick").toArray(TextureRegion.class), new int[]{6}).listen(this));
        animator.addAnimation(new Animation("thrust", Animation.AnimationPlayState.ONCE, FrameRate.total(0.75f), atlas.findRegionsWithMeta("thrust").toArray(TextureRegion.class), new int[]{6}).listen(this));
        animator.addAnimation(new Animation("bash", Animation.AnimationPlayState.ONCE, FrameRate.total(0.75f), atlas.findRegionsWithMeta("bash").toArray(TextureRegion.class), new int[]{6}).listen(this));
        animator.addAnimation(new Animation("cut", Animation.AnimationPlayState.ONCE, FrameRate.total(0.75f), atlas.findRegionsWithMeta("cut").toArray(TextureRegion.class), new int[]{6}).listen(this));
        animator.addAnimation(new Animation("slash", Animation.AnimationPlayState.ONCE, FrameRate.total(0.75f), atlas.findRegionsWithMeta("slash").toArray(TextureRegion.class), new int[]{6}).listen(this));
        animator.addAnimation(new Animation("smash", Animation.AnimationPlayState.ONCE, FrameRate.total(0.75f), atlas.findRegionsWithMeta("smash").toArray(TextureRegion.class), new int[]{6}).listen(this));

        animator.addAnimation(new Animation("jump", Animation.AnimationPlayState.ONCE, FrameRate.total(1.0f), atlas.findRegionsWithMeta("jump").toArray(TextureRegion.class)));
        animator.addAnimation(new Animation("throw", Animation.AnimationPlayState.ONCE, FrameRate.total(1.0f), atlas.findRegionsWithMeta("throw").toArray(TextureRegion.class)).listen(this));
        animator.addAnimation(new Animation("climb", Animation.AnimationPlayState.REPEAT, FrameRate.total(1.0f), atlas.findRegionsWithMeta("climb").toArray(TextureRegion.class)));
        animator.addAnimation(new Animation("sprint", Animation.AnimationPlayState.REPEAT, FrameRate.total(0.75f), atlas.findRegionsWithMeta("sprint").toArray(TextureRegion.class)));
        animator.addAnimation(new Animation("push", Animation.AnimationPlayState.REPEAT, FrameRate.total(1.75f), atlas.findRegionsWithMeta("push").toArray(TextureRegion.class)));
        animator.addAnimation(new Animation("stand", Animation.AnimationPlayState.ONCE, FrameRate.total(0f), new TextureRegion[]{atlas.findRegionsWithMeta("pose").get(3)}));
        animator.addAnimation(new AnimationBlend(6).addAnimation(
                new Animation("walk", Animation.AnimationPlayState.REPEAT, FrameRate.total(1f), atlas.findRegionsWithMeta("walk").toArray(TextureRegion.class))).addAnimation(
                new Animation("run", Animation.AnimationPlayState.REPEAT, FrameRate.total(0.5f), atlas.findRegionsWithMeta("run").toArray(TextureRegion.class))).addAnimation(
                new Animation("sneak", Animation.AnimationPlayState.REPEAT, FrameRate.total(1.25f), atlas.findRegionsWithMeta("sneak").toArray(TextureRegion.class))).addAnimation(
                new Animation("crawl", Animation.AnimationPlayState.REPEAT, FrameRate.total(1.25f), atlas.findRegionsWithMeta("crawl").toArray(TextureRegion.class))).addAnimation(
                new Animation("runpistol", Animation.AnimationPlayState.REPEAT, FrameRate.total(0.5f), atlas.findRegionsWithMeta("runpistol").toArray(TextureRegion.class))).addAnimation(
                new Animation("runpistolaim", Animation.AnimationPlayState.REPEAT, FrameRate.total(0.5f), atlas.findRegionsWithMeta("runpistolaim").toArray(TextureRegion.class))).addAnimation(
                new Animation("runrifle", Animation.AnimationPlayState.REPEAT, FrameRate.total(0.5f), atlas.findRegionsWithMeta("runrifle").toArray(TextureRegion.class))).addAnimation(
                new Animation("runrifleaim", Animation.AnimationPlayState.REPEAT, FrameRate.total(0.5f), atlas.findRegionsWithMeta("runrifleaim").toArray(TextureRegion.class))));

        animator.switchToAnimation("stand");
    }

    public void update(float delta) {
        animator.update(delta);
        if (velx > 0) facingLeft = false;
        if (velx < 0) facingLeft = true;

        vely -= 0.1f;

        x += velx;
        y += vely;

        if (currentState == State.AIRBORNE && y < 0) {
            currentState = State.STILL;
            animator.switchToAnimation("stand");
        }

        if (y < 0) {
            y = 0;
            vely = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) attack();

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) jump();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) move(-4);
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) move(4);
        else if (currentState == State.NEUTRAL || currentState == State.STILL) move(0);
    }

    public void draw(SpriteBatch spriteBatch) {
        TextureRegion frame = animator.getFrame();
        spriteBatch.draw(frame, x, y, (facingLeft ? frame.getRegionWidth() : 0), 0, frame.getRegionWidth() * 3, frame.getRegionHeight() * 3, (facingLeft ? -1 : 1), 1, 0);
    }

    public void attack() {
        if (currentState == State.NEUTRAL || currentState == State.STILL) {
            currentState = State.ATTACK;
            lastAttack = null;
            animator.switchToAnimation(combos[0]);
        } else if (currentState == State.COMBOAVAILABLE) {
            for (int i = 0; i < combos.length; i++) {
                if (combos[i].equalsIgnoreCase(lastAttack)) {
                    if (i + 1 < combos.length) {
                        animator.switchToAnimation(combos[i + 1]);
                        currentState = State.ATTACK;
                    }
                    break;
                }
            }
        }
    }

    public void move(int direction) {
        if (currentState == State.STILL) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                animator.switchToAnimation("run");
            } else {
                animator.switchToAnimation("walk");
            }
            currentState = State.NEUTRAL;
        }
        if (currentState == State.NEUTRAL) {
            velx = direction;
        }
        if (direction == 0) {
            currentState = State.STILL;
            animator.switchToAnimation("stand");
        }
    }

    public void jump() {
        if (currentState == State.NEUTRAL || currentState == State.STILL) {
            vely = 5;
            currentState = State.AIRBORNE;
            animator.switchToAnimation("jump");
        }
    }

    @Override
    public void animationNotification(Animation self, Animation.AnimationListenerState listenerState) {
        if (listenerState == Animation.AnimationListenerState.KEYFRAME) {
            lastAttack = self.name();
            currentState = State.COMBOAVAILABLE;
        }
        if (listenerState == Animation.AnimationListenerState.FINISHED) {
            animator.switchToAnimation("stand");
            currentState = State.NEUTRAL;
        }
    }

    public enum State {
        STILL,
        NEUTRAL,
        ATTACK,
        COMBOAVAILABLE,
        AIRBORNE
    }
}
