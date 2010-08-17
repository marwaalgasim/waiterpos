package ua.cn.yet.waiter.model;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Class that holds order report for the specified from
 * 
 * @author Yuriy Tkach
 */
public class OrderReport {

	/** 
	 * Report start date range.
	 * Can be <code>null</code> - that means that all dates are included.
	 */
	private Calendar from;
	
	/** 
	 * Report end date range.
	 * Can be <code>null</code> - that means that all dates are included.
	 */
	private Calendar to;

	/**
	 * Owner of all orders. If <code>null</code> then all waiters were taken
	 * into account
	 */
	private User waiter;

	/**
	 * If only closedOrders or open orders are included. If <code>null</code>
	 * then both of them are taken into account
	 */
	private Boolean closedOrders;
	
	/**
	 * If only deleted/cancelled or normal orders are included. If <code>null</code>
	 * then both of them orders were taken into account
	 */
	private Boolean deletedOrders;

	/** Total sums for separate item types, excluding deleted or canceled orders */
	private Map<ItemType, Double> totalsForType = new HashMap<ItemType, Double>();

	/** Total sum for the report, excluding deleted or canceled orders */
	private double totalSum = 0;

	/** Total number of orders for report, excluding deleted or canceled orders */
	private int totalOrders = 0;

	/** Total sums for separate item types. Only open orders */
	private Map<ItemType, Double> totalsForTypeOpenOrders = new HashMap<ItemType, Double>();

	/** Total sum for the report. Only open orders */
	private double totalSumOpenOrders = 0;

	/** Total number of open orders */
	private int totalOpenOrders = 0;
	
	/** Total number of canceled/deleted orders */
	private int totalDelOrders = 0;
	
	/** Total sum of deleted or canceled orders */
	private double totalSumDelOrders = 0;
	
	/** Total sums for separate item types. Only deleted or canceled orders */
	private Map<ItemType, Double> totalsForTypeDelOrders = new HashMap<ItemType, Double>();

	/**
	 * Creating report object for specified date range and initializing
	 * maps of totals for types with zero values
	 * 
	 * @param from
	 *            Start of the date range for the report
	 * @param to End date of report date range
	 */
	public OrderReport(Calendar from, Calendar to) {
		super();
		this.from = from;
		this.to = to;

		ItemType[] types = ItemType.values();
		for (ItemType itemType : types) {
			totalsForType.put(itemType, Double.valueOf(0));
			totalsForTypeOpenOrders.put(itemType, Double.valueOf(0));
			totalsForTypeDelOrders.put(itemType, Double.valueOf(0));
		}
	}

	/**
	 * Adding order to the report
	 * 
	 * @param order
	 *            Order to add
	 */
	public void addOrder(Order order) {
		
		if (order.isForDeletion() || order.isCanceled()) {
			totalDelOrders++;
			totalSumDelOrders += order.getSum();
		}
		
		totalOrders++;
		totalSum += order.getSum();
		
		if (order.isOpen()) {
			totalOpenOrders++;
			totalSumOpenOrders += order.getSum();
		}
		
		ItemType[] types = ItemType.values();
		for (ItemType itemType : types) {
			Double sumForType = order.getSumForType(itemType);
			
			Double totalSumForType = getTotalsForType(itemType);
			totalsForType.put(itemType, totalSumForType + sumForType);

			if (order.isOpen()) {
				totalSumForType = getTotalsForTypeOpenOrders(itemType);
				totalsForTypeOpenOrders.put(itemType, totalSumForType
						+ sumForType);
			}
			
			if (order.isForDeletion() || order.isCanceled()) {
				totalSumForType = getTotalsForTypeDelOrders(itemType);
				totalsForTypeDelOrders.put(itemType, totalSumForType + sumForType);
			} 	

		}
	}

	public String getFromDateStr() {
		if (null == from) {
			return "Все время";
		}
		String dateStr;
		try {
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
			dateStr = df.format(from.getTime());
		} catch (Exception e) {
			dateStr = "";
		}
		return dateStr;
	}
	
	public String getToDateStr() {
		if (null == to) {
			return "";
		}
		String dateStr;
		try {
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
			dateStr = df.format(to.getTime());
		} catch (Exception e) {
			dateStr = "";
		}
		return dateStr;
	}
	
	public Boolean isClosedAndOpenIncluded(){
		return null == closedOrders;
	}

	public Double getTotalsForType(ItemType type) {
		Double rez = totalsForType.get(type);
		if (null == rez) {
			rez = 0.0;
		}
		return rez;
	}

	public String getTotalsForTypeStr(ItemType type) {
		return String.format("%.2f грн.", getTotalsForType(type));
	}

	public Double getTotalsForBar() {
		Double rez = getTotalsForType(ItemType.BAR)
				+ getTotalsForType(ItemType.SOFT_DRINK);
		return rez;
	}

	public String getTotalsForBarStr() {
		return String.format("%.2f грн.", getTotalsForBar());
	}

	public String getTotalSumStr() {
		return String.format("%.2f грн.", totalSum);
	}

	public String getTotalOrdersStr() {
		return String.valueOf(totalOrders);
	}

	public Double getTotalsForTypeOpenOrders(ItemType type) {
		Double rez = totalsForTypeOpenOrders.get(type);
		if (null == rez) {
			rez = 0.0;
		}
		return rez;
	}
	
	public Double getTotalsForTypeDelOrders(ItemType type) {
		Double rez = totalsForTypeDelOrders.get(type);
		if (null == rez) {
			rez = 0.0;
		}
		return rez;
	}

	public String getTotalsForTypeOpenOrdersStr(ItemType type) {
		return String.format("%.2f грн.", getTotalsForTypeOpenOrders(type));
	}
	
	public String getTotalsForTypeDelOrdersStr(ItemType type) {
		return String.format("%.2f грн.", getTotalsForTypeDelOrders(type));
	}

	public Double getTotalsForBarOpenOrders() {
		Double rez = getTotalsForTypeOpenOrders(ItemType.BAR)
				+ getTotalsForTypeOpenOrders(ItemType.SOFT_DRINK);
		return rez;
	}
	
	public Double getTotalsForBarDelOrders() {
		Double rez = getTotalsForTypeDelOrders(ItemType.BAR)
				+ getTotalsForTypeDelOrders(ItemType.SOFT_DRINK);
		return rez;
	}

	public String getTotalsForBarOpenOrdersStr() {
		return String.format("%.2f грн.", getTotalsForBarOpenOrders());
	}

	public String getTotalsForBarDelOrdersStr() {
		return String.format("%.2f грн.", getTotalsForBarDelOrders());
	}
	public String getTotalSumOpenOrdersStr() {
		return String.format("%.2f грн.", totalSumOpenOrders);
	}
	
	public String getTotalSumDelOrdersStr() {
		return String.format("%.2f грн.", totalSumDelOrders);
	}

	/**
	 * @return the totalOpenOrders
	 */
	public int getTotalOpenOrders() {
		return totalOpenOrders;
	}

	public String getTotalOpenOrdersStr() {
		return String.valueOf(totalOpenOrders);
	}
	
	public int getTotalDelOrders() {
		return totalDelOrders;
	}

	public String getTotalDelOrdersStr() {
		return String.valueOf(totalDelOrders);
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
	 * @return the closedOrders
	 */
	public Boolean getOnlyClosed() {
		return closedOrders;
	}
	
	public Boolean getOnlyDeleted() {
		return deletedOrders;
	}
	
	public void setOnlyDeleted(boolean onlyDeleted) {
		this.deletedOrders = onlyDeleted;
	}

	/**
	 * @param closedOrders
	 *            the closedOrders to set
	 */
	public void setOnlyClosed(Boolean closed) {
		this.closedOrders = closed;
	}

	public boolean isAllWaitersIncluded() {
		return null == waiter;
	}

	public boolean isAllIncluded() {
		return (null == closedOrders && null == deletedOrders);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("from", getFromDateStr());
		sb.append("waiter", (getWaiter() != null) ? getWaiter().getUsername()
				: "all");
		
		String status = "";
		if (isAllIncluded()) {
			status = "all";
		} else {
			if (closedOrders != null) {
				status += closedOrders? "closed ":"open ";
			}
			if (deletedOrders != null) {
				status += deletedOrders? "deleted":"not deleted";
			}
		}
		
		sb.append("orders", status);
		return sb.toString();
	}

	/**
	 * @return the totalSum
	 */
	public double getTotalSum() {
		return totalSum;
	}

	/**
	 * @return the totalSumOpenOrders
	 */
	public double getTotalSumOpenOrders() {
		return totalSumOpenOrders;
	}

	/**
	 * @return the totalSumDelOrders
	 */
	public double getTotalSumDelOrders() {
		return totalSumDelOrders;
	}

	/**
	 * @return the totalOrders
	 */
	public int getTotalOrders() {
		return totalOrders;
	}
		
}
