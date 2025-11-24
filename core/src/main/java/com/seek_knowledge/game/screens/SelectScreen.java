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
    private ArrayList<ButtonGame> characterButtons;
    private World world;
    String[] characterNames = { "Samurai", "Satiro", "Astronomo", "Paladino" };
    private ArrayList<Character> characters;

    public SelectScreen(MainGame game) {
        super(game, "assets/assets.atlas", "Background_Select");
        Gdx.input.setInputProcessor(stage);
        this.world = new World(new Vector2(0, -10), true);
        this.characterButtons = new ArrayList<ButtonGame>();
        this.characters = new ArrayList<Character>();
        
        for (String name : characterNames) {
            ButtonGame button = new ButtonGame(name);
            characterButtons.add(button);
        }

        Table table = new Table();
        table.setFillParent(true);
        table.center().bottom();
        table.padBottom(100);

        float spacing = WORLD_WIDTH/(characterNames.length);

        for (int i = 0; i < characterButtons.size(); i++) {
            table.add(characterButtons.get(i).getButton()).expandX().size(360, 120);
            float x = spacing * (i + 0.5f);
            characters.add(new Character(world, "characters/" + characterNames[i] + ".atlas", x, 46f, 0.1f));
        }
        stage.addActor(table);

        for (int i = 0; i < characterButtons.size(); i++) {
            final int index = i;
            characterButtons.get(i).getButton().addListener(new ClickListener() {
                String name = characterNames[index];

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new MainScreen(game, name));
                    dispose();
                }
            });
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        super.batch.begin();
        for (Character character : characters) {
            character.update(delta, 1f, 0.2f);
            character.draw(batch);
        }
        super.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (characterButtons != null) {
            for (ButtonGame btn : characterButtons) {
                if (btn != null)
                    btn.dispose();
            }
        }
        super.dispose();
    }
}
