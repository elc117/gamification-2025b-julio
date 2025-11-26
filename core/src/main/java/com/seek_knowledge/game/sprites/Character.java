package com.seek_knowledge.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Character extends Sprite {

    // Estados possíveis da animação do personagem
    public enum State {
        Idle, Attack, Hurt
    };

    private State currentState; // Estado atual da animação
    private boolean pendingAnimation; // Marca se a animação de Hurt deve começar após um delay
    private float delayedHurtTimer; // Controle do delay para animação Hurt
    private float hurtTimer = 0.15f; // Delay antes da animação Hurt iniciar

    private float smoothX, smoothY; // Suavização de movimento
    private float slideSpeed = 5f; // Velocidade da interpolação suave

    public World world;
    public Body body;
    private TextureAtlas atlas;
    private int health;

    private TextureRegion characterRegion; // Frame atual da animação
    private Animation<TextureRegion> idleAnimation; // Animação Idle
    private Animation<TextureRegion> attackAnimation; // Animação Attack
    private Animation<TextureRegion> hurtAnimation; // Animação Hurt
    private float stateTimer; // Tempo dentro do estado atual
    private float scale; // Escala do personagem

    // Construtor completo
    public Character(World world, int health, String atlas, Float posX, Float posY, Float scale) {
        this.world = world;

        // Cria corpo Box2D do personagem
        defineCharacter(posX, posY);

        this.health = health;
        this.atlas = new TextureAtlas(atlas);
        this.scale = scale;

        // Valores iniciais de suavização
        this.smoothX = posX;
        this.smoothY = posY;

        // Carrega animações do atlas
        this.idleAnimation = new Animation<>(0.25f, this.atlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        this.attackAnimation = new Animation<>(0.15f, this.atlas.findRegions("Attack"), Animation.PlayMode.NORMAL);
        this.hurtAnimation = new Animation<>(0.25f, this.atlas.findRegions("Hurt"), Animation.PlayMode.NORMAL);

        this.currentState = State.Idle; // Começa parado
        this.stateTimer = 0; // Reseta timer de animação

        // Primeiro frame
        this.characterRegion = idleAnimation.getKeyFrame(stateTimer);
        setRegion(characterRegion);
    }

    // Sobrecarga: personagem padrão com vida = 3
    public Character(World world, String atlas, Float posX, Float posy, Float scale) {
        this(world, 3, atlas, posX, posy, scale);
    }

    public TextureRegion getCharacterRegion() {
        return characterRegion;
    }

    // Atualiza animação e posição suave
    public void update(float dt, Float posX, Float posY) {
        stateTimer += dt;

        // Controle de delay antes da animação Hurt
        if (pendingAnimation) {
            delayedHurtTimer += dt;

            // Só troca para Hurt depois do delay definido
            if (delayedHurtTimer >= hurtTimer) {
                pendingAnimation = false;
                delayedHurtTimer = 0;
                currentState = State.Hurt;
                stateTimer = 0;
            }
        }

        // Seleção de animação conforme estado
        switch (currentState) {
            case Attack:
                characterRegion = attackAnimation.getKeyFrame(stateTimer);
                // Quando a animação de Attack termina, volta para Idle
                if (attackAnimation.isAnimationFinished(stateTimer)) {
                    currentState = State.Idle;
                    stateTimer = 0;
                }
                break;

            case Hurt:
                characterRegion = hurtAnimation.getKeyFrame(stateTimer);
                // Quando Hurt termina, volta para Idle
                if (hurtAnimation.isAnimationFinished(stateTimer)) {
                    currentState = State.Idle;
                    stateTimer = 0;
                }
                break;

            default:
                characterRegion = idleAnimation.getKeyFrame(stateTimer);
                break;
        }

        // Atualiza frame atual
        setRegion(characterRegion);

        // Ajusta tamanho do sprite com base no frame e escala
        float scale = this.scale;
        float frameWidth = characterRegion.getRegionWidth() / scale;
        float frameHeight = characterRegion.getRegionHeight() / scale;
        setSize(frameWidth, frameHeight);

        // Suaviza o movimento para acompanhar posX e posY
        smoothX += (posX - smoothX) * dt * slideSpeed;
        smoothY += (posY - smoothY) * dt * slideSpeed;

        // Ajusta posição final
        setPosition(
                body.getPosition().x - getWidth() / 2f + smoothX,
                body.getPosition().y - getHeight() / 4f + smoothY);
    }

    // Cria o corpo no mundo Box2D
    public void defineCharacter(Float posX, Float posY) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(posX, posY);
        bodyDef.type = BodyDef.BodyType.StaticBody; // Corpo estático (não se move)
        body = world.createBody(bodyDef);
    }

    // Reduz saúde ao tomar dano
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public int getHealth() {
        return health;
    }

    // Inicia animação de ataque
    public void attack() {
        currentState = State.Attack;
        stateTimer = 0;
    }

    // Agenda animação de dano com delay
    public void hurt() {
        delayedHurtTimer = 0;
        pendingAnimation = true;
    }

    // Retorna true se o personagem está parado
    public boolean getState() {
        return currentState == State.Idle;
    }

    // Restaura vida máxima
    public void restoreLife() {
        this.health = 3;
    }

    // Indica se o personagem está ocupado animando (não pode agir)
    public boolean isBusy() {
        return currentState == State.Attack || currentState == State.Hurt || pendingAnimation;
    }

}