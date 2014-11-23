package state.game;

import core.Shape;

public interface Collisionable {
	/**
	 * Procesa la colision con otro colisionable
	 * @param gs
	 * @param other
	 */
	public void onCollision (GameState gs, Collisionable other);
	public Shape getShape();
}
