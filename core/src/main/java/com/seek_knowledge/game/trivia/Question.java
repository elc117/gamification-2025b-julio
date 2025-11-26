package com.seek_knowledge.game.trivia;

import com.seek_knowledge.game.ui.ButtonGame;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Question {
    public int id;
    public String text;
    public String[] options;
    public int correctIndex;

    private ButtonGame[] optionsButtons;
    private BitmapFont questionFont;
    private Viewport viewport;
    private Stage stage;
    private Table table;
    private Label questionLabel;

    public Question() {
    }

    // Construtor principal que cria a pergunta completa na tela
    public Question(String[] answerOptions, Viewport viewport, Stage stage, int correctIndex, String text) {
        // Carrega fonte usada no texto da pergunta
        this.questionFont = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        this.questionFont.getData().setScale(0.6f);

        this.optionsButtons = new ButtonGame[4]; // Sempre 4 alternativas
        this.correctIndex = correctIndex;
        this.text = text;
        this.viewport = viewport;
        this.stage = stage;

        // Carrega a textura de fundo da caixa da pergunta
        Texture texture = new Texture(Gdx.files.internal("assets/background.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(texture));

        // Estilo do texto da pergunta
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = questionFont;
        style.background = background;

        // Label com o texto da pergunta
        this.questionLabel = new Label(text, style);
        questionLabel.setAlignment(Align.center);
        questionLabel.setWrap(true); // Permite quebra de linha automática

        // Tabela que organiza a pergunta e as opções na tela
        this.table = new Table();
        table.setFillParent(true); // Preenche a tela inteira
        table.center().top(); // Posição no topo
        table.add(questionLabel).colspan(2).width(500).padBottom(20);
        table.row();

        // Cria e adiciona os botões das opções
        for (int i = 0; i < 4; i++) {
            optionsButtons[i] = new ButtonGame((i + 1) + ") " + answerOptions[i], 0.5f);
            table.add(optionsButtons[i].getButton()).width(300).height(50).pad(10);

            // A cada duas opções, pula para linha de baixo
            if (i % 2 == 1) {
                table.row();
            }
        }

        // Adiciona na Stage se existir
        if (stage != null) {
            stage.addActor(table);
        }
    }

    // Atualiza a pergunta, texto e respostas (usado ao trocar de pergunta)
    public void updateQuestion(String[] newOptions, int newCorrectIndex, String newText) {
        this.correctIndex = newCorrectIndex;
        this.text = newText;
        questionLabel.setText(newText);

        // Atualiza o texto de cada botão
        for (int i = 0; i < 4; i++) {
            optionsButtons[i].getButton().setText((i + 1) + ") " + newOptions[i]);
        }
    }

    // Verifica se o índice clicado é o correto
    public boolean isCorrect(int index) {
        return index == correctIndex;
    }

    public BitmapFont getQuestionFont() {
        return questionFont;
    }

    public ButtonGame[] getOptionsButtons() {
        return optionsButtons;
    }

    public Stage getStage() {
        return stage;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Table getTable() {
        return table;
    }

    // Bloqueia os botões para impedir múltiplos cliques
    public void blockInput() {
        for (int i = 0; i < 4; i++) {
            optionsButtons[i].getButton().setTouchable(Touchable.disabled);
        }
    }

    // Reabilita os botões
    public void enableInput() {
        for (int i = 0; i < 4; i++) {
            optionsButtons[i].getButton().setTouchable(Touchable.enabled);
        }
    }

    // Animação suave para transição de perguntas
    public void animateQuest(Runnable onMid) {
        Table root = getTable();

        // Fade-out → troca pergunta → fade-in
        root.addAction(Actions.sequence(
                Actions.fadeOut(0.2f),
                Actions.run(onMid),
                Actions.fadeIn(0.2f)));
    }
}