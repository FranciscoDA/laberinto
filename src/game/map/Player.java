package game.map;

import game.core.Animation;
import game.core.Box;
import game.core.Collisionable;
import game.core.Direction;
import game.core.Drawable;
import game.core.InputComponent;
import game.core.Moveable;
import game.core.Shape;
import game.core.Spritesheet;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import state.GameState;

/**
 * La clase Player representa un personaje jugable dentro del mapa.
 * 
 * @author Francisco Altoe
 *
 */
public class Player extends MapObject implements Drawable, Moveable, Collisionable, KeyListener, Observer {
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
	private HashMap<String, Animation> animations;
	private Animation currentAnimation;
	private Animation nextAnimation;
	private boolean falling;

	public Player(int x, int y)
	{
		animations = new HashMap<String, Animation>();
		box = new Box(x, y, 32, 32);
		input = new InputComponent();
		input.setControl(KeyEvent.VK_LEFT, "left");
		input.setControl(KeyEvent.VK_RIGHT, "right");
		input.setControl(KeyEvent.VK_UP, "up");
		input.setControl(KeyEvent.VK_DOWN, "down");
		currentdir = null;
		nextdir = null;
		animations.put("walkSouth", spritesheet.createAnimation("walkSouth"));
		animations.put("walkNorth", spritesheet.createAnimation("walkNorth"));
		animations.put("walkWest", spritesheet.createAnimation("walkWest"));
		animations.put("walkEast", spritesheet.createAnimation("walkEast"));
		animations.put("standSouth", spritesheet.createAnimation("standSouth"));
		animations.put("standNorth", spritesheet.createAnimation("standNorth"));
		animations.put("standWest", spritesheet.createAnimation("standWest"));
		animations.put("standEast", spritesheet.createAnimation("standEast"));
		animations.put("fall", spritesheet.createAnimation("fall"));
		for (Animation a : animations.values())
			a.addObserver(this);

		currentAnimation = animations.get("standSouth");
		currentAnimation.start();
		nextAnimation = null;
		
		falling = false;
	}

	@Override
	public void move(GameState gs) {
		Map map = gs.getMap();
		if (box.getWest() % map.getTileWidth() == 0 && box.getNorth() % map.getTileHeight() == 0)
		{
			updateAnimation();
			updateDirection();
		}
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
		}
	}

	@Override
	public void draw(GameState gs, Graphics2D g2d) {
		if (currentAnimation != null)
		{
			Camera cam = gs.getCamera();
			currentAnimation.draw(g2d, box.getWest() - cam.getWest(), box.getNorth() - cam.getNorth());
			currentAnimation.advance();
		}
	}

	@Override
	public void onCollision(GameState gs, Collisionable other) {
		if (other instanceof EntityAbyss)
		{
			if (!falling)
			{
				falling = true;
				input.disable();
				nextAnimation = animations.get("fall");
				nextdir = null;
			}
		}
	}

	@Override
	public Shape getShape() {
		return box;
	}
	
	private void updateAnimation()
	{
		if (nextAnimation != null)
		{
			currentAnimation = nextAnimation;
			nextAnimation = null;
			currentAnimation.start();
		}
		else if (nextdir != currentdir)
		{
			if(nextdir == null)
			{
				switch (currentdir)
				{
				case EAST:
					currentAnimation = animations.get("standEast");
					break;
				case NORTH:
					currentAnimation = animations.get("standNorth");
					break;
				case SOUTH:
					currentAnimation = animations.get("standSouth");
					break;
				case WEST:
					currentAnimation = animations.get("standWest");
					break;
				default:
					break;
				}
			}
			else
			{
				switch (nextdir)
				{
				case EAST:
					currentAnimation = animations.get("walkEast");
					break;
				case NORTH:
					currentAnimation = animations.get("walkNorth");
					break;
				case SOUTH:
					currentAnimation = animations.get("walkSouth");
					break;
				case WEST:
					currentAnimation = animations.get("walkWest");
					break;
				default:
					break;
				}
			}
			currentAnimation.start();
		}
	}
	
	private void updateDirection ()
	{
		currentdir = nextdir;
		nextdir = null;
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
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 == animations.get("fall"))
		{
			currentAnimation = null;
		}
		
	}
	
}
