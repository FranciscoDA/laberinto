package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;

public class MapMenuState implements State {
	private GamePanel panel;
	private LinkedList<String> mapList;
	private Path parentFolder;
	private int selectionIndex;

	public MapMenuState(GamePanel panel, Path parent)
	{
		this.parentFolder = parent;
		this.panel = panel;
		this.mapList = new LinkedList<String>();
		this.selectionIndex = 0;
		for (File map : parent.toFile().listFiles())
		{
			if (map.getName().endsWith(".txt"))
				mapList.add(map.getName());
		}
	}

	@Override
	public void run() {
	}

	@Override
	public void init() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
			selectionIndex ++;
		else if (arg0.getKeyCode() == KeyEvent.VK_UP)
			selectionIndex --;
		else if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			panel.popGameState();
		else if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
			if (!mapList.isEmpty())
				panel.pushGameState(
					new GameState(panel,
						(parentFolder.resolve(mapList.get(selectionIndex).toString()))
					)
				);
		
		if (selectionIndex >= mapList.size())
			selectionIndex = mapList.size() - 1;
		if (selectionIndex < 0)
			selectionIndex = 0;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public int getPreferredFPS() {
		return 30;
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(new Color(0,0,0,255));
		g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		g2d.setColor(new Color(255,255,255,255));
		int idx = 0;
		for (String name : mapList)
		{
			g2d.setFont(new Font("SansSerif", 0, 15));
			if(idx == selectionIndex)
				g2d.fillOval(100 - 10, 50 + 50 * idx - 8, 5, 5);
			g2d.drawString(name, 100, 50 + 50 * idx);
			idx ++;
		}
	}

}
