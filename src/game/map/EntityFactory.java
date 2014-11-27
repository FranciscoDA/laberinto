package game.map;

import java.util.HashMap;

/**
 * Implementacion de una clase factory para entidades del mapa utilizando un singleton.
 * De esta forma se pueden crear entidades de clases heredadas segun su nombre sin
 * utilizar un switch. Es decir, sin conocer las clases de antemano.
 * (ver http://oodesign.com)
 * 
 * @author Francisco Altoe
 *
 */
public class EntityFactory {
	private static EntityFactory instance = new EntityFactory();
	
	private HashMap<String, Entity> entityTypes;
	
	private EntityFactory()
	{
		entityTypes = new HashMap<String, Entity>();
	}
	
	public static EntityFactory getInstance()
	{
		return instance;
	}

	
	
	public void registerEntityType (String name, Entity entityClass)
	{
		entityTypes.put(name, entityClass);
	}
	
	public Entity createEntity(Map map, HashMap<String, String> keyval)
	{
		Entity ent = entityTypes.get(keyval.get("type"));
		if (ent == null)
			return null;
		return ent.createEntity(map, keyval);
	}
}
