package ua.cn.yet.waiter.ui.table.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import ua.cn.yet.common.ui.popup.PopupFactory;
import ua.cn.yet.common.ui.popup.PopupListener;
import ua.cn.yet.waiter.ui.AbstractForm;

public class BooleanColumnEditor extends AbstractCellEditor implements
		TableCellEditor {
	
	private static final long serialVersionUID = 1L;

	private JLabel editComponent;

	private Boolean newValue;

	public BooleanColumnEditor() {
		editComponent = new JLabel();
		editComponent.setHorizontalAlignment(SwingConstants.CENTER);
		editComponent.setText(null);
		editComponent.addMouseListener(new PopupListener(PopupFactory
				.getGeneralPopup(new YesAction(),
						null, new NoAction())));
		editComponent.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					if (newValue) {
						newValue = false;
					} else {
						newValue = true;
					}
					fireEditingStopped();
				}
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		newValue = (Boolean) value;
		
		if (newValue) {
			editComponent.setIcon(AbstractForm.createImageIcon("check.png"));
		} else {
			editComponent.setIcon(null);
		}

		return editComponent;
	}

	@Override
	public Object getCellEditorValue() {
		return newValue;
	}

	private class YesAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public YesAction() {
			super("Да");
		}

		public void actionPerformed(ActionEvent e) {
			newValue = true;
			editComponent.setIcon(AbstractForm.createImageIcon("check.png"));
			fireEditingStopped(); // Make the renderer reappear.
		}
	}

	private class NoAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public NoAction() {
			super("Нет");
		}

		public void actionPerformed(ActionEvent e) {
			newValue = false;
			editComponent.setIcon(null);
			fireEditingStopped(); // Make the renderer reappear.
		}
	}

}
