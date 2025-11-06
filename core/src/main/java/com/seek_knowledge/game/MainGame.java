package com.seek_knowledge.game;

import com.badlogic.gdx.Game;
import com.seek_knowledge.game.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}
