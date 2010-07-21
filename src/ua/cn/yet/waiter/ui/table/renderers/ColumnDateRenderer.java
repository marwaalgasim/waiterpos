package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColumnDateRenderer extends DefaultTableCellRenderer {

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

		if (value != null) {
			Calendar cal = (Calendar)value;
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);

			((JLabel) result).setText(formatter.format(cal.getTime()));
		} else {
			((JLabel) result).setText("");
		}
		
		return result;
	}
}
