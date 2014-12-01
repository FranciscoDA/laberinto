package game.map;

import game.core.Shape;

public interface Collisionable {
	/**
	 * Procesa la colision con otro colisionable
	 * @param gs
	 * @param other
	 */
	public void onCollision (Map map, Collisionable other);
	public Shape getShape();
}
