package com.seek_knowledge.game.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Map {
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    // Construtor: carrega o primeiro mapa baseado no personagem
    public Map(String characterName, OrthographicCamera camera) {
        this.mapLoader = new TmxMapLoader();

        // Carrega o mapa inicial (World1)
        this.map = mapLoader.load("maps/" + characterName + "_World1.tmx");

        // Renderer responsável por desenhar o mapa na tela
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // Câmera usada para exibir o mapa
        this.camera = camera;
    }

    // Troca o mapa atual para outro (por fase/mundo)
    public void changeMap(String fileName, Integer currentIndex) {
        // Carrega o novo mapa baseado no nome do personagem + world
        TiledMap newMap = mapLoader.load("maps/" + fileName + "_World" + currentIndex + ".tmx");

        // Atualiza o renderer para usar o novo mapa
        mapRenderer.setMap(newMap);

        // Libera memória do mapa anterior
        map.dispose();

        // Guarda referência do novo mapa
        map = newMap;
    }

    // Renderiza o mapa na tela
    public void render() {
        camera.update(); // Atualiza posição/zoom da câmera
        mapRenderer.setView(camera); // Diz ao renderer qual câmera usar
        mapRenderer.render(); // Desenha o mapa completo
    }

    // Libera todos os recursos
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

}
