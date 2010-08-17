package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.ui.table.models.TableModelOrders;

public class OrderColumnRenderer extends DefaultTableCellRenderer{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component rez = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		
		checkOrderState(table, row);
		
		return rez;
	}
	
	protected Order getOrder(JTable table, int row) {
		int modelRow = table.convertRowIndexToModel(row);
		
		if (table.getModel() instanceof TableModelOrders) {	
			TableModelOrders tableModel = (TableModelOrders) table.getModel();
			Order order = tableModel.getOrderAt(modelRow);
			
			return order;
		} else {
			return null;
		}
	}
	
	protected void checkOrderState(JTable table, int row) {
		Order order = getOrder(table, row);
		
		if (order == null) {
			return;
		}
		
		if (order.isCanceled() || order.isForDeletion()) {
			setBackground(new Color(255,187,186));
		}
	}
	
	
}
