package game.core;

import java.awt.image.BufferedImage;

/**
 * La clase AnimationModel define la secuencia de imagenes
 * y la velocidad por defecto de una instancia de Animation.
 * La animacion se divide en dos clases (AnimationModel y Animation)
 * para evitar replicar datos: Si existen dos objetos que compartan
 * al menos una animacion, ambos deberian guardar la misma
 * secuencia de imagenes y la velocidad por defecto
 * (ademas de la informacion pertinente a cada objeto)
 * 
 * Flyweight Pattern:
 * http://en.wikipedia.org/wiki/Flyweight_pattern
 * @author Francisco Altoe
 *
 */
public class AnimationModel {
	private BufferedImage[] sequence;
	private float defaultSpeed;
	
	public AnimationModel(BufferedImage[] sequence, float defaultSpeed)
	{
		this.sequence = sequence;
		this.defaultSpeed = defaultSpeed;
	}
	
	public BufferedImage[] getSequence()
	{
		return sequence;
	}
	
	public float getDefaultSpeed()
	{
		return defaultSpeed;
	}

	public BufferedImage getSprite(int id)
	{
		return sequence[id];
	}
}
