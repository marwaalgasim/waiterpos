package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ua.cn.yet.waiter.model.AbstractItem;

public class ColumnMassRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	public ColumnMassRenderer() {
		super();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
	 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component result = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		Integer mass = (Integer)value;
		if (mass.equals(AbstractItem.SINGLE_ITEM_NO_MASS)) {
			((JLabel) result).setText("");
		}

		return result;
	}
}
