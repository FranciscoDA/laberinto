package state.game.map;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import state.game.Collisionable;
import state.game.Drawable;
import state.game.GameState;
import state.game.Moveable;
import core.Animation;
import core.Box;
import core.Direction;
import core.InputComponent;
import core.Shape;
import core.Spritesheet;


public class Player extends MapObject implements Drawable, Moveable, Collisionable, KeyListener {
	static Spritesheet spritesheet;
	static
	{
		try
		{
			spritesheet = new Spritesheet();
			spritesheet.load("spritesheet/player.txt");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private Box box;
	private InputComponent input;
	private Direction currentdir;
	private Direction nextdir; //este atributo va a actuar como una suerte de buffer
	private Animation walkSouth;
	private Animation walkNorth;
	private Animation walkWest;
	private Animation walkEast;
	private Animation standSouth;
	private Animation standNorth;
	private Animation standWest;
	private Animation standEast;
	private Animation currentAnimation;

	public Player(int x, int y)
	{
		box = new Box(x, y, 32, 32);
		input = new InputComponent();
		input.setControl(KeyEvent.VK_LEFT, "left");
		input.setControl(KeyEvent.VK_RIGHT, "right");
		input.setControl(KeyEvent.VK_UP, "up");
		input.setControl(KeyEvent.VK_DOWN, "down");
		currentdir = null;
		walkSouth = spritesheet.createAnimation("walkSouth");
		walkNorth = spritesheet.createAnimation("walkNorth");
		walkWest = spritesheet.createAnimation("walkWest");
		walkEast = spritesheet.createAnimation("walkEast");
		standSouth = spritesheet.createAnimation("standSouth");
		standNorth = spritesheet.createAnimation("standNorth");
		standWest = spritesheet.createAnimation("standWest");
		standEast = spritesheet.createAnimation("standEast");
		
		currentAnimation = standSouth;
		currentAnimation.start();
	}

	@Override
	public void move(GameState gs) {
		Map map = gs.getMap();
		if (currentdir != null && currentdir.isOrthogonal())
		{
			Box newbox = new Box(
				box.getCenterX() + (int)currentdir.xFactor(), box.getCenterY() + (int)currentdir.yFactor(),
				box.getWidth(), box.getHeight()
			);
			if (map.isFloor(newbox.getWest() / map.getTileWidth(), newbox.getNorth() / map.getTileHeight()) &&
				map.isFloor((newbox.getEast()-1) / map.getTileWidth(), (newbox.getSouth()-1) / map.getTileHeight()))
			{
				box.setCenterX(box.getCenterX() + (int)currentdir.xFactor());
				box.setCenterY(box.getCenterY() + (int)currentdir.yFactor());
			}
			if (box.getWest() % map.getTileWidth() == 0 && box.getNorth() % map.getTileHeight() == 0)
			{
				if (currentdir != nextdir)
					stopDirection(currentdir);
				else
					nextdir = null;
			}
		}
		if (currentdir == null && nextdir != null)
		{
			changeDirection(nextdir);
			nextdir = null;
		}
		
	}

	@Override
	public void draw(GameState gs, Graphics2D g2d) {
		Camera cam = gs.getCamera();
		currentAnimation.draw(g2d, box.getWest() - cam.getWest(), box.getNorth() - cam.getNorth());
		currentAnimation.advance();
	}

	@Override
	public void onCollision(GameState gs, Collisionable other) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Shape getShape() {
		return box;
	}
	
	private void changeDirection (Direction newdir)
	{
		if (newdir != currentdir)
		{
			currentdir = newdir;
			if (currentdir == Direction.NORTH)
				currentAnimation = walkNorth;
			else if (currentdir == Direction.SOUTH)
				currentAnimation = walkSouth;
			else if (currentdir == Direction.WEST)
				currentAnimation = walkWest;
			else if (currentdir == Direction.EAST)
				currentAnimation = walkEast;
			currentAnimation.start();
		}
	}
	private void stopDirection (Direction dir)
	{
		if (dir == currentdir)
		{
			if (currentdir == Direction.NORTH)
				currentAnimation = standNorth;
			else if (currentdir == Direction.SOUTH)
				currentAnimation = standSouth;
			else if (currentdir == Direction.WEST)
				currentAnimation = standWest;
			else if (currentdir == Direction.EAST)
				currentAnimation = standEast;
			currentdir = null;
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (input.keyPressed(arg0))
		{
			String lastinteraction = input.getLastInteraction();
			switch (lastinteraction)
			{
			case "left":
				nextdir = Direction.WEST;
				break;
			case "right":
				nextdir = Direction.EAST;
				break;
			case "up":
				nextdir = Direction.NORTH;
				break;
			case "down":
				nextdir = Direction.SOUTH;
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		/*if(input.keyReleased(arg0))
		{
			String lastinteraction = input.getLastInteraction();
			switch (lastinteraction)
			{
			case "left":
				stopDirection(Direction.WEST);
				break;
			case "right":
				stopDirection(Direction.EAST);
				break;
			case "up":
				stopDirection(Direction.NORTH);
				break;
			case "down":
				stopDirection(Direction.SOUTH);
				break;
			}
		}*/
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
}
