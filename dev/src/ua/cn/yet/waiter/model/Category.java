package ua.cn.yet.waiter.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotEmpty;

/**
 * Category of the item
 * 
 * @author Yuriy Tkach
 */
@Entity
public class Category extends DomainObject implements OutputElement, Comparable<Category> {
	
	private static final long serialVersionUID = 1L;

	/** Name of the category */
	@NotEmpty
	private String name;
	
	/** Picture of the category */
	private String picture;
	
	/** Items of the category */
	@OneToMany(fetch=FetchType.EAGER, targetEntity=Item.class, cascade={CascadeType.REMOVE})
	@Sort(type=SortType.NATURAL)
	private SortedSet<Item> items = new TreeSet<Item>();
	
	/**
	 * Plain constructor
	 */
	public Category () {}

	/**
	 * Constructor with fields
	 */
	public Category(String name, String picture) {
		super();
		this.name = name;
		this.picture = picture;
	}
	
	/**
	 * Checking if item with provided name exists in category
	 * 
	 * @param name
	 *            Name to check
	 * @return True if item exists
	 */
	public boolean itemExists(String name) {
		for (Item item : getItems()) {
			if (item.getName().compareToIgnoreCase(name) == 0) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.model.OutputElement#isDisabled()
	 */
	public boolean isDisabled() {
		return items.isEmpty();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}

	/**
	 * @return the items
	 */
	public SortedSet<Item> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(SortedSet<Item> items) {
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Category o) {
		int rez = -1;
		if ((o != null) && (o.getName() != null) && (this.getName() != null)) {
			rez = this.getName().compareToIgnoreCase(o.getName());
		}
		return rez;
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.model.DomainObject#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("id", getId());
		sb.append("name", name);
		sb.append("pic", picture);
		sb.append("items", items.size());
		
		return sb.toString();
	}
}
