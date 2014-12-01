package game.map;


import game.core.Box;
import game.core.Camera;
import game.core.Direction;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * La clase Map contiene la informacion de los cuadrados del mapa
 * cargado de un archivo y su distribucion.
 * Tambien contiene los objetos y el campo de vision.
 * 
 * @author Francisco Altoe
 *
 */
public class Map
{
	private int width;
	private int height;
	
	private Tileset tileset;
	private ObjectManager objects;
	private boolean[] calculatedFOV;
	private Tile[] floorTiles;
	private Tile[] wallTiles;

	Map (int width, int height, Tileset tileset)
	{
		this.width = width;
		this.height = height;
		floorTiles = new Tile[width*height];
		wallTiles = new Tile[width*height];
		calculatedFOV = new boolean[width*height];
		for (int i = 0; i < width*height; i++)
		{
			floorTiles[i] = null;
			wallTiles[i] = null;
			calculatedFOV[i] = false;
		}
		objects = new ObjectManager();
		this.tileset = tileset;
	}

	public ObjectManager getObjects() { return objects; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getTileWidth() { return tileset.getTileWidth(); }
	public int getTileHeight() { return tileset.getTileHeight(); }
	public int xPixelsToTiles(int pixels) { return pixels / getTileWidth(); }
	public int yPixelsToTiles(int pixels) { return pixels / getTileHeight(); }
	public boolean inBounds (int x, int y) { return x>=0 && x<getWidth() && y>=0 && y<getHeight(); }
	
	public Tile getTile(int x, int y)
	{
		int idx = y * getWidth() + x;
		if (wallTiles[idx] != null)
			return wallTiles[idx];
		return floorTiles[idx];
	}
	public boolean isWall(int x, int y)
	{
		return wallTiles[y * getWidth() + x] != null;
	}
	public boolean isFloor(int x, int y)
	{
		return (wallTiles[y * getWidth() + x] == null && floorTiles[y*getWidth()+x] != null);
	}
	public void setWall (int x, int y, Tile t)
	{
		wallTiles[y * getWidth() + x] = t;
	}
	public void setFloor (int x, int y, Tile t)
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
	
	public void logic()
	{
		objects.logic(this);
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
	
	public void draw(Camera cam, Graphics2D g2d)
	{
		/*
		 * Para que los objetos dibujables no necesiten conocer la camara,
		 * aplicamos la transformacion de traslacion directamente en el g2d
		 * La ventaja es que se reducen las dependencias de las clases y
		 * se ahorran lineas de codigo.
		 */
		g2d.translate(-cam.getWest(), -cam.getNorth());
		
		/*
		 * Por cada recuadro recalculamos el campo de vision.
		 * Actualmente el unico objeto que provee vision del
		 * mapa es el jugador.
		 */
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

		int miny = Math.max(cam.getNorth() / getTileHeight(), 0);
		int maxy = Math.min(cam.getSouth() / getTileHeight() + 1, getHeight());
		int minx = Math.max(cam.getWest() / getTileWidth(), 0);
		int maxx = Math.min(cam.getEast() / getTileWidth() + 1, getWidth());

		for (int ty = miny; ty < maxy; ty ++)
		{
			int desty = ty * getTileHeight();
			for (int tx = minx; tx < maxx; tx ++)
			{
				int destx = tx * getTileWidth();
				if (isInFOV(tx,ty))
					tileset.drawTile(g2d, getTile(tx, ty), destx, desty);
			}
		}
		objects.draw(this, g2d);
	}
	
	public void startGame()
	{
		objects.startGame(this);
	}
	public void keyPressed(KeyEvent arg0) {
		objects.keyPressed(arg0);
	}
	public void keyReleased(KeyEvent arg0) {
		objects.keyReleased(arg0);
	}
}
