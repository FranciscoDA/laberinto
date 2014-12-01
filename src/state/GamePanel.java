package state;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class GamePanel extends JPanel implements KeyListener, Runnable {
	private static int SCREEN_WIDTH = 640;
	private static int SCREEN_HEIGHT = 480;
	private JFrame frame;
	private Stack<State> stateStack;

	public GamePanel (JFrame frame)
	{
		super();
		this.frame = frame;
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setFocusable(true);
		this.requestFocus();
		addKeyListener(this);
		stateStack = new Stack<State>();
		setDoubleBuffered(true);
	}
	public void popGameState ()
	{
		if (!stateStack.isEmpty())
		{
			stateStack.peek().destroy();
			stateStack.pop();
			if (!stateStack.isEmpty())
			{
				stateStack.peek().resume();
			}
			else
			{
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		}
	}
	public void pushGameState (State gs)
	{	
		if(!stateStack.isEmpty())
		{
			stateStack.peek().pause();
		}
		gs.init();
		stateStack.push(gs);
	}

	public void run() {
		while (!stateStack.isEmpty())
		{
			State state = stateStack.peek();
			long start = System.nanoTime();
			state.run();
			repaint();
			long delta = System.nanoTime() - start;

			/* tiempo maximo por frame - tiempo utilizado = tiempo de espera */
			long wait = 1000 / state.getPreferredFPS() - delta/100000;
			try
			{
				if (wait > 0)
					Thread.sleep(wait);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	@Override
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		if (!stateStack.isEmpty())
		{
			Graphics2D g2d = (Graphics2D) g;
			stateStack.peek().paint(g2d);
			Toolkit.getDefaultToolkit().sync();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (!stateStack.isEmpty())
		{
			stateStack.peek().keyPressed(arg0);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (!stateStack.isEmpty())
		{
			stateStack.peek().keyReleased(arg0);
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) { }

	public int getScreenWidth()
	{
		return SCREEN_WIDTH;
	}
	public int getScreenHeight()
	{
		return SCREEN_HEIGHT;
	}
	
	public static void main (String[] args)
	{
		JFrame frame = new JFrame ("Main menu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GamePanel gamepanel = new GamePanel(frame);
		frame.add(gamepanel);
		frame.setResizable(false);
		frame.setVisible(true);
		state.MainMenuState menu = new state.MainMenuState(gamepanel);
		gamepanel.pushGameState(menu);
		gamepanel.run();
	}
}
