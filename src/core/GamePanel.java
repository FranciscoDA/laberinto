package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

import javax.swing.JPanel;

import state.State;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements KeyListener {
	private boolean exit;
	public GamePanel ()
	{
		super();
		setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		stateStack = new Stack<State>();
		setDoubleBuffered(true);
		exit = false;
	}
	public void popGameState ()
	{
		if (!stateStack.isEmpty())
		{
			stateStack.peek().destroy(this);
			stateStack.pop();
			if (!stateStack.isEmpty())
			{
				stateStack.peek().resume(this);
			}
		}
	}
	public void pushGameState (State gs)
	{
		if(!stateStack.isEmpty())
		{
			stateStack.peek().pause(this);
		}
		stateStack.push(gs);
		gs.init(this);
	}
	
	public void run() {
		while (!exit)
		{
			long start = System.nanoTime();
			if (!stateStack.isEmpty())
			{
				stateStack.peek().logic(this);
			}
			repaint();
			long elapsed = System.nanoTime() - start;
			long wait = targetTime - elapsed/100000; // => tiempo estimado por frame - tiempo utilizado = tiempo de espera
			try
			{
				if (wait > 0)
				{
					Thread.sleep(wait);
				}
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
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(SCREEN_SCALE, SCREEN_SCALE);
		g2d.setColor(new Color (0, 0, 0, 255));
		g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		if (!stateStack.isEmpty())
		{
			stateStack.peek().paint(this, (Graphics2D) g);
		}
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
			exit = true;
		else if (!stateStack.isEmpty())
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
		return (int) (SCREEN_WIDTH / SCREEN_SCALE);
	}
	public int getScreenHeight()
	{
		return (int) (SCREEN_HEIGHT / SCREEN_SCALE);
	}

	private static int SCREEN_WIDTH = 640;
	private static int SCREEN_HEIGHT = 480;
	private static double SCREEN_SCALE = 2.0;
	
	private static long fps = 60;
	private static long targetTime = 1000 / fps; // 1000ms / 60fps => tiempo estimado por frame
	
	Stack<State> stateStack;
}
