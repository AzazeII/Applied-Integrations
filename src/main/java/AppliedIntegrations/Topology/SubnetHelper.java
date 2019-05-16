package AppliedIntegrations.Topology;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SubnetHelper {

	/**
	 * @param cache to inspect
	 * @return IGrid of cache
	 */
	public static IGrid getOuterGridOrNull(IGridCache cache) throws IllegalArgumentException, IllegalAccessException, ClassCastException {
		// Get all fields of class
		Field[] fields = cache.getClass().getDeclaredFields();

		// Iterate for each field
		for (Field field : fields) {
			// Check if name is equal to "myGrid"
			if (field.getName().equals("myGrid")) {
				// Check if field is private
				if (Modifier.isPrivate(field.getModifiers())) {
					// Make field accesible
					field.setAccessible(true);

					// Return field value
					return (IGrid) field.get(cache);
				}
			}
		}

		// Dead end
		return null;
	}
}
