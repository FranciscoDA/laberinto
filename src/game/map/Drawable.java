package game.map;

import java.awt.Graphics2D;

public interface Drawable {
	/**
	 * dibuja el objeto en cuestion
	 * @param map El mapa del juego
	 * @param g2d El objeto Graphics2D del motor awt
	 */
	public void draw (Map map, Graphics2D g2d);
}
