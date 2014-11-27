package state;

import game.core.GamePanel;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


/**
 * La clase abstracta State provee una capa entre el panel del juego y
 * la logica del mismo.
 * Las clases que extienden a State representan pantallas o etapas del juego,
 * por lo que cada una tendra su comportamiento caracteristico a la hora
 * de ejecutar la logica del juego, dibujar en pantalla o responder a interacciones.
 * 
 * La idea detras de cada pantalla es que se las pueda administrar como una pila,
 * de manera que transicionen entre las siguientes etapas:
 * 
 * inicio (init) => pausa (pause) <-> resumido (resume) => fin (destroy)
 * 
 * Un State es iniciado por el GamePanel al agregarlo a la pila
 * Cuando se agrega otro State sobre el actual en la pila,
 * 	el viejo entra en pausa y se inicia el nuevo.
 * Cuando un State pausado vuelve al tope de la pila, resume su ejecucion.
 * Finalmente, cuando es desapilado, es destruido.
 * 
 * @author Francisco Altoe
 *
 */
public abstract class State {	
	public abstract void init(GamePanel gp);
	public abstract void pause(GamePanel gp);
	public abstract void resume(GamePanel gp);
	public abstract void destroy(GamePanel gp);
	
	public abstract void logic(GamePanel gp);
	public abstract void paint(GamePanel gp, Graphics2D g2d);


	public abstract void keyPressed(KeyEvent arg0);
	public abstract void keyReleased(KeyEvent arg0);
}
