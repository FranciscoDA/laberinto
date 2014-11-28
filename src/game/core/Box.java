package game.core;

public class Box implements Shape {
	private int cx;
	private int cy;
	private int w;
	private int h;

	public Box (int x, int y, int w, int h)
	{
		cx = x;
		cy = y;
		this.w = w;
		this.h = h;
	}
	public int getWidth()
	{
		return w;
	}
	public int getHeight()
	{
		return h;
	}
	public int getWest()
	{
		return cx - getWidth() / 2;
	}
	public int getEast()
	{
		return cx + getWidth() / 2;
	}
	public int getNorth()
	{
		return cy - getHeight() / 2;
	}
	public int getSouth()
	{
		return cy + getHeight() / 2;
	}
	@Override
	public int getCenterX() {
		return cx;
	}
	@Override
	public int getCenterY() {
		return cy;
	}
	@Override
	public void setCenterX(int cx) {
		this.cx = cx;
	}
	@Override
	public void setCenterY(int cy) {
		this.cy = cy;
	}
	
	@Override
	public boolean collides(Shape other)
	{
		if (other instanceof Box)
		{
			Box b = (Box) other;
			// en este caso es mas sencillo preguntar si no hay colision
			if (b.getEast() <= getWest() || getEast() <= b.getWest() || b.getSouth() <= getNorth() || getSouth() <= b.getNorth())
				return false;
			return true;
		}
		if (other instanceof Point)
		{
			Point p = (Point) other;
			if (getWest() < p.getCenterX() && p.getCenterX() < getEast())
				if (getNorth() < getCenterY() && p.getCenterY() < getSouth())
					return true;
		}
		
		return false;
	}
}
