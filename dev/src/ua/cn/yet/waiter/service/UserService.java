package ua.cn.yet.waiter.service;

import java.util.Collection;

import ua.cn.yet.waiter.model.User;

/**
 * User service to deal with users
 * 
 * @author Yuriy Tkach
 */
public interface UserService extends GenericService<User> {

	/**
	 * Authenticating user
	 * 
	 * @param login
	 *            Login to use
	 * @param password
	 *            Password to use
	 * @return Authenticated user or <code>null</code>
	 * @throws GeneralServiceException
	 *             If db errors occur
	 * @throws SuperLoginException
	 */
	public User authenticateUser(String login, String password)
			throws GeneralServiceException;

	/**
	 * Getting all non-admin users
	 * 
	 * @return collection of users
	 */
	public Collection<User> getAllNonAdmins();
}
