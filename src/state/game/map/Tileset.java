package state.game.map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tileset {
	public Tileset ()
	{
		tileSheet = null;
		set = null;
	}
	public void load (String path, int tileWidth, int tileHeight) throws IOException
	{
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileSheet = ImageIO.read(new File(path));

		int tileCount = tileSheet.getWidth() / tileWidth * tileSheet.getHeight() / tileHeight;
		set = new Tile[tileCount+1];
		set[0] = null;
		for (int i=1; i <= tileCount; i++)
		{
			set[i] = new Tile( i,
				(i-1) * tileWidth % tileSheet.getWidth(),
				(i-1) * tileWidth / tileSheet.getWidth() * tileHeight
			);
		}
	}

	public int getTileWidth()
	{
		return tileWidth;
	}
	public int getTileHeight()
	{
		return tileHeight;
	}
	public Tile getTile(int i)
	{
		return set[i];
	}
	public void drawTile (Graphics2D g2d, Tile t, int dx, int dy)
	{
		if (t != null)
		{
			g2d.drawImage(tileSheet,
				dx, dy,
				dx + getTileWidth(), dy + getTileHeight(),
				set[t.getGid()].getSheetX(), set[t.getGid()].getSheetY(),
				set[t.getGid()].getSheetX() + getTileWidth(), set[t.getGid()].getSheetY() + getTileHeight(),
				null
			);
		}
	}

	private int tileWidth;
	private int tileHeight;
	
	private BufferedImage tileSheet;
	private Tile[] set;
}
