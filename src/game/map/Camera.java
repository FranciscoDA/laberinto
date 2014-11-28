package game.map;

import game.core.Box;
import game.core.Moveable;
import game.core.Shape;
import state.GameState;

/**
 * La clase Camera representa lo que el jugador deberia ver dentro del juego,
 * por lo tanto, todos los objetos que se dibujen en pantalla deberian tener
 * en cuenta la posicion de la camara.
 * 
 * 
 * @author Francisco Altoe
 */
public class Camera implements Moveable {
	private Box box;
	private Shape parent;
	private float xv;
	private float yv;
	
	public Camera (int x, int y, int w, int h)
	{
		box = new Box(x,y,w,h);
		setHorizontalVelocity(0.0f);
		setVerticalVelocity(0.0f);
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

	@Override
	public void move (GameState gs)
	{
		box.setCenterX((int) (box.getCenterX() + getHorizontalVelocity()));
		box.setCenterY((int) (box.getCenterY() + getVerticalVelocity()));
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

	public void setHorizontalVelocity(float xv) {
		this.xv = xv;
	}
	public void setVerticalVelocity(float yv) {
		this.yv = yv;
	}
	public float getHorizontalVelocity() {
		return xv;
	}
	public float getVerticalVelocity() {
		return yv;
	}
}
