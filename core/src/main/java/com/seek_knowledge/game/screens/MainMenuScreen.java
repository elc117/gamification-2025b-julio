package com.seek_knowledge.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.seek_knowledge.game.MainGame;
import com.seek_knowledge.game.ui.ButtonGame;

public class MainMenuScreen extends GameScreen {
    private ButtonGame playButton, helpButton;

    public MainMenuScreen(MainGame game) {
        // Chama o GameScreen com o atlas principal e o background do menu
        super(game, "assets/assets.atlas", "Background");

        // Define que os inputs vão para o Stage (botões)
        Gdx.input.setInputProcessor(stage);

        // Cria os botões do menu
        this.playButton = new ButtonGame("Jogar", 1.5f);
        this.helpButton = new ButtonGame("Ajuda", 1.5f);

        // Table organiza os botões verticalmente no centro da tela
        Table table = new Table();
        table.setFillParent(true); // ocupa a tela inteira
        table.center(); // centraliza elementos

        // Adiciona o botão "Jogar"
        table.add(playButton.getButton())
                .size(360, 120)
                .padTop(100);

        // Próxima linha
        table.row();

        // Adiciona o botão "Ajuda"
        table.add(helpButton.getButton())
                .size(360, 120)
                .padTop(20);

        // Insere o layout no Stage (UI)
        stage.addActor(table);

        // Listener do botão "Jogar" → vai para SelectScreen
        playButton.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectScreen(game));
                dispose(); // libera recursos da tela atual
            }
        });

        // Listener do botão "Ajuda" → vai para HelpScreen
        helpButton.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HelpScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        // Renderiza o background + UI definida em GameScreen
        super.render(delta);

        // Atualiza e desenha o Stage (botões)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        // Libera recursos do botão (fonte, texturas, etc.)
        if (playButton != null)
            playButton.dispose();

        // Libera recursos herdados de GameScreen
        super.dispose();
    }

}