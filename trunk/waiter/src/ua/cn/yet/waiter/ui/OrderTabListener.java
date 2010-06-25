package ua.cn.yet.waiter.ui;

/**
 * Listener to different event that occur in the order tab
 * 
 * @author Yuriy Tkach
 */
public interface OrderTabListener {

	/**
	 * Order is canceled and confirmed by user
	 * 
	 * @param orderTab
	 *            Order tab that triggered event
	 */
	public void orderCanceled(OrderTab orderTab);

	/**
	 * Order is closed and confirmed by user
	 * 
	 * @param orderTab
	 *            Order tab that triggered event
	 */
	public void orderClosed(OrderTab orderTab);

	/**
	 * Items of the category were displayed in the tab
	 * 
	 * @param orderTab
	 *            Order tab that triggered event
	 */
	public void categoryDisplayed(OrderTab orderTab);

	/**
	 * Categories list was displayed in the tab
	 * 
	 * @param orderTab
	 *            Order tab that triggered event
	 */
	public void categoriesListDisplayed(OrderTab orderTab);

}
