package com.seek_knowledge.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Character extends Sprite {
    
    public enum State {Idle, Attack, Hurt};
    private State currentState;

    public World world;
    public Body body;
    private TextureAtlas atlas;
    private int health;

    private TextureRegion characterRegion;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private float stateTimer;

    public Character(World world, int health, String atlas, Float frameWidth, Float frameHeight, Float posX, Float posY) {
        this.world = world;
        defineCharacter(posX, posY);
        this.health = health;
        this.atlas = new TextureAtlas(atlas);

        idleAnimation = new Animation<>(0.5f, this.atlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        attackAnimation = new Animation<>(0.1f, this.atlas.findRegions("Attack"), Animation.PlayMode.NORMAL);
        hurtAnimation = new Animation<>(0.25f, this.atlas.findRegions("Hurt"), Animation.PlayMode.NORMAL);
        currentState = State.Idle;
        stateTimer = 0;

        characterRegion = idleAnimation.getKeyFrame(stateTimer);
        setBounds(2, 2, frameWidth, frameHeight);
        setRegion(characterRegion);
    }

    public TextureRegion getCharacterRegion() {
        return characterRegion;
    }

    public void update(float dt, Float posX, Float posY) {
        stateTimer += dt;

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
        setPosition(body.getPosition().x - getWidth() * posX, body.getPosition().y - getHeight() * posY);
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

    public void attack () {
        currentState = State.Attack;
        stateTimer = 0;
    }

    public void hurt() {
        currentState = State.Hurt;
        stateTimer = 0;
    }
}
