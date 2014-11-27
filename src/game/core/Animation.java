package game.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Observable;

public class Animation extends Observable {
	public static final int STATUS_STOPPED = 0;
	public static final int STATUS_LOOPING = 1;
	public Animation (AnimationPrototype prototype)
	{
		this.prototype = prototype;
		this.frame = 0.0f;
		this.speed = prototype.getDefaultSpeed();
		this.status = STATUS_STOPPED;
	}
	public void start ()
	{
		this.frame = 0.0f;
		this.status = STATUS_LOOPING;
	}
	public void advance ()
	{
		if (status == STATUS_LOOPING)
		{
			frame = (frame + speed);
			while (frame > prototype.getSequence().length)
			{
				frame = frame - prototype.getSequence().length;
				setChanged();
				notifyObservers(this);
			}
		}
	}
	public void stop ()
	{
		status = STATUS_STOPPED;
	}
	public void draw(Graphics2D g2d, int destx, int desty)
	{
		BufferedImage sprite = prototype.getSprite((int) frame);
		g2d.drawImage(sprite, destx, desty, null);
	}

	private float frame;
	private float speed;
	private AnimationPrototype prototype;
	private int status;
}
