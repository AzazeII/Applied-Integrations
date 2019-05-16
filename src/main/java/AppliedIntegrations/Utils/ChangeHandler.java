package AppliedIntegrations.Utils;

import java.util.function.Consumer;

/**
 * @Author Call onChange at any moment, to check if declared variable @param <T> was changed
 */
public class ChangeHandler<T> {
	private T lastT;

	public void onChange(T t, Consumer<T> action) {
		if (t != lastT) {
			action.accept(t);

			lastT = t;
		}
	}
}
