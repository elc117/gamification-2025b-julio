package com.seek_knowledge.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Character extends Sprite {

    public enum State {
        Idle, Attack, Hurt
    };

    private State currentState;
    private boolean pendingAnimation;
    private float delayedHurtTimer;
    private float hurtTimer = 0.15f;
    private float smoothX, smoothY;
    private float slideSpeed = 5f;

    public World world;
    public Body body;
    private TextureAtlas atlas;
    private int health;

    private TextureRegion characterRegion;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private float stateTimer;
    private float scale;

    public Character(World world, int health, String atlas, Float posX, Float posY, Float scale) {
        this.world = world;
        defineCharacter(posX, posY);
        this.health = health;
        this.atlas = new TextureAtlas(atlas);
        this.scale = scale;
        this.smoothX = posX;
        this.smoothY = posY;

        this.idleAnimation = new Animation<>(0.25f, this.atlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        this.attackAnimation = new Animation<>(0.15f, this.atlas.findRegions("Attack"), Animation.PlayMode.NORMAL);
        this.hurtAnimation = new Animation<>(0.25f, this.atlas.findRegions("Hurt"), Animation.PlayMode.NORMAL);
        this.currentState = State.Idle;
        this.stateTimer = 0;

        this.characterRegion = idleAnimation.getKeyFrame(stateTimer);
        setRegion(characterRegion);
    }

    public Character(World world, String atlas, Float posX, Float posy, Float scale) {
        this(world, 3, atlas, posX, posy, scale);
    }

    public TextureRegion getCharacterRegion() {
        return characterRegion;
    }

    public void update(float dt, Float posX, Float posY) {
        stateTimer += dt;

        if (pendingAnimation) {
            delayedHurtTimer += dt;

            if (delayedHurtTimer >= hurtTimer) {
                pendingAnimation = false;
                delayedHurtTimer = 0;
                currentState = State.Hurt;
                stateTimer = 0;
            }

        }

        switch (currentState) {
            case Attack:
                characterRegion = attackAnimation.getKeyFrame(stateTimer);
                if (attackAnimation.isAnimationFinished(stateTimer)) {
                    currentState = State.Idle;
                    stateTimer = 0;
                }
                break;
            case Hurt:
                characterRegion = hurtAnimation.getKeyFrame(stateTimer);
                if (hurtAnimation.isAnimationFinished(stateTimer)) {
                    currentState = State.Idle;
                    stateTimer = 0;
                }
                break;
            default:
                characterRegion = idleAnimation.getKeyFrame(stateTimer);
                break;
        }

        setRegion(characterRegion);

        float scale = this.scale;
        float frameWidth = characterRegion.getRegionWidth() / scale;
        float frameHeight = characterRegion.getRegionHeight() / scale;
        setSize(frameWidth, frameHeight);

        smoothX += (posX - smoothX) * dt * slideSpeed;
        smoothY += (posY - smoothY) * dt * slideSpeed;

        setPosition(
                body.getPosition().x - getWidth() / 2f + smoothX,
                body.getPosition().y - getHeight() / 4f + smoothY);
    }

    public void defineCharacter(Float posX, Float posY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(posX, posY);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public int getHealth() {
        return health;
    }

    public void attack() {
        currentState = State.Attack;
        stateTimer = 0;
    }

    public void hurt() {
        delayedHurtTimer = 0;
        pendingAnimation = true;
    }

    public boolean getState() {
        return currentState == State.Idle;
    }

    public void restoreLife() {
        this.health = 3;
    }

    public boolean isBusy() {
        return currentState == State.Attack || currentState == State.Hurt || pendingAnimation;
    }

}
