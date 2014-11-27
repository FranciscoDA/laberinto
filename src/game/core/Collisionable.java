package game.core;

import state.GameState;

public interface Collisionable {
	/**
	 * Procesa la colision con otro colisionable
	 * @param gs
	 * @param other
	 */
	public void onCollision (GameState gs, Collisionable other);
	public Shape getShape();
}
