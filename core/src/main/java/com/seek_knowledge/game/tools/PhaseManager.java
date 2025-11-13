package com.seek_knowledge.game.tools;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.seek_knowledge.game.trivia.Question;
import com.seek_knowledge.game.ui.ButtonGame;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.screens.MainMenuScreen;
import com.seek_knowledge.game.sprites.Character;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PhaseManager {
    private Question[] questions;
    private Question currentQuestion;
    private int currentPhase;
    private int currentIndex;
    private Skin skin;
    private TextureRegion heartTexture;
    private Character player, enemy;
    private Boolean isBossLevel;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private World world;
    private float enemyWidth;
    private String characterName;

    public PhaseManager(Character player, Character enemy, World world, Float enemyWidth, Viewport viewport,
            Stage stage, Skin skin, String character, TiledMap map, OrthogonalTiledMapRenderer mapRenderer,
            TmxMapLoader mapLoader) {
        this.player = player;
        this.enemy = enemy;
        this.world = world;
        this.enemyWidth = enemyWidth;
        this.isBossLevel = false;
        this.currentIndex = 0;
        this.currentPhase = 1;
        this.skin = skin;
        this.characterName = character;
        this.map = map;
        this.mapRenderer = mapRenderer;
        this.mapLoader = mapLoader;
        this.heartTexture = this.skin.getRegion("3hearts");

        loadJson("questions/" + characterName + "_questions.json");
        int random = randomNum();

        currentQuestion = new Question(questions[random].options, viewport, stage,
                questions[random].correctIndex, questions[random].text);
    }

    private void loadJson(String path) {
        JsonValue root = new JsonReader().parse(Gdx.files.internal(path));
        questions = new Question[root.size];
        int i = 0;

        for (JsonValue q : root) {
            Question question = new Question();
            question.text = q.getString("text");
            question.correctIndex = q.getInt("correctIndex");

            JsonValue optionsJson = q.get("options");
            question.options = new String[optionsJson.size];
            int j = 0;
            for (JsonValue opt : optionsJson)
                question.options[j++] = opt.asString();

            questions[i++] = question;
        }
    }

    public void addListener(final Image heartImage, Table table, MainGame game, Screen screen) {
        final Image enemyImage = new Image(heartTexture);
        table.add(enemyImage).padTop(10);
        enemyImage.setDrawable(null);

        ClickListener listener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int index = -1;
                for (int i = 0; i < currentQuestion.getOptionsButtons().length; i++) {
                    if (event.getListenerActor() == currentQuestion.getOptionsButtons()[i].getButton()) {
                        index = i;
                        break;
                    }
                }

                if (index == -1)
                    return;

                if (currentQuestion.isCorrect(index)) {
                    player.attack();
                    currentIndex++;

                    if (isBossLevel) {
                        enemy.takeDamage(1);
                        enemy.hurt();
                        if (enemy.getHealth() == 0) {
                            checkGameFinished();
                            currentIndex = 0;
                            changeMap("maps/" + characterName + "_World" + currentPhase + ".tmx");
                            isBossLevel = false;
                            loadJson("questions/" + characterName + "_questions.json");
                            enemyImage.setDrawable(null);
                            enemy = new Character(world, 1,
                                    "enemies/" + characterName + "/enemy" + (currentIndex + 1) + ".atlas", enemyWidth,
                                    70f, 0.3f);
                            player.restoreLife();
                            updatePlayerImage(heartImage);
                        } else {
                            updateEnemyImage(enemyImage);
                        }
                    }

                    if (currentIndex >= 3 && !isBossLevel) {
                        startBossLevel(enemyImage);

                    } else if (!isBossLevel) {
                        enemy = new Character(world, 1,
                                "enemies/" + characterName + "/enemy" + (currentIndex + 1) + ".atlas", enemyWidth, 70f,
                                0.3f);
                    }

                } else {
                    player.takeDamage(1);
                    checkGameOver();
                }
                updateQuestion();
            }

            private void updateEnemyImage(Image img) {
                img.setDrawable(new Image(skin.getRegion(enemy.getHealth() + "hearts")).getDrawable());
            }

            private void updatePlayerImage(Image img) {
                heartTexture = skin.getRegion(player.getHealth() + "hearts");
                img.setDrawable(new Image(heartTexture).getDrawable());
            }

            private void startBossLevel(Image img) {
                isBossLevel = true;
                loadJson("questions/" + characterName + "_boss.json");
                enemy = new Character(world, 3, "enemies/" + characterName + "/boss" + (currentPhase) + ".atlas",
                        enemyWidth, 46f, 0.25f);
                updateEnemyImage(img);
                currentIndex = 0;
            }

            private void updateQuestion() {
                Question q = questions[randomNum()];
                currentQuestion.updateQuestion(q.options, q.correctIndex, q.text);
            }

            private void checkGameFinished() {
                if (currentPhase < 3) {
                    currentPhase++;
                } else {
                    game.setScreen(new MainMenuScreen(game));
                }
            }

            private void checkGameOver() {
                if (player.getHealth() == 0) {
                    game.setScreen(new MainMenuScreen(game));
                } else {
                    enemy.attack();
                    player.hurt();
                    updatePlayerImage(heartImage);
                }
            }
        };

        for (ButtonGame btn : currentQuestion.getOptionsButtons()) {
            btn.getButton().addListener(listener);
        }
    }

    public void changeMap(String fileName) {
        map.dispose();
        map = mapLoader.load(fileName);
        mapRenderer.setMap(map);
        ;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public Character getEnemy() {
        return enemy;
    }

    public void update(float delta, SpriteBatch batch) {
        if (enemy != null) {
            enemy.update(delta, -1.1f, 0.2f);
            enemy.draw(batch);
        }
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    private int randomNum() {
        Random generator = new Random();
        int num;

        num = generator.nextInt((questions.length - 1) - 0 + 1) + 0;

        return num;
    }
}
