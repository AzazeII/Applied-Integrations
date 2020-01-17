package AppliedIntegrations.Utils;
/**
 * @Author Azazell
 */
public class IterableHelpers {
	public static <T> boolean containsOnlyNulls(Iterable<T> list) {
		for (T t : list) {
			if (t != null) {
				return false;
			}
		}

		return true;
	}
}
