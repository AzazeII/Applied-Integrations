package AppliedIntegrations.Topology;


import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @Author Azazell
 */
public class SubnetHelper {

	/**
	 * Accesses outer grid of any node bridging two grids like quarts fiber
	 * @param cache to inspect
	 * @return IGrid of cache
	 */
	public static IGrid getOuterGridOrNull(IGridCache cache) throws IllegalArgumentException, IllegalAccessException, ClassCastException {
		Field[] fields = cache.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("myGrid")) {
				if (Modifier.isPrivate(field.getModifiers())) {
					field.setAccessible(true);
					return (IGrid) field.get(cache);
				}
			}
		}

		return null;
	}
}
