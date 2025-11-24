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

    public ButtonGame(String text) {
        this(text, 1f);
    }

    public ButtonGame(String text, float fontScale) {
        this.atlas = new TextureAtlas("assets/assets.atlas");
        this.skin = new Skin();
        skin.addRegions(atlas);
        
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("Button_Unpressed");
        style.down = skin.getDrawable("Button_Pressed");
        font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        if (fontScale != 1f) {
            font.getData().setScale(fontScale);
        }
        style.font = font; 

        button = new TextButton(text, style);
        button.getLabel().setWrap(true);
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
