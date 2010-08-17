package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColumnPriceRenderer extends OrderColumnRenderer {

	private static final long serialVersionUID = 1L;
	private boolean boldOutput;
	
	public ColumnPriceRenderer(boolean boldOutput) {
		super();
		this.boldOutput = boldOutput;
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

		int fontProps = boldOutput ? Font.BOLD : Font.PLAIN;
		result.setFont(new Font("", fontProps, 12));

		((JLabel) result).setText(String.format("%.2f", value));

		return result;
	}
}
