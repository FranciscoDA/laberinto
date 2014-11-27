package game.map;

public class Tile {
	public Tile (int gid, int sX, int sY)
	{
		this.gid = gid;
		this.sheetX = sX;
		this.sheetY = sY;
	}

	public int getGid()
	{
		return gid;
	}
	public int getType()
	{
		return type;
	}
	public int getSheetX()
	{
		return sheetX;
	}
	public int getSheetY()
	{
		return sheetY;
	}

	private int gid;
	private int sheetX;
	private int sheetY;
	private int type;
}
