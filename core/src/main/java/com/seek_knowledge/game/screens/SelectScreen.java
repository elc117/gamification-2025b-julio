package com.seek_knowledge.game.screens;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.ui.ButtonGame;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SelectScreen extends GameScreen {
    private ArrayList<ButtonGame> characterButtons;

    String[] characterNames = { "Dio_Brando", "Jotaro_Kujo", "Joseph_Joestar", "Giorno_Giovanna" };

    public SelectScreen(MainGame game) {
        super(game, "images.atlas", "Dio_Brando");
        Gdx.input.setInputProcessor(stage);

        characterButtons = new ArrayList<ButtonGame>();

        for (String name : characterNames) {
            ButtonGame button = new ButtonGame(name, "assets.atlas", "Button_Unpressed", "Button_Pressed");
            characterButtons.add(button);
        }

        Table table = new Table();
        table.setFillParent(true);
        table.bottom();
        table.padBottom(100);

        for (int i = 0; i < characterButtons.size(); i++) {
            table.add(characterButtons.get(i).getButton()).padRight(50).size(360, 120);
        }
        stage.addActor(table);

        for (int i = 0; i < characterButtons.size(); i++) {
            characterButtons.get(i).getButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new MainScreen(game));
                    dispose();
                }
            });
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
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
