package game.map;


import game.core.Camera;
import game.core.Direction;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * La clase Map contiene la informacion de los cuadrados del mapa
 * cargado de un archivo y su distribucion.
 * Tambien contiene los objetos y calcula el campo de vision.
 * 
 * @author Francisco Altoe
 *
 */
public class Map implements Observer
{
	public enum State {
		NONE,
		PLAYING,
		GAME_OVER,
		GAME_WIN,
		EXIT
	}
	private int width;
	private int height;
	
	private Tileset tileset;
	private ObjectManager objects;
	private boolean[] calculatedFOV;
	private Tile[] floorTiles;
	private Tile[] wallTiles;
	private LinkedList<Collisionable> illuminators;
	private Camera cam;
	private State state;

	Map (int width, int height, Tileset tileset)
	{
		this.width = width;
		this.height = height;
		this.floorTiles = new Tile[width*height];
		this.wallTiles = new Tile[width*height];
		this.calculatedFOV = new boolean[width*height];
		for (int i = 0; i < width*height; i++)
		{
			this.floorTiles[i] = null;
			this.wallTiles[i] = null;
			this.calculatedFOV[i] = false;
		}
		this.objects = new ObjectManager(this);
		this.tileset = tileset;
		this.illuminators = new LinkedList<Collisionable>();
		this.state = State.NONE;
	}

	public void addObject (MapObject o) { objects.add(o); }
	public int getTileWidth() { return tileset.getTileWidth(); }
	public int getTileHeight() { return tileset.getTileHeight(); }
	public boolean inBounds (int x, int y) { return x>=0 && x<width && y>=0 && y<height; }
	
	public Tile getTile(int x, int y)
	{
		int idx = y * width + x;
		if (wallTiles[idx] != null)
			return wallTiles[idx];
		return floorTiles[idx];
	}
	public boolean isWall(int x, int y)
	{
		return wallTiles[y * width + x] != null;
	}
	public boolean isFloor(int x, int y)
	{
		return (wallTiles[y * width + x] == null && floorTiles[y*width+x] != null);
	}
	public void setWall (int x, int y, Tile t)
	{
		wallTiles[y * width + x] = t;
	}
	public void setFloor (int x, int y, Tile t)
	{
		floorTiles[y * width + x] = t;
	}
	public boolean isInFOV(int x, int y)
	{
		return calculatedFOV[y * width + x];
	}
	public void addToFOV(int x, int y)
	{
		calculatedFOV[y * width + x] = true;
	}
	public void setCamera (Camera cam)
	{
		this.cam = cam;
	}
	
	public void logic()
	{
		objects.logic();
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
			int x = ((int) (startx + t * dx/distance)) / getTileWidth();
			int y = ((int) (starty + t * dy/distance)) / getTileHeight();
			if (x != startx / getTileWidth() || y != starty / getTileHeight())
				if (x != destx / getTileWidth() || y != desty / getTileHeight())
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
		 * Guardamos una copia del Graphics2D antes
		 * de aplicar la transformacion
		 */

		Graphics2D pretransform = (Graphics2D)g2d.create();
		/*
		 * Para que los objetos dibujables no necesiten conocer la camara,
		 * aplicamos la transformacion directamente en el g2d
		 * La ventaja es que se reducen las dependencias de las clases (y
		 * se ahorran lineas de codigo)
		 */
		g2d = cam.transform(g2d);
		
		/*
		 * Por cada recuadro recalculamos el campo de vision.
		 * Actualmente el unico objeto que provee vision del
		 * mapa es el jugador, pero hay soporte para otros
		 * objetos que posiblemente hagan lo mismo.
		 */
		if (illuminators.size() > 0)
		{
			for (int i = 0; i < calculatedFOV.length; i++)
				calculatedFOV[i] = false;

			for (Collisionable c : illuminators)
			{
				int xx = c.getShape().getCenterX()  / getTileWidth();
				int yy = c.getShape().getCenterY() / getTileHeight();
				shadowCast(xx, yy, xx, yy);
			}
		}

		int miny = Math.max(cam.getNorth() / getTileHeight(), 0);
		int maxy = Math.min(cam.getSouth() / getTileHeight() + 1, height);
		int minx = Math.max(cam.getWest() / getTileWidth(), 0);
		int maxx = Math.min(cam.getEast() / getTileWidth() + 1, width);

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
		objects.draw(g2d);
		
		if (state == State.GAME_OVER)
		{
			pretransform.setColor(new Color(0,0,0));
			pretransform.fillRect(0,0, cam.getEast()-cam.getWest(), 30);
		
			pretransform.setColor(new Color(255,255,255,255));
			pretransform.setFont(new Font("SansSerif", 0, 15));
			
			String msg = "¡Perdiste!";
			Rectangle2D rect = pretransform.getFontMetrics().getStringBounds(msg, pretransform);
			pretransform.drawString(
				msg, (cam.getWidth() - (int)(rect.getMaxX() - rect.getMinX())) / 2, 15
			);
			
			pretransform.setFont(new Font("SansSerif", 0, 10));
			msg = "Enter para volver...";
			rect = pretransform.getFontMetrics().getStringBounds(msg, pretransform);
			pretransform.drawString(
				msg, (cam.getWidth() - (int)(rect.getMaxX() - rect.getMinX())) / 2, 25
			);
		}
		else if (state == State.GAME_WIN)
		{
			pretransform.setColor(new Color(0,0,0));
			pretransform.fillRect(0,0, cam.getEast()-cam.getWest(), 30);
		
			pretransform.setColor(new Color(255,255,255,255));
			pretransform.setFont(new Font("SansSerif", 0, 15));
			
			String msg = "¡Ganaste!";
			Rectangle2D rect = pretransform.getFontMetrics().getStringBounds(msg, pretransform);
			pretransform.drawString(
				msg, (cam.getWidth() - (int)(rect.getMaxX() - rect.getMinX())) / 2, 15
			);
			
			pretransform.setFont(new Font("SansSerif", 0, 10));
			msg = "Enter para volver...";
			rect = pretransform.getFontMetrics().getStringBounds(msg, pretransform);
			pretransform.drawString(
				msg,(cam.getWidth() - (int)(rect.getMaxX() - rect.getMinX())) / 2, 25
			);
		}
	}
	
	public State getState()
	{
		return state;
	}
	
	public void startGame()
	{
		objects.startGame(this);
		state = State.PLAYING;
	}
	public void keyPressed(KeyEvent arg0) {
		objects.keyPressed(arg0);
		if (state == State.GAME_OVER || state == State.GAME_WIN)
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
				state = State.EXIT;
	}
	public void keyReleased(KeyEvent arg0) {
		objects.keyReleased(arg0);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Player)
		{
			Player pl = (Player) arg0;
			if (arg1.equals("created"))
			{
				cam.lock(pl.getShape());
				illuminators.add(pl);
			}
			else if (arg1.equals("died"))
			{
				state = State.GAME_OVER;
			}
			else if (arg1.equals("won"))
			{
				state = State.GAME_WIN;
			}
		}
	}
}
