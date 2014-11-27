package game.map;

import game.core.Box;
import game.core.Collisionable;
import game.core.Direction;
import game.core.Drawable;
import game.core.Shape;

import java.awt.Graphics2D;
import java.util.HashMap;

import state.GameState;

public class EntityPlayerExit extends Entity implements Collisionable, Drawable {
	private Box box;
	private Direction orientation;

	public EntityPlayerExit()
	{
	}

	private EntityPlayerExit(String name, String triggers, Box box, Direction orientation)
	{
		super(name, triggers);
		this.box = box;
		this.orientation = orientation;
	}

	@Override
	public void draw(GameState gs, Graphics2D g2d) {
		int destx = box.getWest() - gs.getCamera().getWest();
		int desty = box.getNorth() - gs.getCamera().getNorth();
		if (gs.getMap().isInFOV(gs.getMap().xPixelsToTiles(box.getWest()), gs.getMap().yPixelsToTiles(box.getNorth())))
			switch (orientation)
			{
			case EAST:
				g2d.drawImage(spritesheet.getSprite("playerExitEast"), destx, desty, null);
				break;
			case NORTH:
				g2d.drawImage(spritesheet.getSprite("playerExitNorth"), destx, desty, null);
				break;
			case SOUTH:
				g2d.drawImage(spritesheet.getSprite("playerExitSouth"), destx, desty, null);
				break;
			case WEST:
				g2d.drawImage(spritesheet.getSprite("playerExitWest"), destx, desty, null);
				break;
			default:
				break;
			}
	}

	@Override
	public void onCollision(GameState gs, Collisionable other) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Shape getShape() {
		return box;
	}

	@Override
	public Entity createEntity(Map map, HashMap<String, String> keyval) {
		String name = keyval.get("name");
		String triggers = keyval.get("triggers");
		String[] location = keyval.get("location").split(",");
		Box b = snapBoxToTiles(map,
				Float.parseFloat(location[0]), Float.parseFloat(location[1]),
				Float.parseFloat(location[2]), Float.parseFloat(location[3]));
		String dirstr = keyval.get("orientation");
		Direction dir = null;
		switch (dirstr)
		{
		case "East":
			dir = Direction.EAST;
			break;
		case "West":
			dir = Direction.WEST;
			break;
		case "North":
			dir = Direction.NORTH;
			break;
		case "South":
			dir = Direction.SOUTH;
			break;
		}
		return new EntityPlayerExit(name, triggers, b, dir);
	}

	@Override
	public void onGameStart(GameState gs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trigger(GameState gs) {
		// TODO Auto-generated method stub
		
	}

}
