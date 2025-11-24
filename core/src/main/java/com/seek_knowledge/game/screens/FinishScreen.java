package com.seek_knowledge.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.ui.ButtonGame;


public class FinishScreen extends GameScreen {
    private ButtonGame returnGame;
    private Label message;
    private BitmapFont font;

    public FinishScreen (String text, MainGame game) {
        super(game, "assets/assets.atlas", "Background_Select");
        this.returnGame = new ButtonGame("Retornar ao menu", 1.5f);
        this.font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        this.font.getData().setScale(5f);
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        message = new Label(text, style);
        message.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(message);
        table.row();
        table.add(returnGame.getButton()).size(560, 200).padTop(5f);
        returnGame.getButton().addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        stage.addActor(table);
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void dispose() {
        returnGame.dispose();
        super.dispose();
    }


}
