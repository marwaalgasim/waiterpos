package ua.cn.yet.waiter.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

/**
 * Item that was ordered
 * 
 * @author Yuriy Tkach
 */
@Entity
public class OrderedItem extends AbstractItem {

	private static final long serialVersionUID = 1L;

	/**
	 * Number of items in the order.
	 */
	@Min(value = 1)
	private int count;

	/**
	 * New mass for ordered item.
	 */
	@Min(value = 1)
	private int newMass;

	/** Order for this ordered item */
	@ManyToOne
	@NotNull
	private Order order;
	
	/** Indicates whether item was already printed for the cook */
	private Boolean printed = false;
	
	/** Indicates whether item was updated after print */
	private Boolean updated = false;
	

	public OrderedItem() {
		super();
	}

	public OrderedItem(Item item, Order order, int count, int newMass) {
		super();
		this.count = count;
		this.newMass = newMass;

		setItemType(item.getItemType());
		setMass(item.getMass());
		setName(item.getName());
		setPrice(item.getPrice());

		setOrder(order);
	}

	/**
	 * Firstly, getting adjusted price for new mass, from the original price and
	 * original mass. Then, converting to bills and coins (div by 100), then
	 * multiplying by count
	 * 
	 * @return price for this ordered item in bills and coins
	 */
	public double getOrderedPriceBillAndCoins() {
		return getOrderedPriceBillAndCoins(getNewMass(), getCount());
	}

	/**
	 * Firstly, getting adjusted price for new mass, from the original price and
	 * original mass. Then, converting to bills and coins (div by 100), then
	 * multiplying by count
	 * 
	 * @param newMass
	 *            new mass of the item
	 * @param count
	 *            count of the items
	 * 
	 * @return price for this ordered item in bills and coins
	 */
	public double getOrderedPriceBillAndCoins(int newMass, int count) {
		BigDecimal priceForMass = getPrice().multiply(
				BigDecimal.valueOf((double) newMass / getMass()));

		return priceForMass.divide(new BigDecimal(100)).multiply(
				new BigDecimal(count)).doubleValue();
	}

	/**
	 * Checking items logically only by some params and name.
	 * 
	 * @param other
	 *            Item to check for equals
	 * @return true if <code>this</code> logically equals to <code>other</code>
	 */
	public boolean equalsLogically(OrderedItem other) {
		if (null == other) {
			return false;
		}

		if (this.isLiquid() != other.isLiquid()) {
			return false;
		} else {
			if (this.isAlcohol() != other.isAlcohol()) {
				return false;
			}
		}

		return this.getName().equals(other.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.model.DomainObject#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("id", getId());
		sb.append("name", getName());
		sb.append("price", getOrderedPriceBillAndCoins());
		sb.append("count", getCount());
		sb.append("newMass", getNewMass());
		sb.append("org price", getPrice());
		sb.append("org mass", getMass());
		sb.append("order id", getOrder().getId());
		if (null == getOrder().getWaiter()) {
			sb.append("waiter", "");
		} else {
			sb.append("waiter", getOrder().getWaiter().getUsername());
		}
		sb.append("item type", getItemType());
		return sb.toString();
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @return the newMass
	 */
	public int getNewMass() {
		return newMass;
	}

	/**
	 * @param newMass
	 *            the newMass to set
	 */
	public void setNewMass(int newMass) {
		this.newMass = newMass;
	}

	/**
	 * @return the printed
	 */
	public Boolean isPrinted() {
		return printed;
	}

	/**
	 * @param printed the printed to set
	 */
	public void setPrinted(Boolean printed) {
		this.printed = printed;
	}

	/**
	 * @return the updated
	 */
	public Boolean isUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(Boolean updated) {
		if(printed){
			this.updated = updated;
		}
	}
	
}
