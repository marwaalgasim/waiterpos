package ua.cn.yet.waiter.service.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.service.UserService;

public class UserServiceImpl extends GenericServiceImpl<User> implements
		UserService {
	
	private OrderService orderService;

	public UserServiceImpl() {
		super(User.class);
	}

	@Override
	protected void beforeEntityAddUpdate(User entity)
			throws GeneralServiceException {
	}

	@Override
	protected void beforeEntityDelete(User entity)
			throws GeneralServiceException {
		orderService.deleteUserFromUserOrders(entity);
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.service.UserService#authenticateUser(java.lang.String, java.lang.String)
	 */
	@Override
	public User authenticateUser(String login, String password)
			throws GeneralServiceException {
		
		if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
			return null;
		}
		
		User user = executeQuery(User.QUERY_BY_LOGIN_AND_PASS, true, true, login, password);
		
		log.info("Authenticating " + login + ". Result: " + (user != null));
		
		return user;
	}

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService() {
		return orderService;
	}

	/**
	 * @param orderService the orderService to set
	 */
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.service.UserService#getAllNonAdmins()
	 */
	@Override
	public Collection<User> getAllNonAdmins() {
		return executeQuery(User.QUERY_ALL_NON_ADMINS, true, false);
	}
}
