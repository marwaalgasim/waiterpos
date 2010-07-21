package ua.cn.yet.waiter;

import ua.cn.yet.waiter.ui.AbstractForm;

/**
 * Listener to the form closing and other events
 * 
 * @author Yuriy Tkach
 */
public interface FormListener {

	/**
	 * Event when the form is about to close
	 * 
	 * @param form
	 *            form that is closing
	 */
	public void formClosing(AbstractForm form);

}
