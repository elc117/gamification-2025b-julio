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
        super(game, "assets/assets.atlas", "Background_Select");
        this.returnGame = new ButtonGame("Retornar ao menu", 1.5f);
        this.font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        this.world = new World(new Vector2(0, -10), true);
        Gdx.input.setInputProcessor(stage);
        this.helps = new ArrayList<Label>();
        this.characters = new ArrayList<Character>();

        String[] helpsText = {
                "Escolha um dentre quatro personagens disponíveis.",
                "Você deve responder 3 perguntas de nível fácil para enfrentar um chefe, onde deverá responder 3 perguntas difíceis, até vencer os 3 mapas.",
                "Em caso de erro, perderá vida, tendo 3 no total. A cada chefe derrotado, suas vidas regeneram.",
                "Sátiro possui perguntas de biologia, Samurai possui perguntas de história, Astrônomo de astronomia e Paladino possui perguntas de mitologia."
        };

        returnGame.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        float spacing = WORLD_WIDTH / characterNames.length;

        for (int i = 0; i < characterNames.length; i++) {
            float x = spacing * (i + 0.5f);
            characters.add(new Character(world, "characters/" + characterNames[i] + ".atlas", x, 46f, 0.2f));
        }

        Texture texture = new Texture(Gdx.files.internal("assets/background.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(texture));

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.background = background;

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        for (int index = 0; index < helpsText.length; index++) {
            table.row();
            final int i = index;
            Label help = new Label(helpsText[i], style);
            help.setWrap(true);
            help.setAlignment(Align.center);

            helps.add(help);
            table.add(helps.get(i)).width(WORLD_WIDTH / 1.5f).padTop(50);
        }
        table.row();
        table.add(returnGame.getButton()).size(560, 160).padTop(30);
        stage.addActor(table);
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        super.batch.begin();
        for (Character character : characters) {
            character.update(dt, 1f, 0.2f);
            character.draw(batch);
        }
        super.batch.end();
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void dispose() {
        returnGame.dispose();
        super.dispose();
    }
}
