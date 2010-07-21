package ua.cn.yet.waiter.service;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderReport;

/**
 * Service for printing order and receipts
 * 
 * @author Yuriy Tkach
 */
public interface PrintingService {

	/**
	 * Printing order for personnel.
	 * 
	 * @param order
	 *            Order to print
	 * @return true if printing was successful
	 */
	public boolean printOrder(Order order);

	/**
	 * Printing receipt for user
	 * 
	 * @param order
	 *            Order to print
	 * @return true if printing was successful
	 */
	public boolean printReceipt(Order order);

	/**
	 * Printing order report
	 * 
	 * @param report
	 *            Order report to print
	 * @return true if printing was successful
	 */
	public boolean printOrderReport(OrderReport report);

}
