package ua.cn.yet.waiter.model;

import javax.persistence.Entity;

/**
 * Item that is being sold
 * 
 * @author Yuriy Tkach
 */
@Entity
public class Item extends AbstractItem implements OutputElement {

	private static final long serialVersionUID = 1L;
	
	/** Full path to the item's picture */
	private String picture;
	
	public Item() {
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @param picture
	 *            the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
}
