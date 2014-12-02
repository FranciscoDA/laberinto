package game.map;

import game.map.entity.Abyss;
import game.map.entity.EntityFactory;
import game.map.entity.PlayerExit;
import game.map.entity.PlayerSpawn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Clase para cargar mapas en distintos formatos.
 * Por el momento solo soporta un unico formato que es el
 * formato flare map del editor Tiled (http://www.mapeditor.org)
 * 
 * @author Francisco Altoe
 *
 */
public abstract class MapLoader {
	static {
		try {
			EntityFactory.getInstance().registerEntityType(PlayerSpawn.class);
			EntityFactory.getInstance().registerEntityType(Abyss.class);
			EntityFactory.getInstance().registerEntityType(PlayerExit.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public enum MapType
	{
		FLARE/*, TMX*/
	}
	public static Map loadMap (MapType type, String mapPath) throws Exception
	{
		if (type == MapType.FLARE)
		{
			return loadFlareMap (mapPath);
		}
		return null;
	}
	
	/**
	 * Carga un mapa en formato flare map exportado por el editor tiled
	 * (http://mapeditor.org/)
	 * 
	 * @param mapPath Nombre del archivo a cargar
	 * @throws Exception 
	 */
	private static Map loadFlareMap (String mapPath) throws Exception
	{
		String parent = mapPath.substring(0, mapPath.lastIndexOf('/') + 1);
		Map map = null;
		Tileset tileset = null;
		int width = 0;
		int height = 0;
		//iremos leyendo el archivo de a tags encerrados entre corchetes
		BufferedReader br = new BufferedReader(new FileReader(mapPath));
		for (String line = br.readLine(); line != null; line = br.readLine())
		{
			// del tag header solo nos interesa el alto y ancho del mapa
			if (line.equals("[header]"))
			{
				for (String attribute = br.readLine(); !attribute.isEmpty(); attribute = br.readLine())
				{
					String[] keyval = attribute.split("=");
					if (keyval[0].equals("width"))
						width = Integer.parseInt(keyval[1]);
					else if (keyval[0].equals("height"))
						height = Integer.parseInt(keyval[1]);
				}
				if (tileset != null)
					map = new Map (width, height, tileset);
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
					path = parent + (Paths.get(opts[0].split("=")[1]));
					tw = Integer.parseInt(opts[1]);
					th = Integer.parseInt(opts[2]);
				}
				System.out.println(path);
				tileset = new Tileset (path, tw, th);
				if (map == null)
					map = new Map (width, height, tileset);
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
				for (int y = 0; y < height; y++)
				{
					String[] tiledata = br.readLine().split(",");
					for (int x = 0; x < width; x++)
					{ 
						Tile t = tileset.getTile(Integer.parseInt(tiledata[x]));
						if (type.equals("wall"))
							map.setWall(x, y, t);
						else if (type.equals("floor"))
							map.setFloor(x, y, t);
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
				map.addObject(EntityFactory.getInstance().createEntity(map, keyval));
			}
		}
		br.close();

		return map;
	}
}
