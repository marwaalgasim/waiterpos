package ua.cn.yet.waiter.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderReport;
import ua.cn.yet.waiter.model.User;

/**
 * Service for managing orders
 * 
 * @author Yuriy Tkach
 */
public interface OrderService extends GenericService<Order> {

	/**
	 * Getting user's open orders
	 * 
	 * @param waiter
	 *            User to get orders for
	 * @return Collection of orders. Never <code>null</code>
	 * @throws IllegalArgumentException
	 *             If params are incorrect
	 * @throws GeneralServiceException
	 *             If db error occurs
	 */
	public Collection<Order> getOpenUserOrders(User waiter)
			throws IllegalArgumentException, GeneralServiceException;

	/**
	 * Getting all user's orders. If the <code>closed</code> argument is
	 * <code>null</code>, then it will not be taken into account.
	 * 
	 * @param waiter
	 *            User to get orders for
	 * @param closed
	 *            Specify to filter only closed or open orders. If
	 *            <code>null</code> then all orders will count
	 * @return Collection of orders. Never <code>null</code>
	 * @throws IllegalArgumentException
	 *             If params are incorrect
	 */
	public Collection<Order> getAllUserOrders(User waiter, Boolean closed)
			throws IllegalArgumentException;

	/**
	 * Removing user from order objects of the passed user
	 * 
	 * @param user
	 *            User that is being deleted
	 * @throws IllegalArgumentException
	 *             If params are incorrect
	 * @throws GeneralServiceException
	 *             If db error occurs
	 */
	public void deleteUserFromUserOrders(User user)
			throws IllegalArgumentException, GeneralServiceException;

	/**
	 * Getting orders that were created during the specified date range. If
	 * <code>from</code> date is <code>null</code> then orders will be included.
	 * If <code>to</code> date is <code>null</code> then the whole day is taken
	 * into account. Also <code>waiter</code> and <code>closed</code> arguments
	 * can be <code>null</code>, then they will not be taken into account.
	 * 
	 * @param from
	 *            Date to get report for. If <code>null</code> then all orders
	 *            will be included
	 * @param to
	 *            End date for the range. If <code>null</code> then full day
	 *            starting from <code>from</code> date will count
	 * @param waiter
	 *            Waiter to filter by. If <code>null</code> then all waiter will
	 *            count
	 * @param closed
	 *            Specify to filter only closed or open orders. If
	 *            <code>null</code> then all orders will count
	 * @return collection of orders
	 */
	public Collection<Order> getOrdersForRange(Calendar from, Calendar to,
			User waiter, Boolean closed, Boolean forDeletion);

	/**
	 * Getting report for the specified date range. If <code>from</code> date is
	 * <code>null</code> then all orders will be included. If <code>to</code>
	 * date is <code>null</code> then the whole day is taken into account. Also
	 * <code>waiter</code> and <code>closed</code> arguments can be
	 * <code>null</code>, then they will not be taken into account.
	 * 
	 * @param from
	 *            Date to get report for. If <code>null</code> then all orders
	 *            will be included.
	 * @param to
	 *            End date for the range. If <code>null</code> then full day
	 *            starting from <code>from</code> date will count
	 * @param waiter
	 *            Waiter to filter by. If <code>null</code> then all waiter will
	 *            count
	 * @param closed
	 *            Specify to filter only closed or open orders. If
	 *            <code>null</code> then all orders will count
	 * @return Report object
	 */
	public OrderReport getOrderReport(Calendar from, Calendar to, User waiter,
			Boolean closed, Boolean forDeletion);

	/**
	 * Getting all table numbers that are occupied now, not including bar and
	 * no_table orders
	 * 
	 * @return Set of table numbers that are occupied now
	 */
	public Set<Integer> getOccupiedTables();

}
