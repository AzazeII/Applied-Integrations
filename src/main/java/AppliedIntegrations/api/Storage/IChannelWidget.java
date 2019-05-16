package AppliedIntegrations.api.Storage;

import appeng.api.storage.data.IAEStack;

/**
 * @param <T> Type of AE-stack, that this widget contains
 * @Author Azazell
 * @apiNote This interface represents filter for given AE stack
 */
public interface IChannelWidget<T extends IAEStack<T>> {
	/**
	 * @return Current filtered AE stack
	 */
	IAEStack<T> getAEStack();

	/**
	 * Updates current filtered AE stack
	 *
	 * @param t new stack
	 */
	void setAEStack(IAEStack<?> t);

	/**
	 * @return Message on mouse hover
	 */
	String getStackTip();

	/**
	 * Called in drawScreen to draw this widget
	 */
	void drawWidget();

	/**
	 * Check if mouse over widget
	 *
	 * @param x mouse X
	 * @param y mouse Y
	 * @return Is mouse at given x and y is over widget
	 */
	boolean isMouseOverWidget(int x, int y);
}
