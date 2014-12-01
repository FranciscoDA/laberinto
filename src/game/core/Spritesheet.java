package game.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * La clase spritesheet trabaja con conjuntos de sprites
 * compactados en un solo archivo.
 * Para definir los sprites y las animaciones se utiliza un archivo
 * de texto que es leido por esta clase.
 * El archivo que describe la planilla de sprites debe ser
 * escrito a mano siguiendo el formato:
 * 
 * [sprite]
 * id=idsprite
 * location=x,y,w,h
 * 
 * ...
 * 
 * [animation]
 * name=nombre
 * sequence=idsprite1,idsprite2,idsprite3,...
 * speed=velocidadpordefecto
 * 
 * @author Francisco Altoe
 *
 */
public class Spritesheet {
	private BufferedImage sheet;
	HashMap<Integer, BufferedImage> sprites;
	HashMap<String, BufferedImage> namedSprites;
	HashMap<String, AnimationModel> animations;
	public Spritesheet ()
	{
		sheet = null;
		sprites = new HashMap<Integer, BufferedImage>();
		animations = new HashMap<String, AnimationModel>();
		namedSprites = new HashMap<String, BufferedImage>();
	}
	public void load (String fileName)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			for (String line = br.readLine(); line != null; line = br.readLine())
			{
				if(line.equals("[spritesheet]"))
				{
					HashMap <String, String> attributes = new HashMap<String, String>();
					for(String subline = br.readLine(); subline != null && !subline.isEmpty(); subline = br.readLine())
					{
						String[] split = subline.split("="); 
						attributes.put(split[0], split[1]);
					}
					sheet = ImageIO.read(new File(attributes.get("source")));
				}
				if(line.equals("[sprite]"))
				{
					HashMap <String, String> attributes = new HashMap<String, String>();
					for(String subline = br.readLine(); subline != null && !subline.isEmpty(); subline = br.readLine())
					{
						String[] split = subline.split("="); 
						attributes.put(split[0], split[1]);
					}
					String[] location = attributes.get("location").split(",");
					BufferedImage subimg = sheet.getSubimage(
							Integer.parseInt(location[0]),
							Integer.parseInt(location[1]),
							Integer.parseInt(location[2]),
							Integer.parseInt(location[3])
					);
					sprites.put(Integer.parseInt(attributes.get("id")), subimg);
					String name = attributes.get("name");
					if (name != null)
					{
						namedSprites.put(name, subimg);
					}
				}
				else if (line.equals("[animation]"))
				{
					HashMap <String, String> attributes = new HashMap<String, String>();
					for(String subline = br.readLine(); subline != null && !subline.isEmpty(); subline = br.readLine())
					{
						String[] split = subline.split("="); 
						attributes.put(split[0], split[1]);
					}
					String[] sequenceString = attributes.get("sequence").split(",");
					BufferedImage[] sequence = new BufferedImage[sequenceString.length];
					for (int i = 0; i < sequenceString.length; i++)
					{
						sequence[i] = sprites.get(Integer.parseInt(sequenceString[i]));
					}
					float speed = 1.0f;
					if (attributes.get("speed") != null)
						speed = Float.parseFloat(attributes.get("speed"));
					animations.put(attributes.get("name"), new AnimationModel (sequence,speed));
				}
			}
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public Animation createAnimation(String name)
	{
		return new Animation(animations.get(name));
	}
	public BufferedImage getSprite(String name)
	{
		return namedSprites.get(name);
	}
}
