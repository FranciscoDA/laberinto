package state;

import game.core.Camera;
import game.map.Map;
import game.map.MapLoader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;


public class GameState implements State {
	private static double SCREEN_SCALE = 2.0;
	private GamePanel panel;
	private String mapPath;
	private Camera camera;
	private Map map;

	public GameState(GamePanel panel, String path)
	{
		this.mapPath = path;
		this.panel = panel;
	}
	
	@Override
	public void init() {
		camera = new Camera(
			0, 0,
			(int)(panel.getScreenWidth() / SCREEN_SCALE), (int)(panel.getScreenHeight() / SCREEN_SCALE)
		);
		try
		{
			map = MapLoader.loadMap(MapLoader.MapType.FLARE, mapPath);
			map.setCamera(camera);
			map.startGame();
		}
		catch (Exception e)
		{
			// si hubo un error al cargar el mapa, informamos que no se pudo encontrar
			JOptionPane.showMessageDialog(null, e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		
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
	public void paint(Graphics2D g2d) {
		g2d.setColor(new Color (0, 0, 0, 255));
		g2d.fillRect(0, 0, panel.getScreenWidth(), panel.getScreenHeight());
		g2d.scale(SCREEN_SCALE, SCREEN_SCALE);
		map.draw(camera, g2d);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		map.keyPressed(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		map.keyReleased(arg0);
	}

	@Override
	public int getPreferredFPS() {
		return 60;
	}

	@Override
	public void run() {
		map.logic();
		if (map.getState() == Map.State.EXIT)
		{
			panel.popState();
		}
	}
}
