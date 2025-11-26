package com.seek_knowledge.game.tools;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.seek_knowledge.game.ui.Map;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.screens.FinishScreen;
import com.seek_knowledge.game.sprites.Character;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PhaseManager {

    private Question[] questions; // Todas as perguntas carregadas do JSON atual
    private Question currentQuestion; // Pergunta exibida na tela nesse momento
    private int currentPhase; // Fase atual (1 a 3)
    private int currentIndex; // Contador de perguntas respondidas na fase
    private Skin skin; // Skin usada para UI
    private TextureRegion heartTexture; // Textura atual de corações do player
    private Character player, enemy; // Personagem jogador e inimigo
    private Boolean isBossLevel; // Indica se está lutando contra o boss
    private Map map; // Controla o mapa e mudanças visuais
    private World world; // Mundo Box2D para criação de personagens
    private float enemyWidth; // Posição onde inimigos aparecem
    private String characterName; // Personagem selecionado (define perguntas e inimigos)
    private ArrayList<Integer> enemyQuestions; // Perguntas já usadas contra inimigos comuns
    private ArrayList<Integer> bossQuestions; // Perguntas já usadas contra o boss

    public PhaseManager(Character player, Character enemy, World world, Float enemyWidth, Viewport viewport,
            Stage stage, Skin skin, String character, Map map) {

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

        this.heartTexture = this.skin.getRegion("3hearts"); // Player sempre começa com 3 vidas
        this.enemyQuestions = new ArrayList<>();
        this.bossQuestions = new ArrayList<>();

        // Carrega o arquivo JSON da fase inicial
        loadJson("questions/" + characterName + "_questions.json");

        // Escolhe pergunta aleatória que ainda não foi usada
        int random = randomNum(enemyQuestions);

        // Cria a pergunta na tela
        currentQuestion = new Question(questions[random].options, viewport, stage,
                questions[random].correctIndex, questions[random].text);
    }

    // Lê JSON de perguntas e cria array de Question
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

    // Adiciona listener que controla TODA a lógica do combate e progressão
    public void addListener(final Image heartImage, Table table, MainGame game, Screen screen) {

        // Imagem de coração do inimigo (no início vazia)
        final Image enemyImage = new Image(heartTexture);
        enemyImage.setScale(2f);
        table.add(enemyImage).padTop(50);
        enemyImage.setDrawable(null);

        ClickListener listener = new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                // Impede spam de inputs durante animações
                currentQuestion.blockInput();
                int index = -1;

                // Descobre qual botão foi clicado
                for (int i = 0; i < currentQuestion.getOptionsButtons().length; i++) {
                    if (event.getListenerActor() == currentQuestion.getOptionsButtons()[i].getButton()) {
                        index = i;
                        break;
                    }
                }

                if (index == -1)
                    return;

                // RESPOSTA CORRETA -------------------------------------
                if (currentQuestion.isCorrect(index)) {
                    player.attack();
                    currentIndex++;

                    if (isBossLevel) {
                        // Dano ao boss
                        enemy.takeDamage(1);
                        enemy.hurt();

                        // Boss derrotado
                        if (enemy.getHealth() == 0) {
                            endBossLevel();
                            updatePlayerImage(heartImage);
                        } else {
                            updateEnemyImage(enemyImage);
                        }
                    }

                    // Após 3 perguntas corretas com inimigos normais → começa o boss
                    if (currentIndex >= 3 && !isBossLevel) {
                        startBossLevel(enemyImage);

                    } else if (!isBossLevel) {
                        // Troca de inimigo comum entre cada pergunta
                        enemy = new Character(world, 1,
                                "enemies/" + characterName + "/enemy" + (currentIndex + 1) + ".atlas",
                                enemyWidth, 70f, 0.3f);
                    }

                } else {
                    // RESPOSTA ERRADA ------------------------------------
                    player.takeDamage(1);
                    checkGameOver();
                }

                // Atualiza pergunta de acordo com fase (boss ou normal)
                if (isBossLevel) {
                    updateQuestion(bossQuestions);
                } else {
                    updateQuestion(enemyQuestions);
                }
            }

            // Atualiza corações do inimigo
            private void updateEnemyImage(Image img) {
                img.setDrawable(new Image(skin.getRegion(enemy.getHealth() + "hearts")).getDrawable());
            }

            // Atualiza corações do player
            private void updatePlayerImage(Image img) {
                heartTexture = skin.getRegion(player.getHealth() + "hearts");
                img.setDrawable(new Image(heartTexture).getDrawable());
            }

            // Inicia luta contra o boss
            private void startBossLevel(Image img) {
                isBossLevel = true;

                loadJson("questions/" + characterName + "_boss.json");

                enemy = new Character(world, 3,
                        "enemies/" + characterName + "/boss" + (currentPhase) + ".atlas",
                        enemyWidth, 46f, 0.27f);

                updateEnemyImage(img);
                currentIndex = 0;
            }

            // Finaliza boss e volta para inimigos comuns
            private void endBossLevel() {
                checkGameFinished(); // Avança fase ou finaliza jogo
                currentIndex = 0;
                map.changeMap(characterName, currentPhase);

                isBossLevel = false;

                loadJson("questions/" + characterName + "_questions.json");

                enemyImage.setDrawable(null);

                enemy = new Character(world, 1,
                        "enemies/" + characterName + "/enemy1.atlas",
                        enemyWidth, 70f, 0.3f);

                player.restoreLife();
            }

            // Puxa uma nova pergunta sem repetir
            private void updateQuestion(ArrayList<Integer> array) {
                Question q = questions[randomNum(array)];
                currentQuestion.animateQuest(() -> {
                    currentQuestion.updateQuestion(q.options, q.correctIndex, q.text);
                });
            }

            // Avança a fase ou encerra o jogo
            private void checkGameFinished() {
                if (currentPhase < 3) {
                    currentPhase++;
                } else {
                    game.setScreen(new FinishScreen("Você Venceu!", game));
                }
            }

            // Verifica se o jogador morreu
            private void checkGameOver() {
                if (player.getHealth() == 0) {
                    game.setScreen(new FinishScreen("Você Perdeu!", game));
                } else {
                    enemy.attack();
                    player.hurt();
                    updatePlayerImage(heartImage);
                }
            }

        };

        // Liga o listener em todos os botões de resposta
        for (ButtonGame btn : currentQuestion.getOptionsButtons()) {
            btn.getButton().addListener(listener);
        }
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public Character getEnemy() {
        return enemy;
    }

    // Atualiza inimigo e libera input quando o jogador não está animando
    // ataque/dano
    public void update(float delta, SpriteBatch batch) {
        if (enemy != null) {
            enemy.update(delta, -1.1f, 0.2f);
            enemy.draw(batch);
        }

        if (!player.isBusy()) {
            currentQuestion.enableInput();
        }
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    // Gera número aleatório sem repetir
    private int randomNum(ArrayList<Integer> array) {
        Random generator = new Random();
        Integer num;

        do {
            num = generator.nextInt((questions.length - 1) - 0 + 1) + 0;
        } while (questionUndone(num, array));

        array.add(num);

        return num;
    }

    // Verifica se pergunta já foi usada
    private boolean questionUndone(Integer num, ArrayList<Integer> array) {
        return array.contains(num);
    }
}