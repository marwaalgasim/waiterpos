package ua.cn.yet.waiter.ui.table.models;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.EventBus;

import ua.cn.yet.waiter.model.LoggedChange;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderedItem;
import ua.cn.yet.waiter.service.LoggedChangeService;
import ua.cn.yet.waiter.service.OrderedItemService;
import ua.cn.yet.waiter.ui.events.OrderChangedEvent;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Table model for the receipt table that stores ordered items
 * 
 * @author Yuriy Tkach
 */
public class TableModelReceipt extends AbstractTableModel {

	private static final Log log = LogFactory.getLog(TableModelReceipt.class);

	public static final int COLUMN_PRICE = 4;

	public static final int COLUMN_COUNT = 3;

	public static final int COLUMN_MASS = 2;

	public static final int COLUMN_BASE_PRICE = 1;

	public static final int COLUMN_NAME = 0;

	private static final long serialVersionUID = 1L;

	private boolean allowEdit = true;

	public TableModelReceipt(boolean allowEdit) {
		super();
		this.allowEdit = allowEdit;
		service = WaiterInstance.forId(WaiterInstance.ORDERED_ITEM_SERVICE);
		loggedChangeService=WaiterInstance.forId(WaiterInstance.LOGGED_CHANGE_SERVICE);
	}

	/**
	 * Names of all columns
	 */
	private final String[] columnNames = { "Наименование", "Цена", "Масса",
			"Кол-во", "Сумма" };

	private SortedSet<OrderedItem> items = new TreeSet<OrderedItem>();

	private OrderedItemService service;
	
	private LoggedChangeService loggedChangeService;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return items.size();
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
			OrderedItem item = getItemFromSet(rowIndex);

			switch (columnIndex) {
			case COLUMN_NAME:
				return item.getName();
			case COLUMN_BASE_PRICE:
				return item.getPriceBillsAndCoins();
			case COLUMN_MASS:
				return item.getNewMass();
			case COLUMN_COUNT:
				return item.getCount();
			case COLUMN_PRICE:
				return item.getOrderedPriceBillAndCoins();

			default:
				return null;
			}
		}
	}

	/**
	 * Getting item from set by index
	 * 
	 * @param rowIndex
	 *            Row index to search
	 * @return Ordered item
	 */
	public OrderedItem getItemFromSet(int rowIndex) {
		Iterator<OrderedItem> iter = items.iterator();
		while (0 != (rowIndex--)) {
			iter.next();
		}
		OrderedItem item = iter.next();
		return item;
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

		OrderedItem item = getItemFromSet(rowIndex);

		updateItemValue(aValue, columnIndex, item);
	}

	/**
	 * Updating value for the item
	 * 
	 * @param aValue
	 *            new value
	 * @param columnIndex
	 *            column index of value to update
	 * @param item
	 *            Item to update
	 */
	public void updateItemValue(Object aValue, int columnIndex, OrderedItem item) {
		Integer iValue;
		try {
			iValue = Integer.valueOf(aValue.toString());
		} catch (Exception e) {
			return;
		}

		switch (columnIndex) {
		case COLUMN_COUNT:
			if (iValue != item.getCount()) {
				item.setCount(iValue);
			} else {
				return;
			}
			break;
		case COLUMN_MASS:
			if (iValue != item.getNewMass()) {
				item.setNewMass(iValue);
			} else {
				return;
			}
			break;
		}

		persistOrderItem(item);
	}

	/**
	 * Persisting order item
	 * 
	 * @param item
	 *            Item to persist
	 */
	public void persistOrderItem(OrderedItem item) {
		try {
			
			if(items.contains(item)){
				logItemChanges(item, service.getEntityById(item.getId()));
			}
					
			OrderedItem savedItem = service.save(item);
			
			Order order = savedItem.getOrder();
			EventBus.publish(new OrderChangedEvent(this, order));

			items.remove(item);
			items.add(savedItem);

			fireTableDataChanged();
		} catch (Exception e) {
			log.error("Failed to update item: " + item, e);
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"Не получилось сохранить элемент :(",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Creates LoggedChange entity that describes changes to the specified item.
	 * Changes will be logged only if user decreased item mass or count.
	 * Rising values are not logged.
	 * Logging starts after the order was printed for the cook.
	 * @param item item after changes were made
	 * @param oldItem item before changes
	 */
	private void logItemChanges(OrderedItem item,OrderedItem oldItem){
		
		//if order was not printed there is no need in logging
		if(!item.getOrder().isPrinted()){
			return;
		}
		
		String changeDescription="";
				
		if(oldItem.getNewMass()>item.getNewMass()){
			changeDescription+="изменение массы: "+oldItem.getNewMass()+" -> "+item.getNewMass()+"; ";
		}
		if(oldItem.getCount()>item.getCount()){
			changeDescription+="изменение количества: "+oldItem.getCount()+" -> "+item.getCount()+"; ";
		}

		if(StringUtils.isNotBlank(changeDescription)){
			Order order=item.getOrder();
			LoggedChange loggedChange=new LoggedChange(order,item.getName(),changeDescription);
			order.getChanges().add(loggedChange);
			try {
				loggedChangeService.save(loggedChange);
			} catch (Exception e) {
				log.error("Failed to update loggedChange: " + order, e);
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
						"Не получилось сохранить изменения :(",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Creates LoggedChange entity that logs deletion of specified item.
	 * Logging starts after the order was printed for the cook.
	 * @param item item that is going to be deleted
	 */
	private void logItemDeletion(OrderedItem item){
		
		//if order was not printed there is no need in logging
		if(!item.getOrder().isPrinted()){
			return;
		}
		
		String changeDescription="удаление из заказа;";
		
		Order order=item.getOrder();
				
		LoggedChange loggedChange=new LoggedChange(order,item.getName(),changeDescription);
		order.getChanges().add(loggedChange);
		
		try {
			loggedChangeService.save(loggedChange);
		} catch (Exception e) {
			log.error("Failed to update loggedChange: " + order, e);
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"Не получилось сохранить изменения :(",
					JOptionPane.ERROR_MESSAGE);
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
		if (!allowEdit) {
			return false;
		}
		OrderedItem item = getItemFromSet(rowIndex);

		switch (columnIndex) {
		case COLUMN_COUNT:
			return true;
		case COLUMN_MASS:
			return item.isMassEditableInOrder();
		default:
			return false;
		}
	}

	/**
	 * Adding ordered item to the tableModel
	 * 
	 * @param item
	 *            Item to add
	 */
	public void addItem(OrderedItem item) {
		if (null == item) {
			return;
		}

		item = processItemBeforeAdding(item);
		
		persistOrderItem(item);
	}

	/**
	 * Checking if passed item already added. If so, then just processing it,
	 * based on type: increasing count or mass (if new item mass is different).
	 * If item is not found, then just returning passed item, previously adding
	 * it to the order
	 * 
	 * @param item
	 *            Item to add
	 * @return already added item that is processed, or newly adding item
	 */
	private OrderedItem processItemBeforeAdding(OrderedItem item) {
		for (OrderedItem addedItem : items) {
			if (addedItem.equalsLogically(item)) {

				if (addedItem.getNewMass() != item.getNewMass()) {
					int newMass = addedItem.getNewMass() * addedItem.getCount()
							+ item.getNewMass() * item.getCount();
					addedItem.setNewMass(newMass);
					addedItem.setCount(1);
				} else {
					addedItem.setCount(addedItem.getCount() + item.getCount());
				}

				return addedItem;
			}
		}

		// Adding item to order and returning it
		item.getOrder().getItems().add(item);
		return item;
	}

	/**
	 * Getting total sum of ordered items including count of items
	 * 
	 * @return total sum of the ordered items
	 */
	public double getTotalSum() {
		double rez = 0;
		for (OrderedItem item : items) {
			rez += item.getOrderedPriceBillAndCoins();
		}
		return rez;
	}

	/**
	 * @return the items
	 */
	public SortedSet<OrderedItem> getItems() {
		return items;
	}

	/**
	 * Deleting item from the model and DB
	 * 
	 * @param item
	 *            item to delete
	 */
	public void deleteItem(OrderedItem item) {
		try {
			logItemDeletion(item);
			
			service.delEntity(item);
			
			item.getOrder().getItems().remove(item);
			EventBus.publish(new OrderChangedEvent(this, item.getOrder()));

			items.remove(item);
			fireTableDataChanged();
		} catch (Exception e) {
			log.error("Failed to delete item: " + item, e);
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"Не получилось удалить элемент :(",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
}
