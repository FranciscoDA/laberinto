package state.game.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public abstract class MapLoader {
	static {
		EntityFactory.getInstance().registerEntityType("playerSpawn", new EntityPlayerSpawn());
		EntityFactory.getInstance().registerEntityType("abyss", new EntityAbyss());
	}
	public enum MapType
	{
		FLARE/*, TMX*/
	}
	public static void loadMap (Map map, MapType type, String fileName) throws Exception
	{
		if (type == MapType.FLARE)
		{
			loadFlareMap (map, fileName);
		}
	}
	
	/**
	 * Carga un mapa en formato flare map exportado por el editor tiled
	 * (http://mapeditor.org/)
	 * 
	 * @param fileName Nombre del archivo a cargar
	 * @throws Exception 
	 */
	private static void loadFlareMap (Map map, String fileName) throws Exception
	{
		//iremos leyendo el archivo de a tags encerrados entre corchetes
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		for (String line = br.readLine(); line != null; line = br.readLine())
		{
			// del tag header solo nos interesa el alto y ancho del mapa
			if (line.equals("[header]"))
			{
				int w = 0;
				int h = 0;
				for (String attribute = br.readLine(); !attribute.isEmpty(); attribute = br.readLine())
				{
					String[] keyval = attribute.split("=");
					if (keyval[0].equals("width"))
						w = Integer.parseInt(keyval[1]);
					else if (keyval[0].equals("height"))
						h = Integer.parseInt(keyval[1]);
				}
				map.setDimensions(w, h);
			}
			// del tag tilesets solo nos interesa un solo tileset
			else if (line.equals("[tilesets]"))
			{
				String path = "";
				int tw = 0;
				int th = 0;
				for (String attribute = br.readLine(); !attribute.isEmpty(); attribute = br.readLine())
				{
					String[] opts = attribute.split(",");
					path = opts[0].split("=")[1];
					tw = Integer.parseInt(opts[1]);
					th = Integer.parseInt(opts[2]);
				}
				Tileset ts = new Tileset ();
				ts.load(path, tw, th);
				map.setTileSet(ts);
			}
			// del tag layer diferenciamos la capa del piso y la de las paredes
			else if (line.equals("[layer]"))
			{
				String type = "";
				for (String attribute = br.readLine(); !attribute.equals("data="); attribute = br.readLine())
				{
					String[] keyval = attribute.split("=");
					if (keyval[0].equals("type"))
						type = keyval[1];
				}
				for (int y = 0; y < map.getHeight(); y++)
				{
					String[] tiledata = br.readLine().split(",");
					for (int x = 0; x < map.getWidth(); x++)
					{ 
						if (type.equals("wall"))
							map.setWall(x, y, Byte.parseByte(tiledata[x]));
						else if (type.equals("floor"))
							map.setFloor(x, y, Byte.parseByte(tiledata[x]));
					}
				}
			}
			//todos los demas tags son objetos del mapa
			else if (line.matches("^\\[\\w+\\]$"))
			{
				HashMap<String, String> keyval = new HashMap<String, String>();
				for (String attribute = br.readLine(); !attribute.isEmpty(); attribute = br.readLine())
				{
					String[] split = attribute.split("=");
					keyval.put(split[0], split[1]);
				}
				map.getObjects().add(EntityFactory.getInstance().createEntity(map, keyval));
			}
		}
		br.close();
	}
}
