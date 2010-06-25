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
	 * then all orders were taken into account
	 */
	private Boolean closedOrders;

	/** Total sums for separate item types */
	private Map<ItemType, Double> totalsForType = new HashMap<ItemType, Double>();

	/** Total sum for the report */
	private double totalSum = 0;

	/** Total number of orders for report */
	private int totalOrders = 0;

	/** Total sums for separate item types. Only open orders */
	private Map<ItemType, Double> totalsForTypeOpenOrders = new HashMap<ItemType, Double>();

	/** Total sum for the report. Only open orders */
	private double totalSumOpenOrders = 0;

	/** Total number of open orders */
	private int totalOpenOrders = 0;

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
		}
	}

	/**
	 * Adding order to the report
	 * 
	 * @param order
	 *            Order to add
	 */
	public void addOrder(Order order) {
		totalOrders++;
		if (order.isOpen()) {
			totalOpenOrders++;
		}

		totalSum += order.getSum();
		if (order.isOpen()) {
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

	public String getTotalsForTypeOpenOrdersStr(ItemType type) {
		return String.format("%.2f грн.", getTotalsForTypeOpenOrders(type));
	}

	public Double getTotalsForBarOpenOrders() {
		Double rez = getTotalsForTypeOpenOrders(ItemType.BAR)
				+ getTotalsForTypeOpenOrders(ItemType.SOFT_DRINK);
		return rez;
	}

	public String getTotalsForBarOpenOrdersStr() {
		return String.format("%.2f грн.", getTotalsForBarOpenOrders());
	}

	public String getTotalSumOpenOrdersStr() {
		return String.format("%.2f грн.", totalSumOpenOrders);
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

	public boolean isClosedAndOpenIncluded() {
		return null == closedOrders;
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
		sb.append("orders", (isClosedAndOpenIncluded()) ? "all"
				: (closedOrders) ? "closed" : "open");
		return sb.toString();
	}

}
