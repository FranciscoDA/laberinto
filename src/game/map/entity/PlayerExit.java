package game.map.entity;

import game.core.Box;
import game.core.Shape;
import game.map.Collisionable;
import game.map.Drawable;
import game.map.Map;
import game.map.Player;

import java.awt.Graphics2D;
import java.util.HashMap;

public class PlayerExit extends Entity implements Collisionable, Drawable {
	private Box box;
	boolean used;

	public PlayerExit (Map map, HashMap<String, String> keyval) {
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
	public void draw(Map map, Graphics2D g2d) {
		if (map.isInFOV(box.getCenterX() / map.getTileWidth(),box.getCenterY() / map.getTileHeight()))
			if(!used)
				g2d.drawImage(spritesheet.getSprite("playerExit"), box.getWest(), box.getNorth(), null);
			else
				g2d.drawImage(spritesheet.getSprite("playerExitUsed"), box.getWest(), box.getNorth(), null);
	}

	@Override
	public void onCollision(Map map, Collisionable other) {
		if (other instanceof Player)
			used = true;
	}

	@Override
	public Shape getShape() {
		return box;
	}

	@Override
	public void onGameStart() { }

	@Override
	public void trigger() { }

}
