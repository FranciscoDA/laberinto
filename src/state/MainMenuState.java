package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.nio.file.Paths;

public class MainMenuState implements State {
	private GamePanel panel;
	private String[] text = new String[]{
			"Menu principal",
			"1. Jugar",
			"2. Acerca de",
			"3. Salir"
	};
	private int textIndex;
	private int textLine;
	public MainMenuState(GamePanel panel)
	{
		this.panel = panel;
		textIndex = 0;
		textLine = 0;
	}
	
	@Override
	public void init() {
	}

	@Override
	public void pause() {
		textIndex = 0;
		textLine = 0;
	}

	@Override
	public void resume() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_1)
		{
			panel.pushGameState(new MapMenuState(panel, Paths.get("map")));
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_3)
		{
			panel.popGameState();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void run() {
		if(textLine < text.length)
		{
			textIndex ++;
			if (textIndex >= text[textLine].length())
			{
				textLine ++;
				if (textLine >= text.length)
				{
					textLine = text.length;
					textIndex = 0;
				}
				else
				{
					textIndex = 0;
				}
			}
		}
	}

	@Override
	public int getPreferredFPS() {
		return 30;
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(new Color(0,0,0,255));
		g2d.fillRect(0, 0, panel.getScreenWidth(), panel.getScreenWidth());
		g2d.setColor(new Color(255,255,255,255));
		g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
		for (int line = 0; line < textLine; line++)
		{
			g2d.drawString(text[line], 100, 100+50*line);
		}
		if(textLine < text.length)
		{
			
			g2d.drawString (text[textLine].substring(0, textIndex), 100, 100+50*textLine);
		}
	}
}
