package game.map.entity;

import game.core.Box;
import game.core.Shape;
import game.map.Collisionable;
import game.map.Drawable;
import game.map.Map;
import game.map.Player;

import java.awt.Graphics2D;
import java.util.HashMap;

public class PlayerSpawn extends Entity implements Drawable, Collisionable {
	private Box box;
	public PlayerSpawn(Map map, HashMap<String, String> keyval) {
		super(map);
		this.name = keyval.get("name");
		this.triggers = keyval.get("triggers");
		String[] location = keyval.get("location").split(",");
		this.box = snapBoxToTiles(map,
			Float.parseFloat(location[0]), Float.parseFloat(location[1]),
			Float.parseFloat(location[2]), Float.parseFloat(location[3])
		);
	}

	@Override
	public void onGameStart() {
		map.addObject(new Player(map, box.getCenterX(), box.getCenterY()));
	}

	@Override
	public void trigger () { }
	
	@Override
	public void draw(Map map, Graphics2D g2d)
	{
		if (map.isInFOV(box.getCenterX() / map.getTileWidth(), box.getCenterY() / map.getTileHeight()))
			g2d.drawImage(spritesheet.getSprite("playerSpawn"), box.getWest(), box.getNorth(), null);
	}
	@Override
	public void onCollision(Map map, Collisionable other) {
	}
	@Override
	public Shape getShape() {
		return box;
	}
}
