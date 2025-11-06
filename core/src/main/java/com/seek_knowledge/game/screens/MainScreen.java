package com.seek_knowledge.game.screens;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.seek_knowledge.game.MainGame;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import com.seek_knowledge.game.sprites.Character;
import com.seek_knowledge.game.tools.Ground;
import com.seek_knowledge.game.trivia.Question;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainScreen extends GameScreen {
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Character player, enemy;

    private TextureAtlas atlas;
    private Skin skin;
    private TextureRegion heartTexture;

    private Question[] questions;
    private Question currentQuestion;
    private Stage stage;
    private int currentIndex = 0;
    private boolean isBossLevel = false;

    private World world;

    public MainScreen(MainGame game) {
        super(game);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("World1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        camera = new OrthographicCamera();
        super.viewport = new StretchViewport(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, camera);

        world = new World(new Vector2(0, -10), true);
        player = new Character(world, 3, "character.atlas", 256f, 256f, 100f, 70f);
        enemy = new Character(world, 1, "enemy1.atlas", 256f, 256f, 300f, 70f);

        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            new Ground(world, object);
        }

        stage = new Stage(super.viewport);
        Gdx.input.setInputProcessor(stage);

        loadJson("questions.json");

        currentQuestion = new Question(questions[currentIndex].options, super.viewport, stage,
                questions[currentIndex].correctIndex, questions[currentIndex].text);

        atlas = new TextureAtlas("hearts.atlas");
        skin = new Skin();
        skin.addRegions(atlas);
        heartTexture = skin.getRegion("3hearts");

        Image heartImage = new Image(heartTexture);

        Table table = currentQuestion.getTable();
        table.add(heartImage).padTop(30);
        addListener(heartImage, table);
        stage.addActor(table);
    }

    private void loadJson(String jsonPath) {
        Json json = new Json();
        FileHandle file = Gdx.files.internal(jsonPath);
        questions = json.fromJson(Question[].class, file);
    }

    private void addListener(Image heartImage, Table table) {
        Image enemyImage = new Image(heartTexture);

        for (int i = 0; i < currentQuestion.getOptionsButtons().length; i++) {
            final int index = i;
            currentQuestion.getOptionsButtons()[i].getButton()
                    .addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                        @Override
                        public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                            if (currentQuestion.isCorrect(index)) {
                                player.attack();
                                currentIndex++;

                                if (isBossLevel) {
                                    enemy.takeDamage(1);
                                    TextureRegion newRegion = skin.getRegion(enemy.getHealth() + "hearts");
                                    enemyImage.setDrawable(new Image(newRegion).getDrawable());
                                }

                                if (currentIndex >= questions.length) {
                                    isBossLevel = !isBossLevel;
                                    if (isBossLevel) {
                                        loadJson("boss.json");
                                        enemy = new Character(world, 3, "enemy1.atlas", 256f, 256f, 300f, 70f);

                                        table.add(enemyImage).padTop(10);
                                        currentIndex = 0;
                                    } else {
                                        loadJson("questions.json");

                                        currentIndex = 0;
                                    }
                                }

                                currentQuestion.updateQuestion(questions[currentIndex].options,
                                        questions[currentIndex].correctIndex, questions[currentIndex].text);
                            } else {
                                player.takeDamage(1);
                                enemy.attack();
                                player.hurt();
                                heartTexture = skin.getRegion(player.getHealth() + "hearts");
                                heartImage.setDrawable(new Image(heartTexture).getDrawable());
                            }
                        }
                    });
        }
    }

    @Override
    public void render(float delta) {

        super.render(delta);
        mapRenderer.setView(camera);
        mapRenderer.render();
        player.update(delta, 0.2f, 0.2f);
        enemy.update(delta, -1.1f, 0.2f);

        super.batch.begin();
        player.draw(super.batch);
        enemy.draw(super.batch);
        super.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }

}
