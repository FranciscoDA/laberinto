package state.game.map;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.ListIterator;

import state.game.Collisionable;
import state.game.Drawable;
import state.game.GameState;
import state.game.Moveable;
import core.Shape;

public class ObjectManager {
	public ObjectManager()
	{
		addList = new LinkedList<MapObject>();
		killList = new LinkedList<MapObject>();
		objects = new LinkedList<MapObject>(); 
	}
	
	private void killBatch()
	{
		for (MapObject k : killList)
		{
			objects.remove(k);
		}
		killList.clear();
	}
	private void addBatch()
	{
		for (MapObject a : addList)
		{
			objects.add(a);
			if (a instanceof Player)
				player = (Player) a;
		}
		addList.clear();
	}
	public void logic(GameState gs)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Moveable)
				((Moveable) o).move(gs);
		}
		
		ListIterator<MapObject> oit = objects.listIterator();
		while (oit.hasNext())
		{
			MapObject o = oit.next();
			if (o instanceof Collisionable)
			{
				ListIterator<MapObject> pit = objects.listIterator(oit.nextIndex());
				while (pit.hasNext())
				{
					MapObject p = pit.next();
					if (p instanceof Collisionable)
					{
						Shape oshape = ((Collisionable) o).getShape();
						Shape pshape = ((Collisionable) p).getShape();
						if (oshape.collides(pshape) || pshape.collides(oshape))
						{
							System.out.println("collision: " + o + " , " + p);
							((Collisionable) o).onCollision(gs, (Collisionable) p);
							((Collisionable) p).onCollision(gs, (Collisionable) o);
						}
					}
				}
			}
		}
		killBatch();
		addBatch();
	}
	
	public void draw(GameState gs, Graphics2D g2d)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Drawable)
			{
				((Drawable) o).draw(gs, g2d);
			}
		}
	}
	
	public void add(MapObject o)
	{
		addList.add(o);
	}
	
	public void kill (MapObject o)
	{
		killList.add(o);
	}
	
	public void trigger (GameState gs, String name)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Entity)
			{
				Entity e = (Entity) o;
				if (e.getName() != null && e.getName().equals(name))
				{
					e.trigger(gs);
				}
			}
		}
		killBatch();
		addBatch();
	}
	
	public void startGame(GameState gs)
	{
		addBatch();
		for (MapObject o : objects)
		{
			if (o instanceof Entity)
				((Entity) o).onGameStart(gs);
		}
		addBatch();
	}
	
	public Player getPlayer()
	{
		return player;
	}

	private Player player;
	private LinkedList<MapObject> addList;
	private LinkedList<MapObject> killList;
	private LinkedList<MapObject> objects;
}
