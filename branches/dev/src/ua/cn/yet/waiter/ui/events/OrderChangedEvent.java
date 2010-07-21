package ua.cn.yet.waiter.ui.events;

import java.util.EventObject;

import ua.cn.yet.waiter.model.Order;

/**
 * Event that signals that order was changed
 * 
 * @author Yuriy Tkach
 */
public class OrderChangedEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private Order order;

	public OrderChangedEvent(Object source, Order order) {
		super(source);
		this.order = order;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}
	
}
