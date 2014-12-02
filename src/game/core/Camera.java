package game.core;

import java.awt.Graphics2D;


/**
 * La clase Camera representa lo que el jugador deberia ver dentro del juego,
 * por lo tanto, todos los objetos que se dibujen en pantalla deberian tener
 * en cuenta la posicion de la camara.
 * 
 * 
 * @author Francisco Altoe
 */
public class Camera {
	private Box box;
	private Shape parent;
	
	public Camera (int x, int y, int w, int h)
	{
		box = new Box(x,y,w,h);
		parent = null;
	}

	public void lock (Shape p)
	{
		parent = p;
	}
	public void unlock()
	{
		parent = null;
	}
	
	public int getCenterX()
	{
		if (parent != null)
			return parent.getCenterX() - box.getCenterX();
		else
			return box.getCenterX();
	}
	public int getWest()
	{
		if (parent != null)
			return parent.getCenterX() + box.getWest();
		else
			return box.getWest();
	}
	public int getEast()
	{
		if (parent != null)
			return parent.getCenterX() + box.getEast();
		else
			return box.getEast();
	}
	
	public int getCenterY()
	{
		if (parent != null)
			return parent.getCenterY() + box.getCenterY();
		else
			return box.getCenterY();
	}
	public int getNorth()
	{
		if (parent != null)
			return parent.getCenterY() + box.getNorth();
		else
			return box.getNorth();
	}
	public int getSouth()
	{
		if (parent != null)
			return parent.getCenterY() + box.getSouth();
		else
			return box.getSouth();
	}
	public int getWidth()
	{
		return getEast() - getWest();
	}
	public int getHeight()
	{
		return getSouth() - getNorth();
	}

	public Graphics2D transform (Graphics2D g2d)
	{
		g2d.translate(-this.getWest(), -this.getNorth());
		return g2d;
	}
}
