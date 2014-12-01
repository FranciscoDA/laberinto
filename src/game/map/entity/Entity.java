package game.map.entity;

import game.core.Box;
import game.core.Spritesheet;
import game.map.Map;
import game.map.MapObject;

/**
 * La clase Entity representa objetos ya definidos en el archivo del mapa
 * además, soportan un mecanismo de name/triggers aunque no se usa 
 * 
 * @author Francisco Altoe
 *
 */
public abstract class Entity extends MapObject{
	
	static Spritesheet spritesheet;
	static {
		spritesheet = new Spritesheet();
		spritesheet.load("spritesheet/entities.txt");
	}
	
	protected String name;
	protected String triggers;

	public abstract void onGameStart(Map map);
	public abstract void trigger(Map map);
	
	public String getName()
	{
		return name;
	}
	
	public String getTriggers()
	{
		return triggers;
	}
	
	/**
	 * Esta es una funcion de ayuda para las entidades derivadas cuyas
	 * formas correspondan a un Box.
	 * 
	 * @param map El mapa en cuestion
	 * @param x La posicion x dada por el archivo (en fracciones de cuadrados)
	 * @param y La posicion y dada por el archivo (en fracciones de cuadrados)
	 * @param w El ancho dado por el archivo (en fracciones de cuadrados)
	 * @param h El alto dado por el archivo (en fracciones de cuadrados)
	 * @return Un objecto Box cuyos lados coinciden con los bordes de los cuadrados del mapa
	 */
	protected Box snapBoxToTiles(Map map, float x, float y, float w, float h)
	{
		int tw = map.getTileWidth();
		int th = map.getTileHeight();
		
		x = x * tw;
		y = y * th;
		w = w * tw;
		h = h * th;
		int leftTile = map.xPixelsToTiles((int) x);
		int rightTile = map.xPixelsToTiles((int) (x+w));
		int topTile = map.yPixelsToTiles((int) y);
		int bottomTile = map.yPixelsToTiles((int) (y+h));
		return new Box (
			(leftTile+rightTile+1)*tw/2 , (topTile+bottomTile+1) * tw/2,
			(rightTile-leftTile+1) * tw, (bottomTile-topTile+1) * th
		);
	}
}
