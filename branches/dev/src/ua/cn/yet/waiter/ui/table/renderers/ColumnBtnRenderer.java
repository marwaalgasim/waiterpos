package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;

public class ColumnBtnRenderer extends OrderColumnRenderer{

	private static final long serialVersionUID = 1L;

	private JButton button;
	
	public ColumnBtnRenderer(JButton button) {
		super();
		this.button = button;
	}
	
	
	public ColumnBtnRenderer() {
		button = new JButton();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		return button;
	}

	/**
	 * @return the button
	 */
	public JButton getButton() {
		return button;
	}

	/**
	 * @param button the button to set
	 */
	public void setButton(JButton button) {
		this.button = button;
	}
	
	
}
