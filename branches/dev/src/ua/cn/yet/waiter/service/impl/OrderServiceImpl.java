package ua.cn.yet.waiter.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderReport;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.util.Utils;

public class OrderServiceImpl extends GenericServiceImpl<Order> implements
		OrderService {

	public OrderServiceImpl() {
		super(Order.class);
	}

	@Override
	protected void beforeEntityAddUpdate(Order entity)
			throws GeneralServiceException {
		if (entity.getWaiter() != null) {
			if (!entity.getWaiter().isActive()) {
				throw new GeneralServiceException(
						"Пользователь неактивный. Обратитесь к администратору.");
			}

			if (entity.getWaiter().isAdmin()) {
				throw new GeneralServiceException(
						"Пользователь-администратор не может создавать заказы.");
			}
		}
	}

	@Override
	protected void beforeEntityDelete(Order entity)
			throws GeneralServiceException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.OrderService#getAllUserOrders(ua.cn.yet.waiter
	 * .model.User, Boolean)
	 */
	@Override
	public Collection<Order> getAllUserOrders(User waiter, Boolean closed)
			throws IllegalArgumentException {
		if (null == waiter) {
			throw new IllegalArgumentException("Waiter cannot be null");
		}
		if (waiter.isAdmin()) {
			throw new IllegalArgumentException("Admin user cannot have orders");
		}
		try {
			if (null == closed) {
				return executeQuery(Order.QUERY_ALL_USER_ORDERS, true, false,
						waiter);
			} else if (closed.booleanValue() == true) {
				return executeQuery(Order.QUERY_CLOSED_USER_ORDERS, true,
						false, waiter);
			} else {
				return executeQuery(Order.QUERY_OPEN_USER_ORDERS, true, false,
						waiter);
			}
		} catch (Exception e) {
			log.error("Failed to get all orders for user " + waiter, e);
			return new ArrayList<Order>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.OrderService#getOpenUserOrders(ua.cn.yet.waiter
	 * .model.User)
	 */
	@Override
	public Collection<Order> getOpenUserOrders(User waiter)
			throws IllegalArgumentException, GeneralServiceException {
		return getAllUserOrders(waiter, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.OrderService#deleteUserFromUserOrders(ua.cn.
	 * yet.waiter.model.User)
	 */
	@Override
	public void deleteUserFromUserOrders(User user)
			throws IllegalArgumentException, GeneralServiceException {
		executeUpdateQuery(Order.QUERY_REMOVE_USER_FROM_USER_ORDERS, true, user);
	}

	/**
	 * Dynamically building query based on params. I wish JPA would have
	 * Criteria api, like Hibernate does. Well, let's wait for the J2EE-7, but
	 * for now - building query by hand.
	 * 
	 * @see ua.cn.yet.waiter.service.OrderService#getOrdersForRange(java.util.Calendar,
	 *      Calendar, ua.cn.yet.waiter.model.User, java.lang.Boolean)
	 */
	@Override
	public Collection<Order> getOrdersForRange(Calendar from, Calendar to,
			User waiter, Boolean closed, Boolean forDeletion) {
		if ((null == from) && (null == waiter) 
				&& (null == closed) && (null == forDeletion)) {
			return getAllEntites();
		}

		String query = Order.QUERY_ORDERS_NOTNAMED;
		Object[] params = new Object[0];
		int paramIndex = 1;

		if (from != null) {
			if (null == to) {
				Calendar[] dates = Utils.getFullDayRangeForDate(from);
				from = dates[0];
				to = dates[1];
			}

			Utils.adjustTimesForRangeRequest(from, to);

			query += String.format(Order.CONDITION_CREATION_RANGE,
					paramIndex++, paramIndex++);
			params = ArrayUtils.add(params, from);
			params = ArrayUtils.add(params, to);
		}

		if (waiter != null) {
			if (params.length > 0) {
				query += Order.CONDITION_AND;
			}
			query += String.format(Order.CONDITION_WAITER, paramIndex++);
			params = ArrayUtils.add(params, waiter);
		}

		if (closed != null) {
			if (params.length > 0) {
				query += Order.CONDITION_AND;
			}
			query += String.format(Order.CONDITION_CLOSED, closed.toString());
		}
		
		if (forDeletion != null) {
			if (params.length > 0) {
				query += Order.CONDITION_AND;
			}
			query += String.format(Order.CONDITION_FOR_DELETION, forDeletion.toString());
		}

		return executeQuery(query, false, false, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.OrderService#getOrderReport(java.util.Calendar,
	 * java.util.Calendar, ua.cn.yet.waiter.model.User, java.lang.Boolean)
	 */
	@Override
	public OrderReport getOrderReport(Calendar from, Calendar to, User waiter,
			Boolean closed, Boolean forDeletion) {
		if ((null != from) && (null == to)) {
			Calendar[] dates = Utils.getFullDayRangeForDate(from);
			from = dates[0];
			to = dates[1];
		}

		Collection<Order> orders = getOrdersForRange(from, to, waiter, closed, forDeletion);

		OrderReport report = new OrderReport(from, to);
		report.setWaiter(waiter);
		report.setOnlyClosed(closed);
		
		for (Order order : orders) {
			if(!order.isCanceled() || forDeletion){
				report.addOrder(order);
			}
		}

		return report;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.service.OrderService#getOccupiedTables()
	 */
	@Override
	public Set<Integer> getOccupiedTables() {
		Collection<Integer> allNumbers = executeQuery(
				Order.QUERY_ALL_OCCUPIED_TABLE_NUMBERS, true, false);
		Set<Integer> result = new HashSet<Integer>();
		result.addAll(allNumbers);
		result.remove(Order.TABLE_BAR);
		result.remove(Order.TABLE_NONE);
		return result;
	}
}
