package ua.cn.yet.waiter.ui.events;

import java.util.EventObject;

import ua.cn.yet.waiter.model.Order;

/**
 * Event about order created event
 * 
 * @author Yuriy Tkach
 */
public class OrderCreatedEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private Order order;

	public OrderCreatedEvent(Object source, Order order) {
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
