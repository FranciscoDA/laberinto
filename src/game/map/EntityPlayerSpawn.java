package game.map;

import game.core.Drawable;

import java.awt.Graphics2D;
import java.util.HashMap;

import state.GameState;

public class EntityPlayerSpawn extends Entity implements Drawable {
	private int tileX;
	private int tileY;
	public EntityPlayerSpawn()
	{
		
	}
	public EntityPlayerSpawn(String name, String triggers, int tx, int ty) {
		super(name, triggers);
		this.tileX = tx;
		this.tileY = ty;
	}

	@Override
	public Entity createEntity (Map map, HashMap<String, String> keyval)
	{
		String name = null;
		String triggers = null;

		String[] location = keyval.get("location").split(",");
		int x = (int) (Float.parseFloat(location[0]));
		int y = (int) (Float.parseFloat(location[1]));

		return new EntityPlayerSpawn(name, triggers, x, y);
	}

	@Override
	public void onGameStart(GameState gs) {
		int tw = gs.getMap().getTileWidth();
		int th = gs.getMap().getTileHeight();
		gs.getMap().getObjects().add(
			new Player(
				tileX * tw + tw / 2,
				tileY * th + th / 2
			)
		);
		
	}

	@Override
	public void trigger (GameState gs) {
	}
	
	@Override
	public void draw(GameState gs, Graphics2D g2d) {
		if (gs.getMap().isInFOV(tileX, tileY))
		{
			g2d.drawImage(spritesheet.getSprite("playerSpawn"),
					tileX * gs.getMap().getTileWidth() - gs.getCamera().getWest(),
					tileY * gs.getMap().getTileHeight() - gs.getCamera().getNorth(),
					null
			);
		}
	}
}
