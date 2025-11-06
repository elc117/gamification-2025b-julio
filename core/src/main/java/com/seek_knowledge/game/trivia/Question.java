package com.seek_knowledge.game.trivia;

import com.seek_knowledge.game.ui.ButtonGame;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    public Question() {}

    public Question(String[] answerOptions, Viewport viewport, Stage stage, int correctIndex, String text) {
        questionFont = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        optionsButtons = new ButtonGame[4];
        this.correctIndex = correctIndex;
        this.text = text;
        this.viewport = viewport;
        this.stage = stage;

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = questionFont;
        questionLabel = new Label(text, style);
        questionLabel.setAlignment(Align.center);

        table = new Table();
        table.setFillParent(true);
        table.center().top();
        table.add(questionLabel).width(500).padBottom(20);
        table.row();

        for (int i = 0; i < 4; i++) {
            optionsButtons[i] = new ButtonGame((i + 1) + ") " + answerOptions[i], "assets.atlas", "Button_Unpressed", "Button_Pressed");
            table.add(optionsButtons[i].getButton()).width(300).height(50).pad(10);

            if (i % 2 == 1) {
                table.row();
            }
        }

        if (stage != null) {
            stage.addActor(table);
        }
    }

    public void updateQuestion(String[] newOptions, int newCorrectIndex, String newText) {
        this.correctIndex = newCorrectIndex;
        this.text = newText;
        questionLabel.setText(newText);

        for (int i = 0; i < 4; i++) {
            optionsButtons[i].getButton().setText((i + 1) + ") " + newOptions[i]);
        }
    }

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
}