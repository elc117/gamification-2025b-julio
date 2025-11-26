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

    // Construtor padrão com tamanho de fonte normal (1f)
    public ButtonGame(String text) {
        this(text, 1f);
    }

    // Construtor que permite definir escala da fonte
    public ButtonGame(String text, float fontScale) {
        // Carrega atlas com as imagens do botão (Pressed / Unpressed)
        this.atlas = new TextureAtlas("assets/assets.atlas");

        // Skin que gerencia os drawables do atlas
        this.skin = new Skin();
        skin.addRegions(atlas);

        // Criação do estilo do botão
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("Button_Unpressed"); // Estado normal
        style.down = skin.getDrawable("Button_Pressed"); // Estado pressionado

        // Fonte usada no texto do botão
        font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        if (fontScale != 1f) {
            font.getData().setScale(fontScale); // Ajusta o tamanho da fonte, se necessário
        }
        style.font = font;

        // Cria o botão com o estilo definido
        button = new TextButton(text, style);

        // Permite que o texto quebre linha quando necessário
        button.getLabel().setWrap(true);
    }

    // Retorna o botão para ser usado na interface
    public TextButton getButton() {
        return button;
    }

    // Libera memória dos recursos gráficos
    public void dispose() {
        if (font != null)
            font.dispose();
        if (skin != null)
            skin.dispose();
        if (atlas != null)
            atlas.dispose();
    }
}