package com.seek_knowledge.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.ui.ButtonGame;
import com.seek_knowledge.game.sprites.Character;

public class HelpScreen extends GameScreen {
    private ButtonGame returnGame;
    private ArrayList<Label> helps;
    private World world;
    private BitmapFont font;
    private ArrayList<Character> characters;
    String[] characterNames = { "Samurai", "Satiro", "Astronomo", "Paladino" };

    public HelpScreen(MainGame game) {
        // Chama GameScreen carregando atlas e background de seleção
        super(game, "assets/assets.atlas", "Background_Select");

        // Botão para retornar ao menu
        this.returnGame = new ButtonGame("Retornar ao menu", 1.5f);

        // Fonte usada nos textos explicativos
        this.font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));

        // Mundo Box2D (gravidade -10 no eixo Y)
        this.world = new World(new Vector2(0, -10), true);

        // O Stage será responsável por capturar input
        Gdx.input.setInputProcessor(stage);

        // Lista que armazenará os labels de ajuda
        this.helps = new ArrayList<Label>();

        // Lista que armazenará os personagens exibidos na tela
        this.characters = new ArrayList<Character>();

        // Textos que explicam como o jogo funciona
        String[] helpsText = {
                "Escolha um dentre quatro personagens disponíveis.",
                "Você deve responder 3 perguntas de nível fácil para enfrentar um chefe, onde deverá responder 3 perguntas difíceis, até vencer os 3 mapas.",
                "Em caso de erro, perderá vida, tendo 3 no total. A cada chefe derrotado, suas vidas regeneram.",
                "Sátiro possui perguntas de biologia, Samurai possui perguntas de história, Astrônomo de astronomia e Paladino possui perguntas de mitologia."
        };

        // Listener do botão: volta ao menu e libera a tela
        returnGame.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Espaçamento horizontal entre os personagens
        float spacing = WORLD_WIDTH / characterNames.length;

        // Cria e posiciona os 4 personagens exibidos no topo da tela
        for (int i = 0; i < characterNames.length; i++) {
            float x = spacing * (i + 0.5f); // posição centralizada por personagem
            characters.add(
                    new Character(
                            world,
                            "characters/" + characterNames[i] + ".atlas",
                            x,
                            46f, // altura fixa
                            0.2f // escala
                    ));
        }

        // Carrega background para os textos de ajuda
        Texture texture = new Texture(Gdx.files.internal("assets/background.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(texture));

        // Define estilo dos labels (fonte + background)
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.background = background;

        // Table organiza os elementos verticalmente
        Table table = new Table();
        table.setFillParent(true); // ocupa a tela toda
        table.top(); // alinha no topo

        // Cria cada label de ajuda e adiciona na table
        for (int index = 0; index < helpsText.length; index++) {
            table.row();
            final int i = index;

            Label help = new Label(helpsText[i], style);
            help.setWrap(true); // permite quebra de linha
            help.setAlignment(Align.center); // centraliza texto

            helps.add(help);
            table.add(helps.get(i))
                    .width(WORLD_WIDTH / 1.5f) // largura do bloco de texto
                    .padTop(50); // espaço entre linhas
        }

        // Adiciona o botão ao final
        table.row();
        table.add(returnGame.getButton()).size(560, 160).padTop(30);

        // Adiciona a table ao Stage
        stage.addActor(table);
    }

    @Override
    public void render(float dt) {
        // Renderiza o background e a UI usando GameScreen
        super.render(dt);

        // Desenha os personagens animados
        super.batch.begin();
        for (Character character : characters) {
            character.update(dt, 1f, 0.2f); // animação
            character.draw(batch); // desenha
        }
        super.batch.end();

        // Atualiza e desenha a UI
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void dispose() {
        // Libera recursos do botão
        returnGame.dispose();

        // Libera recursos base da GameScreen
        super.dispose();
    }
}