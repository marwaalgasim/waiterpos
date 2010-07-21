package ua.cn.yet.waiter.ui.table.models;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.service.ItemService;
import ua.cn.yet.waiter.util.Utils;
import ua.cn.yet.waiter.util.WaiterInstance;

public class TableModelItemEdit extends AbstractTableModel {

	public static final int COLUMN_DEL = 6;

	public static final int COLUMN_PRICE = 5;

	public static final int COLUMN_MASS = 4;

	public static final int COLUMN_LIQUID = 3;

	public static final int COLUMN_NAME = 2;

	public static final int COLUMN_PIC = 1;

	public static final int COLUMN_NUM = 0;

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(TableModelItemEdit.class);

	/**
	 * Names of all columns
	 */
	private final String[] columnNames = { "№", "Картинка", "Наименование",
			"Напиток", "Выход", "Сумма", "" };

	private Category category;

	private Component parent;

	public TableModelItemEdit(Category category, Component parent) {
		super();
		this.category = category;
		this.parent = parent;
	}

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
		return category.getItems().size();
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

			if (COLUMN_NUM == columnIndex) {
				return rowIndex + 1;
			}

			Item item = getItemByRowIndex(rowIndex);

			switch (columnIndex) {
			case COLUMN_PIC:
				return item;
			case COLUMN_NAME:
				return item.getName();
			case COLUMN_LIQUID:
				return item.getItemType();
			case COLUMN_MASS:
				return item.getMass();
			case COLUMN_PRICE:
				return item.getPriceBillsAndCoins();
			case COLUMN_DEL:
				return item;
			default:
				return null;
			}
		}
	}

	/**
	 * Getting item by index
	 * 
	 * @param rowIndex
	 *            Correct row index
	 * @return found Item
	 */
	private Item getItemByRowIndex(int rowIndex) {
		Item item;
		Iterator<Item> iter = category.getItems().iterator();
		while (0 != (rowIndex--)) {
			iter.next();
		}
		item = iter.next();
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

		Item item = getItemByRowIndex(rowIndex);

		switch (columnIndex) {
		case COLUMN_PIC:
			String pic = aValue.toString();
			if (StringUtils.isNotEmpty(pic)) {
				Utils.deleteImageFile(item);
				pic = Utils.copyToPicturesDir(pic);
			}
			item.setPicture(pic);
			break;
		case COLUMN_NAME:
			String newName = aValue.toString();
			if (!item.getName().equalsIgnoreCase(newName)) {
				if (category.itemExists(newName)) {
					JOptionPane.showMessageDialog(parent, "Элемент с именем '"
							+ newName + "' уже существует в категории "
							+ category.getName(), "Немогу добавить :(",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (StringUtils.isEmpty(newName)) {
					return;
				}
				item.setName(aValue.toString());
			} else {
				return;
			}
			break;
		case COLUMN_LIQUID:
			ItemType newType = (ItemType) aValue;
			if (!item.getItemType().equals(newType)) {
				item.setItemType(newType);
			} else {
				return;
			}
			break;
		case COLUMN_MASS:
			try {
				item.setMass(Integer.valueOf(aValue.toString()));
			} catch (Exception e) {
				return;
			}
			break;
		case COLUMN_PRICE:
			try {
				item.setPriceBillsAndCoins(Double.valueOf(aValue.toString()));
			} catch (Exception e) {
				return;
			}
			break;
		}

		ItemService itemService = WaiterInstance
				.forId(WaiterInstance.ITEM_SERVICE);
		try {
			itemService.save(item);
			fireTableDataChanged();
		} catch (Exception e) {
			log.error("Failed to update item: " + item, e);
			JOptionPane.showMessageDialog(parent, e.getLocalizedMessage(),
					"Не получилось обновить элемент :(",
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
		switch (columnIndex) {
		case COLUMN_PIC:
		case COLUMN_NAME:
		case COLUMN_LIQUID:
		case COLUMN_MASS:
		case COLUMN_PRICE:
		case COLUMN_DEL:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
}
