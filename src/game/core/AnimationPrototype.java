package game.core;

import java.awt.image.BufferedImage;

public class AnimationPrototype {
	private BufferedImage[] sequence;
	private float defaultSpeed;
	
	public AnimationPrototype(BufferedImage[] sequence, float defaultSpeed)
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
