package com.seek_knowledge.game.screens;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.sprites.Character;
import com.seek_knowledge.game.ui.ButtonGame;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SelectScreen extends GameScreen {
    private ArrayList<ButtonGame> characterButtons; // Lista de botões para selecionar personagens
    private World world; // Mundo Box2D (gravidade etc.)
    String[] characterNames = { "Samurai", "Satiro", "Astronomo", "Paladino" }; // Nomes dos personagens
    private ArrayList<Character> characters; // Lista de personagens animados mostrados na tela

    public SelectScreen(MainGame game) {
        // Carrega o fundo padrão usando o construtor da classe pai
        super(game, "assets/assets.atlas", "Background_Select");

        // Define o Stage como processador de entrada
        Gdx.input.setInputProcessor(stage);

        // Mundo físico com gravidade
        this.world = new World(new Vector2(0, -10), true);

        // Inicializa listas
        this.characterButtons = new ArrayList<ButtonGame>();
        this.characters = new ArrayList<Character>();

        // Cria um botão para cada personagem
        for (String name : characterNames) {
            ButtonGame button = new ButtonGame(name);
            characterButtons.add(button);
        }

        // Tabela para organizar os botões na parte inferior da tela
        Table table = new Table();
        table.setFillParent(true);
        table.center().bottom();
        table.padBottom(100);

        // Espaço horizontal entre personagens
        float spacing = WORLD_WIDTH / (characterNames.length);

        // Adiciona botões na tabela e cria personagens animados acima deles
        for (int i = 0; i < characterButtons.size(); i++) {
            table.add(characterButtons.get(i).getButton()).expandX().size(360, 120);

            // Posição X calculada para distribuir uniformemente
            float x = spacing * (i + 0.5f);

            // Cria o personagem correspondente para exibir sua animação
            characters.add(new Character(world, "characters/" + characterNames[i] + ".atlas", x, 46f, 0.1f));
        }

        // Adiciona a tabela ao Stage
        stage.addActor(table);

        // Adiciona listeners para cada botão
        for (int i = 0; i < characterButtons.size(); i++) {
            final int index = i;

            characterButtons.get(i).getButton().addListener(new ClickListener() {
                String name = characterNames[index]; // Nome do personagem selecionado

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Ao clicar, troca para a MainScreen já com o personagem escolhido
                    game.setScreen(new MainScreen(game, name));
                    dispose();
                }
            });
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Desenha a animação dos personagens
        super.batch.begin();
        for (Character character : characters) {
            character.update(delta, 1f, 0.2f); // Atualiza animação
            character.draw(batch); // Desenha na tela
        }
        super.batch.end();

        // Atualiza e desenha elementos do Stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true); // Ajusta viewport para novos tamanhos
    }

    @Override
    public void dispose() {
        // Libera memória dos botões
        if (characterButtons != null) {
            for (ButtonGame btn : characterButtons) {
                if (btn != null)
                    btn.dispose();
            }
        }

        // Libera recursos carregados pela GameScreen
        super.dispose();
    }
}