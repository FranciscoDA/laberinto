package game.core;

public class Point implements Shape {
	protected int x;
	protected int y;

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public int getCenterX()
	{
		return x;
	}
	public int getCenterY()
	{
		return y;
	}
	public void setCenterX(int nx)
	{
		x = nx;
	}
	public void setCenterY(int ny)
	{
		y = ny;
	}

	@Override
	public boolean collides(Shape other) {
		if (other instanceof Point)
		{
			Point p = (Point) other;
			return getCenterX() == p.getCenterX() && getCenterY() == p.getCenterY();
		}
		return false;
	}
}
