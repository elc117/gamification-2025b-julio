package com.seek_knowledge.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.seek_knowledge.game.MainGame;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen implements Screen {
    public static final float WORLD_WIDTH = 1920; // Largura base do mundo do jogo
    public static final float WORLD_HEIGHT = 1080; // Altura base do mundo do jogo
    protected MainGame game;
    protected SpriteBatch batch;
    protected TextureAtlas atlas;
    protected TextureRegion background;
    protected Viewport viewport;
    protected Stage stage;

    public GameScreen(MainGame game, String atlasPath, String regionName) {
        this.game = game;
        this.batch = new SpriteBatch();

        // Carrega o atlas e a região usada como background
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        this.background = atlas.findRegion(regionName);

        // Viewport que estica a imagem para caber no tamanho da janela
        this.viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT);
        // Centraliza a câmera do viewport
        this.viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        // Stage usado para UI, com viewport que mantém proporção
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    }

    public GameScreen(MainGame game) {
        this.game = game;
        this.batch = new SpriteBatch();

        // Viewport que preenche toda a tela cortando excessos
        this.viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        // Stage com layout que mantém proporção
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public void show() {
        // Chamado quando a screen entra em uso (não usado aqui)
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com cor preta
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Aplica o viewport e ajusta a projeção da câmera
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Desenha o background (se existir)
        batch.begin();
        if (background != null) {
            batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        batch.end();

        // Atualiza e desenha o Stage (UI, botões, etc.)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Atualiza o viewport principal
        viewport.update(width, height, true);

        // Atualiza o viewport do stage (UI)
        if (stage != null)
            stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Chamado quando a aplicação pausa (não usado)
    }

    @Override
    public void resume() {
        // Chamado quando a aplicação volta do pause (não usado)
    }

    @Override
    public void hide() {
        // Chamado quando a screen deixa de ser exibida (não usado)
    }

    @Override
    public void dispose() {
        // Libera recursos gráficos
        batch.dispose();
        if (atlas != null)
            atlas.dispose();
        if (stage != null)
            stage.dispose();
    }

    public Viewport getViewport() {
        return viewport; // Permite que outras classes acessem o viewport
    }
}