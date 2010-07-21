package ua.cn.yet.waiter.ui.events;

import java.util.EventObject;

/**
 * Event that is broadcasted when order is deleted
 * 
 * @author Yuriy Tkach
 */
public class OrderDeletedEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private Long orderId;
	
	public OrderDeletedEvent(Object source, Long orderId) {
		super(source);
		this.orderId = orderId;
	}

	/**
	 * @return the orderId
	 */
	public Long getOrderId() {
		return orderId;
	}

}
