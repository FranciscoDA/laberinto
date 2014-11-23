package state.game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import state.game.map.Camera;
import state.game.map.Map;
import state.game.map.MapLoader;
import state.game.map.Player;
import core.GamePanel;
import core.Point;

public class GameState extends state.State {
	@Override
	public void init(GamePanel gp) {
		camera = new Camera(
				0,
				0,
				gp.getScreenWidth(),
				gp.getScreenHeight()
			);
		map = new Map();
		try
		{
			MapLoader.loadMap(map, MapLoader.MapType.FLARE, "map01.txt");
		}
		catch (Exception e)
		{
			/* si hubo un error al cargar el mapa, informamos al usuario del archivo
			 * que no se pudo encontrar y el directorio en el cual se lo esta buscando
			 */
			JOptionPane.showMessageDialog(null, e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		map.getObjects().startGame(this);
		if(map.getObjects().getPlayer() != null)
			camera.lock((Point)map.getObjects().getPlayer().getShape());
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
		/*if (arg0.getKeyCode() == KeyEvent.VK_UP)
			camera.setVerticalVelocity(-2.0f);
		else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT)
			camera.setHorizontalVelocity(2.0f);
		else if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
			camera.setVerticalVelocity(2.0f);
		else if (arg0.getKeyCode() == KeyEvent.VK_LEFT)
			camera.setHorizontalVelocity(-2.0f);*/
		Player player = getMap().getObjects().getPlayer();
		if (player != null)
			player.keyPressed(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		/*if (arg0.getKeyCode() == KeyEvent.VK_UP)
			camera.setVerticalVelocity(0.0f);
		else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT)
			camera.setHorizontalVelocity(0.0f);
		else if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
			camera.setVerticalVelocity(0.0f);
		else if (arg0.getKeyCode() == KeyEvent.VK_LEFT)
			camera.setHorizontalVelocity(0.0f);
		*/
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
