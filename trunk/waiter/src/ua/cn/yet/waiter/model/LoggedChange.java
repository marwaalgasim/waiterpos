package ua.cn.yet.waiter.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="logged_changes")
/**
 * Entity that holds log of the changes made to the order. Logging starts after order is printed for the cook.
 */
public class LoggedChange extends DomainObject implements
		Comparable<LoggedChange>{
	
	private static final long serialVersionUID = 1L;

	/** Order that was changed */
	@ManyToOne
	private Order order;
	
	/** Name of the item in the order that was changed */
	private String itemName;
	
	/** Log message. Describes the change. */
	private String message;
	
	/** Date and time when this entity was created (assuming that change was made at the same time) */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar time=Calendar.getInstance();
	
	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the time
	 */
	public Calendar getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Calendar time) {
		this.time = time;
	}

	public LoggedChange() {
		super();
	}

	public LoggedChange(Order order, String itemName, String message) {
		super();
		this.order = order;
		this.itemName = itemName;
		this.message = message;
	}

	@Override
	public int compareTo(LoggedChange o) {
		return time.compareTo(o.getTime());
	}

	
	
}
