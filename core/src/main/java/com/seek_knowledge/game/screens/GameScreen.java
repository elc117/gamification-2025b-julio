package com.seek_knowledge.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.seek_knowledge.game.MainGame;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen implements Screen {
    protected static final float WORLD_WIDTH = 1920; // Largura base do mundo do jogo
    protected static final float WORLD_HEIGHT = 1080; // Altura base do mundo do jogo
    protected MainGame game;
    protected SpriteBatch batch;
    protected TextureAtlas atlas;
    protected TextureRegion background;
    protected Viewport viewport;
    protected Stage stage;

    public GameScreen(MainGame game, String atlasPath, String regionName) {
        this.game = game;
        this.batch = new SpriteBatch();

        atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        background = atlas.findRegion(regionName);

        this.viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    }

    public GameScreen(MainGame game) {
        this.game = game;
        this.batch = new SpriteBatch();

        this.viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        if (background != null) {
            batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (stage != null)
            stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (atlas != null)
            atlas.dispose();
        if (stage != null)
            stage.dispose();
    }

    public Viewport getViewport() {
        return viewport;
    }
}