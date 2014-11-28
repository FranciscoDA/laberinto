package game.core;

public interface Shape {
	public boolean collides(Shape other);
	public int getCenterX();
	public int getCenterY();
	public void setCenterX(int cx);
	public void setCenterY(int cy);
}
