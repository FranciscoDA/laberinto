package state.game;

import java.awt.Graphics2D;

public interface Drawable {
	/**
	 * dibuja el objeto en cuestion
	 * @param gs
	 * @param g2d
	 */
	public void draw (GameState gs, Graphics2D g2d);
}
