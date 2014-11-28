package state;

import game.core.GamePanel;
import game.map.Camera;
import game.map.Map;
import game.map.MapLoader;
import game.map.Player;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

import javax.swing.JOptionPane;


public class GameState extends state.State {
	Path mapPath;
	public GameState(Path path)
	{
		this.mapPath = path;
	}
	
	@Override
	public void init(GamePanel gp) {
		camera = new Camera(
				0,
				0,
				gp.getScreenWidth(),
				gp.getScreenHeight()
			);
		try
		{
			map = MapLoader.loadMap(map, MapLoader.MapType.FLARE, mapPath);
		}
		catch (Exception e)
		{
			// si hubo un error al cargar el mapa, informamos que no se pudo encontrar
			JOptionPane.showMessageDialog(null, e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		map.getObjects().startGame(this);
		if(map.getObjects().getPlayer() != null)
			camera.lock(map.getObjects().getPlayer().getShape());
	}

	@Override
	public void pause(GamePanel gp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume(GamePanel gp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy(GamePanel gp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logic(GamePanel gp) {
		camera.move(this);
		map.getObjects().logic(this);
	}

	@Override
	public void paint(GamePanel gp, Graphics2D g2d) {
		map.draw(this, g2d);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		Player player = getMap().getObjects().getPlayer();
		if (player != null)
			player.keyPressed(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		Player player = getMap().getObjects().getPlayer();
		if (player != null)
			player.keyReleased(arg0);
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	public Map getMap()
	{
		return map;
	}

	private Camera camera;
	private Map map;
}
