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
	public PlayerSpawn()
	{
		
	}
	public PlayerSpawn(Map map, HashMap<String, String> keyval) {
		this.name = keyval.get("name");
		this.triggers = keyval.get("triggers");
		String[] location = keyval.get("location").split(",");
		this.box = snapBoxToTiles(map,
			Float.parseFloat(location[0]), Float.parseFloat(location[1]),
			Float.parseFloat(location[2]), Float.parseFloat(location[3])
		);
	}

	@Override
	public void onGameStart(Map map) {
		map.getObjects().add(
			new Player(
				box.getCenterX(),
				box.getCenterY()
			)
		);
		
	}

	@Override
	public void trigger (Map map) {
	}
	
	@Override
	public void draw(Map map, Graphics2D g2d)
	{
		if (map.isInFOV(map.xPixelsToTiles(box.getCenterX()), map.yPixelsToTiles(box.getCenterY())))
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
