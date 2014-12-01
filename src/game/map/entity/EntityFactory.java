package game.map.entity;

import game.map.Map;

import java.util.HashMap;

/**
 * Implementacion de una clase factory para entidades del mapa utilizando un singleton
 * y reflexion.
 * De esta forma se pueden crear entidades de clases heredadas segun su nombre sin
 * utilizar un switch por lo tanto, no hace falta conocer las clases que heredan
 * de Entity de antemano.
 * 
 * Factory Pattern:
 * http://www.tutorialspoint.com/design_pattern/factory_pattern.htm
 * 
 * @author Francisco Altoe
 */
public class EntityFactory {
	private static EntityFactory instance = new EntityFactory();
	
	private HashMap<String, Class<?>> entityTypes;
	
	private EntityFactory()
	{
		entityTypes = new HashMap<String, Class<?>>();
	}
	
	public static EntityFactory getInstance()
	{
		return instance;
	}

	public void registerEntityType (Class<?> entityClass) throws Exception
	{
		if (Entity.class.isAssignableFrom(entityClass))
			entityTypes.put(entityClass.getSimpleName(), entityClass);
		else
			throw new Exception("la clase registrada debe extender Entity");
	}
	
	public Entity createEntity(Map map, HashMap<String, String> keyval)
	{
		Class<?> ent = entityTypes.get(keyval.get("type"));

		try {
			return (Entity) ent.getConstructor(Map.class, HashMap.class).newInstance(map, keyval);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
