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
        super(game);

        this.camera = new OrthographicCamera();
        super.viewport = new StretchViewport(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, camera);
        this.map = new Map(characterName, camera);

        this.world = new World(new Vector2(0, -10), true);
        this.player = new Character(world, 3, "characters/" + characterName + ".atlas", 320f, 46f, 0.35f);
        this.enemy = new Character(world, 1, "enemies/" + characterName + "/enemy1.atlas", enemyWidth, 55f, 0.3f);

        this.stage = new Stage(super.viewport);
        Gdx.input.setInputProcessor(stage);

        this.atlas = new TextureAtlas("assets/hearts.atlas");
        this.skin = new Skin();
        skin.addRegions(atlas);
        this.heartTexture = skin.getRegion("3hearts");
        Image heartImage = new Image(heartTexture);
        heartImage.setScale(1.5f);
        this.phaseManager = new PhaseManager(player, enemy, world, enemyWidth, viewport, stage, skin, characterName, map);

        Table table = phaseManager.getCurrentQuestion().getTable();
        table.add(heartImage).padTop(50);
        phaseManager.addListener(heartImage, table, game, this);
        stage.addActor(table);

        if (phaseManager.getCurrentPhase() == 3) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {

        super.render(delta);
        map.render();
        player.update(delta, 1f, 0.2f);

        super.batch.begin();
        player.draw(super.batch);
        phaseManager.update(delta, super.batch);
        super.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        map.dispose();
        world.dispose();
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}
