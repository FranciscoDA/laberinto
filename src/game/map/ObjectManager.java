package game.map;

import game.core.Shape;
import game.map.entity.Entity;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * La clase ObjectManager administra la logica y los graficos de los
 * objetos del mapa en conjunto.
 * 
 * @author Francisco Altoe
 *
 */
public class ObjectManager {
	public ObjectManager()
	{
		addList = new LinkedList<MapObject>();
		killList = new LinkedList<MapObject>();
		objects = new LinkedList<MapObject>();
		player = null;
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
	public void logic(Map map)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Moveable)
				((Moveable) o).move(map);
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
						if (oshape != null && pshape != null)
						{
							if (oshape.collides(pshape) || pshape.collides(oshape))
							{
								((Collisionable) o).onCollision(map, (Collisionable) p);
								((Collisionable) p).onCollision(map, (Collisionable) o);
							}
						}
					}
				}
			}
		}
		killBatch();
		addBatch();
	}
	
	public void draw(Map map, Graphics2D g2d)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Drawable)
			{
				((Drawable) o).draw(map, g2d);
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
	
	public void trigger (Map map, String name)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Entity)
			{
				Entity e = (Entity) o;
				if (e.getName() != null && e.getName().equals(name))
				{
					e.trigger(map);
				}
			}
		}
		killBatch();
		addBatch();
	}
	
	public void startGame(Map map)
	{
		addBatch();
		for (MapObject o : objects)
		{
			if (o instanceof Entity)
				((Entity) o).onGameStart(map);
		}
		addBatch();
	}
	
	public void keyPressed(KeyEvent arg0)
	{
		for (MapObject o : objects)
		{
			if (o instanceof KeyListener)
				((KeyListener) o).keyPressed(arg0);
		}
	}
	public void keyReleased(KeyEvent arg0)
	{
		for (MapObject o : objects)
		{
			if (o instanceof KeyListener)
				((KeyListener) o).keyReleased(arg0);
		}
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
