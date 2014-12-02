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
 * Tambien administra la alta y baja de objetos para evitar errores
 * de escritura concurrente
 * 
 * @author Francisco Altoe
 */
public class ObjectManager {
	private Map map;
	private LinkedList<MapObject> addList;
	private LinkedList<MapObject> killList;
	private LinkedList<MapObject> objects;
	
	public ObjectManager(Map map)
	{
		this.map = map;
		this.addList = new LinkedList<MapObject>();
		this.killList = new LinkedList<MapObject>();
		this.objects = new LinkedList<MapObject>();
	}
	
	private void killBatch()
	{
		for (MapObject k : killList)
			objects.remove(k);
		killList.clear();
	}
	private void addBatch()
	{
		for (MapObject a : addList)
			objects.add(a);
		addList.clear();
	}
	public void logic()
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
	
	public void draw(Graphics2D g2d)
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
	
	public void trigger (String name)
	{
		for (MapObject o : objects)
		{
			if (o instanceof Entity)
			{
				Entity e = (Entity) o;
				if (e.getName() != null && e.getName().equals(name))
				{
					e.trigger();
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
				((Entity) o).onGameStart();
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
}
