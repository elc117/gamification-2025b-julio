package com.seek_knowledge.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.ui.ButtonGame;

public class MainMenuScreen extends GameScreen {
    private ButtonGame playButton;

    public MainMenuScreen(MainGame game) {
        super(game, "assets/assets.atlas", "Background");
        Gdx.input.setInputProcessor(stage);

        this.playButton = new ButtonGame("Jogar", 1.5f);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton.getButton()).size(360, 120).pad(10);
        table.row();
        stage.addActor(table);

        playButton.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectScreen(game));
                dispose();
            }
        });
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
    }

    @Override
    public void dispose() {
        if (playButton != null) playButton.dispose();
        super.dispose();
    }

}