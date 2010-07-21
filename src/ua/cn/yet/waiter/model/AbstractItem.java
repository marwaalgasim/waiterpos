package ua.cn.yet.waiter.model;

import java.math.BigDecimal;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

/**
 * Class for all kinds of items
 * 
 * @author Yuriy Tkach
 */
@MappedSuperclass
public abstract class AbstractItem extends DomainObject implements
		Comparable<AbstractItem> {

	private static final long serialVersionUID = 1L;

	/** Specifies, that this item does not have mass and can be only counted */
	public static final int SINGLE_ITEM_NO_MASS = 1;

	/** Name of the item */
	@NotEmpty
	protected String name;

	/** Price of items in UAH coins */
	@NotNull
	protected BigDecimal price;

	/** Mass in grams for the item */
	protected int mass;

	/** Type of the item */
	@Enumerated(EnumType.STRING)
	protected ItemType itemType = ItemType.FOOD;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.model.OutputElement#isDisabled()
	 */
	public boolean isDisabled() {
		return false;
	}

	/**
	 * Getting price in bills and coins from the price only in coins
	 * 
	 * @return price in bills and coins
	 */
	public double getPriceBillsAndCoins() {
		if (price != null) {
			return price.divide(new BigDecimal(100)).doubleValue();
		} else {
			return 0;
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * @param price
	 *            the price in bills and coins to set
	 */
	public void setPriceBillsAndCoins(double price) {
		this.price = BigDecimal.valueOf(price * 100);
	}

	/**
	 * @return the mass
	 */
	public int getMass() {
		return mass;
	}

	/**
	 * @param mass
	 *            the mass to set
	 */
	public void setMass(int mass) {
		this.mass = mass;
	}

	/**
	 * Checks if this item's mass can be edited when ordered. If item mass is
	 * {@link #SINGLE_ITEM_NO_MASS}, then mass edit is not allowed
	 * 
	 * @return true if item's mass can be edited when ordered
	 */
	public boolean isMassEditableInOrder() {
		return mass != SINGLE_ITEM_NO_MASS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AbstractItem o) {
		int rez = -1;

		if ((o != null) && (o.getName() != null) && (this.getName() != null)) {
			rez = this.getName().compareToIgnoreCase(o.getName());
		}

		return rez;
	}

	/**
	 * @return the liquid
	 */
	public boolean isLiquid() {
		return ItemType.SOFT_DRINK.equals(getItemType())
				|| ItemType.ALCOHOL.equals(getItemType());
	}

	/**
	 * @return the alcohol
	 */
	public boolean isAlcohol() {
		return ItemType.ALCOHOL.equals(getItemType());
	}

	/**
	 * @return the bar
	 */
	public boolean isBar() {
		return ItemType.BAR.equals(getItemType());
	}

	/**
	 * @return the itemType
	 */
	public ItemType getItemType() {
		if (null == itemType) {
			return ItemType.FOOD;
		} else {
			return itemType;
		}
	}

	/**
	 * @param itemType
	 *            the itemType to set
	 */
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

}
