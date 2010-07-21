package ua.cn.yet.waiter;

import ua.cn.yet.waiter.model.User;

/**
 * Listener to the login events
 * 
 * @author Yuriy Tkach
 */
public interface LoginListener {

	/**
	 * @param user User that successfully logged in
	 */
	public void userLoggedIn(User user);
	
}
