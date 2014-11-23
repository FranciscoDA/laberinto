package state.game.map;

import java.awt.Graphics2D;
import java.util.HashMap;

import state.game.Collisionable;
import state.game.Drawable;
import state.game.GameState;
import core.Box;
import core.Shape;

public class EntityAbyss extends Entity implements Collisionable, Drawable {
	private Box box;

	public EntityAbyss()
	{	
	}
	public EntityAbyss(String name, String triggers, Box shape) {
		super(name, triggers);
		box = shape;
	}

	/* Este tipo de entidad tiene que ocupar cuadrados enteros,
	 * asi que corresponden hacer algunos ajustes.
	 * El formato flare map exportado por tiled no ayuda mucho
	 * tampoco...
	 */
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
				g2d.drawImage(spritesheet.getSprite("abyss"),
						destx+x, desty+y, null);
	}

	@Override
	public Shape getShape() {
		return box;
	}
}
