package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import ua.cn.yet.waiter.ui.AbstractForm;

public class BooleanColumnRenderer extends OrderColumnRenderer {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component result = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		((JLabel) result).setHorizontalAlignment(SwingConstants.CENTER);
		
		((JLabel) result).setText(null);
		((JLabel) result).setIcon(null);
		
		Boolean bValue = (Boolean) value;
		
		if (bValue == true) {
			((JLabel) result).setIcon(AbstractForm.createImageIcon("check.png"));
		}
		
		return result;
	}
	
	

}
