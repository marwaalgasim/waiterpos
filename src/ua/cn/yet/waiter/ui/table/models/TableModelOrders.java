package ua.cn.yet.waiter.ui.table.models;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.EventBus;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.ui.ReasonForDelInputDialog;
import ua.cn.yet.waiter.ui.events.OrderChangedEvent;
import ua.cn.yet.waiter.ui.events.OrderDeletedEvent;
import ua.cn.yet.waiter.util.WaiterInstance;

public class TableModelOrders extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(TableModelOrders.class);

	public static final int COLUMN_DEL = 10;
	public static final int COLUMN_MARK_DEL = 9;
	public static final int COLUMN_CANCELLED = 8;
	public static final int COLUMN_CHANGED = 7;
	public static final int COLUMN_SUM = 6;
	public static final int COLUMN_DISCOUNT = 5;
	public static final int COLUMN_CLOSED = 4;
	public static final int COLUMN_CREATED = 3;
	public static final int COLUMN_WAITER = 2;
	public static final int COLUMN_TABLE = 1;
	public static final int COLUMN_ID = 0;

	private List<Order> orders = new ArrayList<Order>();

	boolean adminView = false;

	private User user;
	
	/**
	 * Names of all columns
	 */
	private final String[] columnNames = { "№", "Столик", "Официант", "Создан",
			"Закрыт","Скидка", "Сумма", "Изменен", "Отменен", "Удален", "" };

	private OrderService orderService;

	private Window parentWindow;

	public TableModelOrders(Window parent, User waiter, boolean loadOrdersAfterCreation) {
		this.parentWindow = parent;
		this.user = waiter;
		
		orderService = WaiterInstance.forId(WaiterInstance.ORDER_SERVICE);
		
		if (null == user) {
			adminView = true;
		}
		
		if (loadOrdersAfterCreation) {
			updateLocalOrderList();
		}
	}

	/**
	 * Updating local list of orders for {@link #user}
	 */
	private void updateLocalOrderList() {
		orders.clear();

		if (null == user) {
			orders.addAll(orderService.getAllEntites());
		} else {
			orders.addAll(orderService.getAllUserOrders(user, true));
		}
		fireTableDataChanged();
	}
	
	public void filterOrders(Calendar from, Calendar to, User waiter,
			Boolean closed) {
		orders.clear();
		orders.addAll(orderService.getOrdersForRange(from, to, waiter, closed));
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		if (adminView) {
			return columnNames.length;
		} else {
			return columnNames.length - 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return orders.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if ((rowIndex < 0) || (rowIndex >= getRowCount()) || (columnIndex < 0)
				|| (columnIndex >= getColumnCount())) {
			return null;
		} else {
			Order order = orders.get(rowIndex);

			switch (columnIndex) {
			case COLUMN_ID:
				return order.getId();
			case COLUMN_TABLE:
				return order.getTableString();
			case COLUMN_WAITER:
				if (null == order.getWaiter()) {
					return "";
				} else {
					return order.getWaiter().getFullName();
				}
			case COLUMN_CREATED:
				return order.getCreationDate();
			case COLUMN_CLOSED:
				return order.getClosingDate();
			case COLUMN_DISCOUNT:
				return String.format("%.0f",order.getDiscount()*100)+"%";
			case COLUMN_SUM:
				return order.getSum();
			case COLUMN_MARK_DEL:
				return order.isForDeletion();
			case COLUMN_DEL:
				return order;
			case COLUMN_CANCELLED:
				return order.isCanceled();
			case COLUMN_CHANGED:
				return order.isChanged();
			default:
				return null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case COLUMN_DEL:
			return true;
		case COLUMN_MARK_DEL:
			Order order = getOrderAt(rowIndex);
			return (!this.adminView) && (order.isClosed());
		default:
			return false;
		}
	}

	/**
	 * @param order
	 *            order to delete
	 * @throws Exception
	 *             If error occurs
	 */
	public void deleteOrder(Order order) throws Exception {
		orderService.delEntity(order);
		EventBus.publish(new OrderDeletedEvent(this, order.getId()));
	}
	
	/**
	 * @param row
	 *            row to get order at
	 * @return found order
	 */
	public Order getOrderAt(int row) {
		if ((row < 0) || (row >= getRowCount())) {
			return null;
		} else {
			return orders.get(row);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if ((rowIndex < 0) || (rowIndex >= getRowCount()) || (columnIndex < 0)
				|| (columnIndex >= getColumnCount()) || (null == aValue)) {
			return;
		}

		Order order = getOrderAt(rowIndex);

		boolean doUpdate = false;

		switch (columnIndex) {
		case COLUMN_MARK_DEL:
			Boolean newValue = (Boolean) aValue;
			if (order.isForDeletion() != newValue) {
				if (newValue == true) {
					String reason = ReasonForDelInputDialog.getReason(parentWindow);
					if (StringUtils.isNotBlank(reason)) {
						doUpdate = order.markForDeletion(reason);
					}
				} else {
					order.setForDeletion(false);
					order.setForDeletionReason("");
					doUpdate = true;
				}
			}
			break;
		}

		if (doUpdate) {
			try {
				Order savedOrder = orderService.save(order);
				EventBus.publish(new OrderChangedEvent(this, savedOrder));
				fireTableDataChanged();
			} catch (Exception e) {
				log.error("Failed to update order: " + order, e);
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
						"Не получилось обновить заказ :(",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
