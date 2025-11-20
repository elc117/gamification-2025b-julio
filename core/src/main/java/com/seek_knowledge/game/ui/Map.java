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

    public Map(String characterName, OrthographicCamera camera) {
        this.mapLoader = new TmxMapLoader();
        this.map = mapLoader.load("maps/" + characterName + "_World1.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        this.camera = camera;
    }

    public void changeMap(String fileName, Integer currentIndex) {
        TiledMap newMap = mapLoader.load("maps/" + fileName + "_World" + currentIndex + ".tmx");
        mapRenderer.setMap(newMap);
        map.dispose();
        map = newMap;
    }

    public void render() {
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

}
