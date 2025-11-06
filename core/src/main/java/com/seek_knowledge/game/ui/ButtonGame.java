package com.seek_knowledge.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.Gdx;

public class ButtonGame {
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton button;
    private BitmapFont font;

    // Mantém o construtor antigo, agora delegando para o novo com scale = 1f
    public ButtonGame(String text, String atlasPath, String buttonUp, String buttonDown) {
        this(text, atlasPath, buttonUp, buttonDown, 1f);
    }

    public ButtonGame(String text, String atlasPath, String buttonUp, String buttonDown, float fontScale) {
        atlas = new TextureAtlas(atlasPath);
        skin = new Skin();
        skin.addRegions(atlas);
        
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable(buttonUp);
        style.down = skin.getDrawable(buttonDown);
        font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        if (fontScale != 1f) {
            font.getData().setScale(fontScale);
        }
        style.font = font; 

        button = new TextButton(text, style);
    }

    public TextButton getButton() {
        return button;
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (skin != null) skin.dispose();
        if (atlas != null) atlas.dispose();
    }

}
