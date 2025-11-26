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

    public FinishScreen(String text, MainGame game) {
        // Chama o construtor da classe GameScreen,
        // configura o atlas e o background dessa tela.
        super(game, "assets/assets.atlas", "Background_Select");

        // Cria um botão para retornar ao menu.
        this.returnGame = new ButtonGame("Retornar ao menu", 1.5f);

        // Carrega a fonte usada para exibir a mensagem.
        this.font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        this.font.getData().setScale(5f); // Aumenta o tamanho da fonte

        // Define que o input atual será tratado pelo Stage desta tela.
        Gdx.input.setInputProcessor(stage);

        // Cria estilo de texto para o Label.
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        // Label que exibe o texto de finalização (ex: "Você venceu!" ou "Fim de jogo").
        message = new Label(text, style);
        message.setAlignment(Align.center);

        // Usamos uma Table para organizar o layout na tela.
        Table table = new Table();
        table.setFillParent(true); // Faz a table ocupar toda a tela
        table.center(); // Centraliza todo o conteúdo

        // Adiciona o texto
        table.add(message);
        table.row(); // Pula para a próxima linha da table

        // Adiciona o botão com tamanho e espaçamento configurados
        table.add(returnGame.getButton()).size(560, 200).padTop(5f);

        // Listener do botão: quando clicado, volta para o MainMenuScreen.
        returnGame.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game)); // troca de tela
                dispose(); // libera recursos dessa tela
            }
        });

        // Adiciona a Table ao Stage
        stage.addActor(table);
    }

    @Override
    public void render(float dt) {
        super.render(dt); // Desenha o background (GameScreen faz isso)
        stage.act(dt); // Atualiza os atores do Stage
        stage.draw(); // Desenha os atores do Stage
    }

    @Override
    public void dispose() {
        // Libera recursos do botão
        returnGame.dispose();
        // Chama o dispose da classe base para liberar atlas, stage etc.
        super.dispose();
    }
}