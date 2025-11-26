package com.seek_knowledge.game.screens;

import com.seek_knowledge.game.MainGame;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import com.seek_knowledge.game.sprites.Character;
import com.seek_knowledge.game.tools.PhaseManager;
import com.seek_knowledge.game.ui.Map;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainScreen extends GameScreen {
    private Map map;
    private OrthographicCamera camera;
    private Character player, enemy;
    private TextureAtlas atlas;
    private Skin skin;
    private TextureRegion heartTexture;
    private Stage stage;
    private final float enemyWidth = WORLD_WIDTH / 3;
    private World world;
    private PhaseManager phaseManager;

    public MainScreen(MainGame game, String characterName) {
        // Usa construtor básico do GameScreen (sem background automático)
        super(game);

        // Camera ortográfica que será usada no gameplay
        this.camera = new OrthographicCamera();

        // Substitui o viewport padrão por outro reduzido pela metade
        super.viewport = new StretchViewport(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, camera);

        // Carrega o mapa específico do personagem
        this.map = new Map(characterName, camera);

        // Mundo Box2D com gravidade
        this.world = new World(new Vector2(0, -10), true);

        // Cria o personagem do jogador
        this.player = new Character(world, 3,
                "characters/" + characterName + ".atlas",
                320f, 46f, 0.35f);

        // Cria o inimigo inicial da fase
        this.enemy = new Character(world, 1,
                "enemies/" + characterName + "/enemy1.atlas",
                enemyWidth, 55f, 0.3f);

        // Novo Stage usando o viewport redefinido
        this.stage = new Stage(super.viewport);
        Gdx.input.setInputProcessor(stage);

        // Carrega atlas com corações (vidas)
        this.atlas = new TextureAtlas("assets/hearts.atlas");

        // Skin usada para armazenar regiões do atlas
        this.skin = new Skin();
        skin.addRegions(atlas);

        // Textura das vidas
        this.heartTexture = skin.getRegion("3hearts");

        // Cria imagem do coração e aumenta o tamanho
        Image heartImage = new Image(heartTexture);
        heartImage.setScale(1.5f);

        // Gerenciador que controla as fases, as perguntas e o combate
        this.phaseManager = new PhaseManager(
                player, enemy, world, enemyWidth,
                viewport, stage, skin, characterName, map);

        // Obtém a tabela da pergunta atual e coloca o coração nela
        Table table = phaseManager.getCurrentQuestion().getTable();
        table.add(heartImage).padTop(50);

        // Liga o listener da lógica de fase, controlando o fluxo das perguntas
        phaseManager.addListener(heartImage, table, game, this);

        // Adiciona a tabela renderizada à Stage
        stage.addActor(table);

        // Se já estiver na última fase (fase 3), retorna automaticamente ao menu
        if (phaseManager.getCurrentPhase() == 3) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        // Renderiza fundo básico do GameScreen
        super.render(delta);

        // Renderiza o mapa do jogo
        map.render();

        // Atualiza animação do player
        player.update(delta, 1f, 0.2f);

        // Desenha player e elementos do PhaseManager (inimigo, HUD de combate, etc.)
        super.batch.begin();
        player.draw(super.batch);
        phaseManager.update(delta, super.batch);
        super.batch.end();

        // Atualiza lógica dos atores do Stage (UI)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        // Libera recursos herdados do GameScreen
        super.dispose();

        // Libera recursos específicos dessa tela
        map.dispose();
        world.dispose();
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}