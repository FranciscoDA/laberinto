package game.map;

import java.util.Observable;

/**
 * Clase vacia para agrupar a todos los objetos del mapa
 * con el proposito de que no se agreguen objetos de clases
 * erroneas.
 * 
 * Todos los objetos del mapa deberian conocer al mapa,
 * adicionalmente tambien deberian permitir que otros
 * objetos los conozcan.
 * @author Francisco Altoe
 */
public abstract class MapObject extends Observable {
	protected Map map;
	public MapObject(Map map)
	{
		this.map = map;
	}
}
