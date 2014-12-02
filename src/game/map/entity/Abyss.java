package game.map.entity;

import game.core.Box;
import game.core.Shape;
import game.map.Collisionable;
import game.map.Drawable;
import game.map.Map;

import java.awt.Graphics2D;
import java.util.HashMap;

public class Abyss extends Entity implements Collisionable, Drawable {
	private Box box;

	public Abyss(Map map, HashMap<String, String> keyval) {
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
	public void onGameStart() {	}

	@Override
	public void trigger() {	}

	@Override
	public void onCollision(Map map, Collisionable other) {
	}

	@Override
	public void draw(Map map, Graphics2D g2d) {
		for (int x = 0; x < box.getWidth(); x += map.getTileWidth())
			for (int y = 0; y < box.getHeight(); y += map.getTileHeight())
				if (map.isInFOV((box.getWest()+x) / map.getTileWidth(), (box.getNorth()+y) / map.getTileHeight()))
					g2d.drawImage(spritesheet.getSprite("abyss"), box.getWest()+x, box.getNorth()+y, null);
	}

	@Override
	public Shape getShape() {
		return box;
	}
}
