package com.seek_knowledge.game.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.MapObject;

public class Ground {
    BodyDef bodyDef = new BodyDef();
    PolygonShape shape = new PolygonShape();
    FixtureDef fixtureDef = new FixtureDef();
    Body body;

    public Ground(World world, MapObject object) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
        body = world.createBody(bodyDef);

        shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }
}
