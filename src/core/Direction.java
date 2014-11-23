package core;

public enum Direction {
	// las direcciones compuestas (noreste, sudeste, etc)
	// no se usan en el juego

	NORTH(0,-1),
	NORTHEAST (Math.sqrt(2.0)/2, -Math.sqrt(2.0)/2),
	EAST (1, 0),
	SOUTHEAST (Math.sqrt(2.0)/2, Math.sqrt(2.0)/2),
	SOUTH (0, 1),
	SOUTHWEST (-Math.sqrt(2.0)/2, Math.sqrt(2.0)/2),
	WEST (-1, 0),
	NORTHWEST (-Math.sqrt(2.0)/2, -Math.sqrt(2.0)/2),;
	
	private final double xfactor;
	private final double yfactor;

	public double xFactor () { return xfactor; }
	public double yFactor () { return yfactor; }
	public boolean isOrthogonal ()
	{
		return (xfactor == 0) || (yfactor == 0);
	}
	Direction(double xfactor, double yfactor)
	{
		this.xfactor = xfactor;
		this.yfactor = yfactor;
	}
}
