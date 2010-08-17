package ua.cn.yet.waiter.ui.table.editors;

import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ColumnBtnEditor extends AbstractCellEditor
		implements TableCellEditor{
	
	private static final long serialVersionUID = 1L;

	private JButton button;

	public ColumnBtnEditor(AbstractAction action){
		button = new JButton(action);
		button.setBorderPainted(false);
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		return button;
	}


	@Override
	public Object getCellEditorValue() {
		return null;
	}
	

}
