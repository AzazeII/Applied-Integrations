package AppliedIntegrations.Utils;
/**
 * @Author Azazell
 */
public class IterableHelpers {
	public static <T> boolean containsOnlyNulls(Iterable<T> list) {
		// Iterate for each entry in list
		for (T t : list) {
			// Check not null
			if (t != null) {
				return false;
			}
		}

		return true;
	}
}
