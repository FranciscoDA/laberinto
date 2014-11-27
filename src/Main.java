

import game.core.GamePanel;

import java.awt.event.WindowEvent;
import java.nio.file.Paths;

import javax.swing.JFrame;




public class Main {
	public static void main (String[] args)
	{
		JFrame frame = new JFrame ("Main menu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GamePanel gamepanel = new GamePanel();
		gamepanel.pushGameState(new state.GameState(Paths.get("map/map01.txt")));

		/*
		 * es importante agregar el gamestate al panel antes de agregar el panel al jframe
		 * de otro modo, el motor AWT de java empieza a llamar al metodo paint de manera
		 * asincrona, sin que se hayan cargado todos los recursos necesarios para responder
		 */

		frame.add(gamepanel);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		gamepanel.run();
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
