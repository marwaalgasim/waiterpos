package ua.cn.yet.waiter.ui.table.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import ua.cn.yet.common.ui.popup.PopupFactory;
import ua.cn.yet.common.ui.popup.PopupListener;
import ua.cn.yet.waiter.model.ItemType;

public class ColumnLiquidEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;

	private JLabel editComponent;

	private ItemType newValue;

	public ColumnLiquidEditor() {
		editComponent = new JLabel();
		editComponent.setHorizontalAlignment(SwingConstants.CENTER);
		ItemTypeAction[] actions = new ItemTypeAction[ItemType.values().length];
		for (int i = 0; i < actions.length; i++) {
			actions[i] = new ItemTypeAction(ItemType.values()[i]);
		}
		
		editComponent.addMouseListener(new PopupListener(PopupFactory
				.getGeneralPopup(actions)));
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		ItemType itemType = (ItemType) value;
		editComponent.setText(itemType.getName());
		return editComponent;
	}

	@Override
	public Object getCellEditorValue() {
		return newValue;
	}
	
	private class ItemTypeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private ItemType type;

		public ItemTypeAction(ItemType type) {
			super(type.getName());
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			newValue = type;
			editComponent.setText(type.getName());
			fireEditingStopped(); // Make the renderer reappear.
		}
	}
}
