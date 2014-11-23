package state.game.map;


import java.awt.Graphics2D;

import state.game.GameState;
import core.Box;
import core.Direction;

/**
 * La clase Map contiene la informacion de los cuadrados del mapa
 * cargado de un archivo y su distribucion.
 * Tambien contiene los objetos y el campo de vision.
 * 
 * @author Francisco Altoe
 *
 */
public class Map implements state.game.Drawable {

	private int width;
	private int height;
	
	private Tileset tileset;
	private ObjectManager objects;
	private boolean[] calculatedFOV;
	private byte[] floorTiles;
	private byte[] wallTiles;

	public Map ()
	{
		setDimensions (0,0);
		objects = new ObjectManager();
		tileset = null;
	}
	
	public void setDimensions (int nw, int nh)
	{
		width = nw;
		height = nh;
		floorTiles = new byte[nw*nh];
		wallTiles = new byte[nw*nh];
		calculatedFOV = new boolean[nw*nh];
		for (int i = 0; i < nw*nh; i++)
		{
			floorTiles[i] = 0;
			wallTiles[i] = 0;
			calculatedFOV[i] = false;
		}
	}
	public ObjectManager getObjects() { return objects; }
	public void setTileSet (Tileset ts) { tileset = ts; }
	public Tileset getTileSet () { return tileset; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getTileWidth() { return getTileSet().getTileWidth(); }
	public int getTileHeight() { return getTileSet().getTileHeight(); }
	public int xPixelsToTiles(int pixels) { return pixels / getTileWidth(); }
	public int yPixelsToTiles(int pixels) { return pixels / getTileHeight(); }
	public boolean inBounds (int x, int y) { return x>=0 && x<getWidth() && y>=0 && y<getHeight(); }
	
	public Tile getTile(int x, int y)
	{
		int idx = y * getWidth() + x;
		if (wallTiles[idx] != 0)
			return tileset.getTile((int)wallTiles[idx]);
		return tileset.getTile((int)floorTiles[idx]);
	}
	public boolean isWall(int x, int y)
	{
		return wallTiles[y * getWidth() + x] != 0;
	}
	public boolean isFloor(int x, int y)
	{
		return (wallTiles[y * getWidth() + x] == 0 && floorTiles[y*getWidth()+x] != 0);
	}
	public void setWall (int x, int y, byte t)
	{
		wallTiles[y * getWidth() + x] = t;
	}
	public void setFloor (int x, int y, byte t)
	{
		floorTiles[y * getWidth() + x] = t;
	}
	public boolean isInFOV(int x, int y)
	{
		return calculatedFOV[y * getWidth() + x];
	}
	public boolean addToFOV(int x, int y)
	{
		boolean old = calculatedFOV[y * getWidth() + x];
		calculatedFOV[y * getWidth() + x] = true;
		return old;
	}

	/**
	 * Traza una recta imaginaria desde la posicion inicial hasta el destino
	 * @param startx posicion x inicial en pixeles
	 * @param starty posicion y inicial en pixeles
	 * @param destx posicion x destino en px
	 * @param desty posicion y destino en px
	 * @param fov arreglo con el campo de vision computado hasta el momento
	 * @return true si no hay niguna pared entre los puntos, false de otra manera
	 */
	private boolean lineCast(int startx, int starty, int destx, int desty)
	{
		double dx = destx - startx;
		double dy = desty - starty;
		double distance = Math.max(Math.abs(dx), Math.abs(dy));
		/* podemos parametrizar una recta de la sgte manera
			C(t) = { X = x0 + t * dx,  Y = y0 + t * dy }
			0 <= t <= 1 (con t real)
			dado que t puede tomar un conjunto de valores infinito y en este caso
			solo interesa tomar puntos discretos, tomaremos la distancia mayor entre
			dx y dy para evaluar una cantidad razonable de puntos de la recta
		*/
		for (int t = 0; t < distance; t++)
		{
			int x = xPixelsToTiles((int) (startx + t * dx/distance));
			int y = yPixelsToTiles((int) (starty + t * dy/distance));
			if (x != xPixelsToTiles(startx) || y != yPixelsToTiles(starty))
				if (x != xPixelsToTiles(destx) || y != yPixelsToTiles(desty))
					if (isWall(x, y))
						return false;
		}
		return true;
	}
	/**
	 * Busca todos los cuadrados visibles para el jugador utilizando un algoritmo de shadow casting
	 * shadow casting: un cuadrado de destino es visible desde un cuadrado origen si algun punto
	 * del cuadrado de destino puede unirse con una recta al centro del cuadrado origen.
	 * Para este algoritmo solo se prueban las 4 esquinas del cuadrado de destino, sin embargo,
	 * dado que los cuadrados del mapa se dividen en 4 partes iguales, debemos hacer 4*4=16 pruebas:
	 * 4 por las esquinas del cuadrado destino y 4 por los 4 pixeles que conforman el centro del
	 * cuadrado origen.
	 * 
	 * @param startx posicion x inicial en tiles
	 * @param starty posicion y inicial en tiles
	 * @param fov arreglo con el campo de vision computado hasta el momento
	 * @param currentX posicion x actual en tiles
	 * @param currentY posicion y actual en tiles
	 */
	private void shadowCast(int startx, int starty, int currentx, int currenty)
	{
		int centerStartx = startx * getTileWidth() + getTileWidth() / 2;
		int centerStarty = starty * getTileHeight() + getTileHeight() / 2;
		boolean line = false;
		for (int i = 0; (i < 4) && !line; i++)
			for (int j = 0; (j < 4) && !line; j++)
				line = line || lineCast(
					centerStartx - j/2, centerStarty - j%2,
					(currentx+i/2)*getTileWidth()-i/2, (currenty+i%2)*getTileHeight()-i%2);
			
		if (line)
		{
			addToFOV(currentx, currenty);
			if (isFloor(currentx, currenty))
				for (Direction nextdir : Direction.values())
					if (nextdir.isOrthogonal())
						if (inBounds(currentx + (int)nextdir.xFactor(), currenty + (int)nextdir.yFactor()))
							if (!isInFOV(currentx + (int)nextdir.xFactor(), currenty + (int)nextdir.yFactor()))
								shadowCast(startx, starty, currentx+(int)nextdir.xFactor(), currenty+(int)nextdir.yFactor());
		}
	}
	
	@Override
	public void draw(GameState gs, Graphics2D g2d)
	{
		Camera cam = gs.getCamera();
		Player player = objects.getPlayer();
		if (player != null)
		{
			for (int i = 0; i < calculatedFOV.length; i++)
				calculatedFOV[i] = false;
			Box pbox = (Box) objects.getPlayer().getShape();
			int xx = xPixelsToTiles(pbox.getCenterX());
			int yy = yPixelsToTiles(pbox.getCenterY());
			shadowCast(xx, yy, xx, yy);
		}
		if (cam == null)
			return;

		int miny = Math.max(cam.getNorth() / getTileHeight(), 0);
		int maxy = Math.min(cam.getSouth() / getTileHeight() + 1, getHeight());
		int minx = Math.max(cam.getWest() / getTileWidth(), 0);
		int maxx = Math.min(cam.getEast() / getTileWidth() + 1, getWidth());

		for (int ty = miny; ty < maxy; ty ++)
		{
			int desty = ty * getTileHeight() - cam.getNorth();
			for (int tx = minx; tx < maxx; tx ++)
			{
				int destx = tx * getTileWidth() - cam.getWest();
				if (isInFOV(tx,ty))
					tileset.drawTile(g2d, getTile(tx, ty), destx, desty);
			}
		}
		objects.draw(gs, g2d);
	}
}
