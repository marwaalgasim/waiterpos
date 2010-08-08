package ua.cn.yet.waiter.ui.components;

/**
 * Listener to the input number panel
 * 
 * @author Yuriy Tkach
 */
public interface DiscountInputListener {

	/**
	 * Method that is called when discount is applied 
	 * 
	 * @param newValue
	 *            new value with discount applied
	 */
	public void discountApplied(double newValue);

}
