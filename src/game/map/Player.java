package game.map;

import game.core.Animation;
import game.core.Box;
import game.core.Direction;
import game.core.InputComponent;
import game.core.Shape;
import game.core.Spritesheet;
import game.map.entity.Abyss;
import game.map.entity.PlayerExit;
import game.map.entity.PlayerSpawn;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

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
		spritesheet = new Spritesheet();
		spritesheet.load("spritesheet/player.txt");
	}
	
	private Box box;
	private InputComponent input;
	private Direction currentDirection;
	private Direction nextDirection; //este atributo va a actuar como una suerte de buffer
	private HashMap<String, Animation> animations;
	private Animation currentAnimation;
	private Animation nextAnimation;
	private boolean canCollide;
	private boolean hasTrophy;

	public Player(int x, int y)
	{
		animations = new HashMap<String, Animation>();
		box = new Box(x, y, 32, 32);
		input = new InputComponent();
		input.setControl(KeyEvent.VK_LEFT, "left");
		input.setControl(KeyEvent.VK_RIGHT, "right");
		input.setControl(KeyEvent.VK_UP, "up");
		input.setControl(KeyEvent.VK_DOWN, "down");
		currentDirection = null;
		nextDirection = null;
		for (String s : new String[] {"walkSouth", "walkNorth", "walkEast", "walkWest", "standSouth", "standNorth", "standWest", "standEast", "fall", "win"})
		{
			Animation anim = spritesheet.createAnimation(s);
			animations.put(s, anim);
			anim.addObserver(this);
		}

		currentAnimation = animations.get("standSouth");
		currentAnimation.start();
		nextAnimation = null;
		canCollide = true;
		hasTrophy = false;
	}

	@Override
	public void move(Map map) {
		if (box.getWest() % map.getTileWidth() == 0 && box.getNorth() % map.getTileHeight() == 0)
		{
			updateAnimation();
			updateDirection();
		}
		if (currentDirection != null && currentDirection.isOrthogonal())
		{
			Box newbox = new Box(
				box.getCenterX() + (int)currentDirection.xFactor(), box.getCenterY() + (int)currentDirection.yFactor(),
				box.getWidth(), box.getHeight()
			);
			if (map.isFloor(newbox.getWest() / map.getTileWidth(), newbox.getNorth() / map.getTileHeight()) &&
				map.isFloor((newbox.getEast()-1) / map.getTileWidth(), (newbox.getSouth()-1) / map.getTileHeight()))
			{
				box.setCenterX(box.getCenterX() + (int)currentDirection.xFactor());
				box.setCenterY(box.getCenterY() + (int)currentDirection.yFactor());
			}
		}
	}

	@Override
	public void draw(Map map, Graphics2D g2d) {
		if (currentAnimation != null)
		{
			currentAnimation.draw(g2d, box.getWest(), box.getNorth());
			currentAnimation.advance();
		}
	}

	@Override
	public void onCollision(Map map, Collisionable other) {
		if (other instanceof Abyss)
		{
			if (canCollide)
			{
				nextDirection = null;
				input.disable();
				nextAnimation = animations.get("fall");
				canCollide = false;
			}
		}
		else if (other instanceof PlayerExit)
		{
			hasTrophy = true;
		}
		else if (other instanceof PlayerSpawn)
		{
			if (canCollide && hasTrophy)
			{
				nextDirection = null;
				input.disable();
				nextAnimation = animations.get("win");
				canCollide = false;
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
		else if (nextDirection != currentDirection)
		{
			if(nextDirection == null)
			{
				switch (currentDirection)
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
				switch (nextDirection)
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
		currentDirection = nextDirection;
		//nextDirection = null;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (input.keyPressed(arg0))
		{
			String lastinteraction = input.getLastInteraction();
			if (lastinteraction.equals("left"))
				nextDirection = Direction.WEST;
			else if (lastinteraction.equals("right"))
				nextDirection = Direction.EAST;
			else if (lastinteraction.equals("up"))
				nextDirection = Direction.NORTH;
			else if (lastinteraction.equals("down"))
				nextDirection = Direction.SOUTH;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (input.keyReleased(arg0))
		{
			String lastinteraction = input.getLastInteraction();
			if (currentDirection == nextDirection)
			{
				if (lastinteraction.equals("left"))
					nextDirection = null;
				else if (lastinteraction.equals("right"))
					nextDirection = null;
				else if (lastinteraction.equals("up"))
					nextDirection = null;
				else if (lastinteraction.equals("down"))
					nextDirection = null;
			}
		}
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
