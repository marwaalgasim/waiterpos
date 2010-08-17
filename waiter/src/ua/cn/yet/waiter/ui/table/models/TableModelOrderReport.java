package ua.cn.yet.waiter.ui.table.models;

import javax.swing.table.AbstractTableModel;

import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.model.OrderReport;

/**
 * Table model for order report
 * 
 * @author Yuriy Tkach
 */
public class TableModelOrderReport extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int COLUMN_TOTAL = 5;
	public static final int COLUMN_ALCOHOL = 4;
	public static final int COLUMN_BAR = 3;
	public static final int COLUMN_FOOD = 2;
	public static final int COLUMN_COUNT = 1;
	public static final int COLUMN_HEAD = 0;

	/**
	 * Names of all columns
	 */
	private final String[] columnNames = { "", "Заказов", "Кухня", "Бар",
			"Алкоголь", "Всего" };

	private OrderReport report;

	public TableModelOrderReport() {
		super();
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
		return 4;
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
			if (0 == rowIndex) {
				switch (columnIndex) {
				case COLUMN_HEAD:
					return "Всех";
				case COLUMN_COUNT:
					return report.getTotalOrdersStr();
				case COLUMN_FOOD:
					return report.getTotalsForTypeStr(ItemType.FOOD);
				case COLUMN_BAR:
					return report.getTotalsForBarStr();
				case COLUMN_ALCOHOL:
					return report.getTotalsForTypeStr(ItemType.ALCOHOL);
				case COLUMN_TOTAL:
					return report.getTotalSumStr();
				}
			} else if (1 == rowIndex) {
				switch (columnIndex) {
				case COLUMN_HEAD:
					return "Без удал./отм.";
				case COLUMN_COUNT:
					return report.getTotalOrders() - report.getTotalDelOrders();
				case COLUMN_FOOD:
					return String.format("%.2f грн.",
							report.getTotalsForType(ItemType.FOOD) - report.getTotalsForTypeDelOrders(ItemType.FOOD));
				case COLUMN_BAR:
					return String.format("%.2f грн.",
							report.getTotalsForBar() - report.getTotalsForBarDelOrders());
				case COLUMN_ALCOHOL:
					return String.format("%.2f грн.",
							report.getTotalsForType(ItemType.ALCOHOL) - report.getTotalsForTypeDelOrders(ItemType.ALCOHOL));
				case COLUMN_TOTAL:
					return String.format("%.2f грн.",
							report.getTotalSum() - report.getTotalSumDelOrders());
				}
			}else if (2 == rowIndex) {
				switch (columnIndex) {
				case COLUMN_HEAD:
					return "Открытых";
				case COLUMN_COUNT:
					return report.getTotalOpenOrdersStr();
				case COLUMN_FOOD:
					if (report.getTotalOpenOrders() > 0) {
						return report.getTotalsForTypeOpenOrdersStr(ItemType.FOOD);
					}
				case COLUMN_BAR:
					if (report.getTotalOpenOrders() > 0) {
						return report.getTotalsForBarOpenOrdersStr();
					}
				case COLUMN_ALCOHOL:
					if (report.getTotalOpenOrders() > 0) {
						return report.getTotalsForTypeOpenOrdersStr(ItemType.ALCOHOL);
					}
				case COLUMN_TOTAL:
					if (report.getTotalOpenOrders() > 0) {
						return report.getTotalSumOpenOrdersStr();
					}
				}
			} else if (3 == rowIndex) {
				switch (columnIndex) {
				case COLUMN_HEAD:
					return "Удаленных/отмененных";
				case COLUMN_COUNT:
					return report.getTotalDelOrdersStr();
				case COLUMN_FOOD:
					if (report.getTotalDelOrders() > 0) {
						return report.getTotalsForTypeDelOrdersStr(ItemType.FOOD);
					}
				case COLUMN_BAR:
					if (report.getTotalDelOrders() > 0) {
						return report.getTotalsForBarDelOrdersStr();
					}
				case COLUMN_ALCOHOL:
					if (report.getTotalDelOrders() > 0) {
						return report.getTotalsForTypeDelOrdersStr(ItemType.ALCOHOL);
					}
				case COLUMN_TOTAL:
					if (report.getTotalDelOrders() > 0) {
						return report.getTotalSumDelOrdersStr();
					}
				}
			}
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
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
		return false;
	}

	/**
	 * @return the report
	 */
	public OrderReport getReport() {
		return report;
	}

	/**
	 * @param report
	 *            the report to set
	 */
	public void setReport(OrderReport report) {
		this.report = report;
		fireTableDataChanged();
	}

}
