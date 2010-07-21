package ua.cn.yet.waiter.ui.components;

/**
 * Listener to the input number panel
 * 
 * @author Yuriy Tkach
 */
public interface NumericInputListener {

	/**
	 * Method that is called when number changes
	 * 
	 * @param newNumber
	 *            new number that was input
	 */
	public void numberChanged(double newNumber);

}
