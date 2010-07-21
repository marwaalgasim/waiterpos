package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import ua.cn.yet.waiter.model.ItemType;

public class ColumnLiquidRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

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

		((JLabel) result).setHorizontalAlignment(SwingConstants.CENTER);
		
		ItemType itemType = (ItemType) value;
		((JLabel) result).setText(itemType.getName());
		
		return result;
	}

}
