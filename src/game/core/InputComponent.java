package game.core;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class InputComponent {
	private class InputEntry
	{
		public InputEntry(String n)
		{
			name = n;
			lastPress = 0;
			lastRelease = 0;
		}
		public String name;
		@SuppressWarnings("unused")
		public long lastPress;
		@SuppressWarnings("unused")
		public long lastRelease;
	}
	
	private HashMap<Integer, InputEntry> controlMap;
	private String lastControl;
	boolean enabled;
	
	public InputComponent()
	{
		controlMap = new HashMap<Integer, InputEntry>();
		lastControl = null;
		enabled = true;
	}

	public void disable()
	{
		enabled = false;
	}
	public void enable()
	{
		enabled = true;
	}
	public void setControl(int keycode, String messageName)
	{
		controlMap.put(keycode, new InputEntry(messageName));
	}
	
	public boolean keyPressed(KeyEvent arg0) {
		if (!enabled)
			return false;
		InputEntry entry = controlMap.get(arg0.getKeyCode());
		if (entry != null)
		{
			lastControl = entry.name;
			entry.lastPress = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	public boolean keyReleased(KeyEvent arg0) {
		if (!enabled)
			return false;
		InputEntry entry = controlMap.get(arg0.getKeyCode());
		if (entry != null)
		{
			lastControl = entry.name;
			entry.lastRelease = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public String getLastInteraction()
	{
		return lastControl;
	}
	
	

}
