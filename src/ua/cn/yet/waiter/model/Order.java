package ua.cn.yet.waiter.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * Order that is created by waiter
 * 
 * @author Yuriy Tkach
 */
@Entity
@Table(name = "ordr")
@NamedQueries( {
		@NamedQuery(name = Order.QUERY_OPEN_USER_ORDERS, query = "SELECT x FROM Order x WHERE x.waiter = ?1 AND x.closed = false AND x.canceled = false"),
		@NamedQuery(name = Order.QUERY_CLOSED_USER_ORDERS, query = "SELECT x FROM Order x WHERE x.waiter = ?1 AND x.closed = true"),
		@NamedQuery(name = Order.QUERY_ALL_USER_ORDERS, query = "SELECT x FROM Order x WHERE x.waiter = ?1"),
		@NamedQuery(name = Order.QUERY_REMOVE_USER_FROM_USER_ORDERS, query = "UPDATE Order x SET x.waiter = null WHERE x.waiter = ?1"),
		@NamedQuery(name = Order.QUERY_ALL_OCCUPIED_TABLE_NUMBERS, query = "SELECT x.tableNumber FROM Order x WHERE x.closed = false AND x.canceled=false")
})
public class Order extends DomainObject {

	private static final long serialVersionUID = 1L;

	public static final String QUERY_OPEN_USER_ORDERS = "getOpenOrdersByUser";
	public static final String QUERY_CLOSED_USER_ORDERS = "getClosedOrdersByUser";
	public static final String QUERY_ALL_USER_ORDERS = "getAllOrdersByUser";
	public static final String QUERY_REMOVE_USER_FROM_USER_ORDERS = "removeUserFromUserOrders";
	public static final String QUERY_ORDERS = "getOrdersForCreationRange";
	public static final String QUERY_ALL_OCCUPIED_TABLE_NUMBERS = "getAllOccupiedTableNumbers";
	public static final String QUERY_ORDERS_NOTNAMED = "SELECT x FROM Order x WHERE ";
	public static final String CONDITION_AND = " AND ";
	public static final String CONDITION_CREATION_RANGE = " x.creationDate BETWEEN ?%d AND ?%d ";
	public static final String CONDITION_WAITER = " x.waiter = ?%d";
	public static final String CONDITION_CLOSED = " x.closed = %s";

	public static final int TABLE_BAR = 0;

	public static final int TABLE_NONE = -1;

	/** Defines whether order was already printed for the cook */
	private boolean printed=false;
	
	/** Number of the table */
	private int tableNumber = TABLE_NONE;

	/** Waiter of the order */
	@ManyToOne
	private User waiter;

	/** Specifies, if order is closed for changes */
	private boolean closed = false;
	
	/** Defines whether order was canceled by the waiter after it was printed for cook */
	private boolean canceled=false;
	
	

	/** Date and time, when order was created */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar creationDate = Calendar.getInstance();
	
	/** Date and time, when order was updated */
	@Transient
	private Calendar updateDate = Calendar.getInstance();

	/** Date and time, when order was closed */
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar closingDate = null;

	/** Items of the order */
	@OneToMany(mappedBy = "order", targetEntity = OrderedItem.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
	@Sort(type = SortType.NATURAL)
	private SortedSet<OrderedItem> items = new TreeSet<OrderedItem>();
	
	@OneToMany(mappedBy="order",targetEntity = LoggedChange.class, fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
	@Sort(type = SortType.NATURAL)
	private SortedSet<LoggedChange> changes=new TreeSet<LoggedChange>();
	
	/**
	 * Specifies, if item is marked for deletion. Only applies to closed orders.
	 * This is a convenient way to mark item for non-priviledged user.
	 */
	private Boolean forDeletion = false;

	/** Reason for marking item for deletion */
	private String forDeletionReason;

	/**
	 * Closing order by setting appropriate variables
	 */
	public void closeOrder() {
		closed = true;
		closingDate = Calendar.getInstance();
	}

	/**
	 * @return total sum of the order
	 */
	public double getSum() {
		double rez = 0;
		for (OrderedItem item : items) {
			rez += item.getOrderedPriceBillAndCoins();
		}
		return rez;
	}

		
	/**
	 * Getting sum of items of specified <code>type</code>
	 * 
	 * @param type
	 *            item type to count sum for
	 * @return total sum for item of the specified <code>type</code>
	 */
	public double getSumForType(ItemType type) {
		if (null == type) {
			return getSum();
		}
		double rez = 0;
		for (OrderedItem item : items) {
			if (type.equals(item.getItemType())) {
				rez += item.getOrderedPriceBillAndCoins();
			}
		}
		return rez;
	}

	/**
	 * @return Human-readable title
	 */
	public String getTitle() {
		if (getTableNumber() == TABLE_NONE) {
			return String.format("№%d", getId());
		} else if (getTableNumber() == TABLE_BAR) {
			return String.format("Бар (№%d)", getId());
		} else {
			return String.format("Ст. %d (№%d)", getTableNumber(), getId());
		}
	}

	/**
	 * @return Human-readable title of the table
	 */
	public String getTableString() {
		if (getTableNumber() == TABLE_NONE) {
			return "Без столика";
		} else if (getTableNumber() == TABLE_BAR) {
			return "Бар";
		} else {
			return "Столик № " + getTableNumber();
		}
	}

	/**
	 * Marking closed item for deletion with specifying a reason
	 * 
	 * @param reason
	 *            Reason for marking
	 * @return true if successful
	 */
	public boolean markForDeletion(String reason) {
		if (isClosed()) {
			forDeletion = true;
			forDeletionReason = reason;
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Marking item for canceling
	 * 
	 * @param reason
	 *            Reason for canceling
	 * @return true if successful
	 */
	public boolean markCanceled(String reason) {
		if (!isClosed() && ! isForDeletion()) {
			canceled = true;
			forDeletion = true;
			forDeletionReason = reason;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returning items that are not liquids and not for bar
	 * 
	 * @return Items only for cooking
	 */
	public SortedSet<OrderedItem> getItemsForCook() {
		SortedSet<OrderedItem> rez = new TreeSet<OrderedItem>();
		for (OrderedItem item : items) {
			if (!item.isLiquid() && !item.isBar()) {
				rez.add(item);
			}
		}
		return rez;
	}

	/**
	 * Returning items that are not alcohol and for bar
	 * 
	 * @return Items only for cooking
	 */
	public SortedSet<OrderedItem> getItemsForBar() {
		SortedSet<OrderedItem> rez = new TreeSet<OrderedItem>();
		for (OrderedItem item : items) {
			if (item.isBar() || (item.isLiquid() && !item.isAlcohol())) {
				rez.add(item);
			}
		}
		return rez;
	}

	/**
	 * Returning items that are alcohol
	 * 
	 * @return Items only for cooking
	 */
	public SortedSet<OrderedItem> getItemsForAlcohol() {
		SortedSet<OrderedItem> rez = new TreeSet<OrderedItem>();
		for (OrderedItem item : items) {
			if (item.isAlcohol()) {
				rez.add(item);
			}
		}
		return rez;
	}
	
	/**
	 * @return if order is opened or not
	 */
	public boolean isOpen() {
		return !isClosed();
	}

	/**
	 * @return the tableNumber
	 */
	public int getTableNumber() {
		return tableNumber;
	}

	/**
	 * @param tableNumber
	 *            the tableNumber to set
	 */
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	/**
	 * @return the waiter
	 */
	public User getWaiter() {
		return waiter;
	}

	/**
	 * @param waiter
	 *            the waiter to set
	 */
	public void setWaiter(User waiter) {
		this.waiter = waiter;
	}

	/**
	 * @return the closed
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * @param closed
	 *            the closed to set
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * @return the items
	 */
	public SortedSet<OrderedItem> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(SortedSet<OrderedItem> items) {
		this.items = items;
	}

	/**
	 * @return the creationDate
	 */
	public Calendar getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the closingDate
	 */
	public Calendar getClosingDate() {
		return closingDate;
	}

	/**
	 * @param closingDate
	 *            the closingDate to set
	 */
	public void setClosingDate(Calendar closingDate) {
		this.closingDate = closingDate;
	}

	/**
	 * @return the forDeletion
	 */
	public boolean isForDeletion() {
		if (forDeletion != null) {
			return forDeletion;
		} else {
			return false;
		}
	}

	/**
	 * @param forDeletion
	 *            the forDeletion to set
	 */
	public void setForDeletion(Boolean forDeletion) {
		this.forDeletion = forDeletion;
	}

	/**
	 * @return the forDeletionReason
	 */
	public String getForDeletionReason() {
		return forDeletionReason;
	}

	/**
	 * @param forDeletionReason
	 *            the forDeletionReason to set
	 */
	public void setForDeletionReason(String forDeletionReason) {
		this.forDeletionReason = forDeletionReason;
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.model.DomainObject#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("id", getId());
		sb.append("table", getTableString());
		sb.append("waiter", (getWaiter() != null) ? getWaiter().getUsername() : "null");
		SimpleDateFormat df = new SimpleDateFormat();
		sb.append("created", df.format(getCreationDate().getTime()));
		sb.append("closed", isClosed() ? df.format(getClosingDate().getTime()) : "open");
		sb.append("itemCount", getItems().size());
		sb.append("sum", getSum());
		sb.append("forDel", isForDeletion());
		if (isForDeletion()) {
			sb.append("forDelReason", getForDeletionReason());
		}
		return sb.toString();
	}

	/**
	 * @return the printed
	 */
	public boolean isPrinted() {
		return printed;
	}

	/**
	 * @param printed the printed to set
	 */
	public void setPrinted(boolean printed) {
		this.printed = printed;
	}

	/**
	 * @return the canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * @param canceled the canceled to set
	 */
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	/** 
	 * Defines whether order was changed by the waiter after it was printed for cook
	 * @return true if order was changed
	 */
	public boolean isChanged() {
		if(getChanges().size()>0){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return log of the changes made to the order, uses html markup
	 */
	public String getLoggedChangesHtml(){
		StringBuffer changes=new StringBuffer();
		SimpleDateFormat sdf=new SimpleDateFormat();
		
		if(getChanges().size()>0){
			for(LoggedChange loggedChange:getChanges()){
				changes.append(
						"<b>"+sdf.format(loggedChange.getTime().getTime())+":</b> "+
						"<i>"+loggedChange.getItemName()+"</i>, "+
						loggedChange.getMessage()+"<br/>");
			}
		}
		return changes.toString();
	}
	
	/**
	 * In all the order items sets printed attribute to true and updated to false;
	 */
	public void processItemsAfterPrinting(){
		for(OrderedItem item: items){
			item.setPrinted(true);
			item.setUpdated(false);
		}
	}

	/**
	 * @return the changes
	 */
	public SortedSet<LoggedChange> getChanges() {
		return changes;
	}

	/**
	 * @param changes the changes to set
	 */
	public void setChanges(SortedSet<LoggedChange> changes) {
		this.changes = changes;
	}

	/**
	 * @return the updateDate
	 */
	public Calendar getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Calendar updateDate) {
		this.updateDate = updateDate;
	}
			
}
