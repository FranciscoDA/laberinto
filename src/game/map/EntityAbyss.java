package game.map;

import game.core.Box;
import game.core.Collisionable;
import game.core.Drawable;
import game.core.Shape;

import java.awt.Graphics2D;
import java.util.HashMap;

import state.GameState;

public class EntityAbyss extends Entity implements Collisionable, Drawable {
	private Box box;

	public EntityAbyss()
	{	
	}

	private EntityAbyss(String name, String triggers, Box box) {
		super(name, triggers);
		this.box = box;
	}

	@Override
	public Entity createEntity (Map map, HashMap<String, String> keyval)
	{
		String name = null;
		String triggers = null;
		String[] location = keyval.get("location").split(",");
		return new EntityAbyss(
			name, triggers,
			snapBoxToTiles(map,
				Float.parseFloat(location[0]),Float.parseFloat(location[1]),
				Float.parseFloat(location[2]),Float.parseFloat(location[3])
			)
		);
	}

	@Override
	public void onGameStart(GameState gs) {
	}

	@Override
	public void trigger(GameState gs) {
	}

	@Override
	public void onCollision(GameState gs, Collisionable other) {
	}

	@Override
	public void draw(GameState gs, Graphics2D g2d) {
		Map map = gs.getMap();
		int destx = box.getWest() - gs.getCamera().getWest();
		int desty = box.getNorth() - gs.getCamera().getNorth();
		for (int x = 0; x < box.getWidth(); x += map.getTileWidth())
			for (int y = 0; y < box.getHeight(); y += map.getTileHeight())
				if (map.isInFOV(map.xPixelsToTiles(box.getWest()+x), map.yPixelsToTiles(box.getNorth()+y)))
					g2d.drawImage(spritesheet.getSprite("abyss"), destx+x, desty+y, null);
	}

	@Override
	public Shape getShape() {
		return box;
	}
}
