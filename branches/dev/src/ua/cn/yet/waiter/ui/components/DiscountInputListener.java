package ua.cn.yet.waiter.ui.components;

/**
 * Listener to the discount input panel
 * 
 * @author n0weak
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
