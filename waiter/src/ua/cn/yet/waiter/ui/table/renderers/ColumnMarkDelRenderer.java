package ua.cn.yet.waiter.ui.table.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.ui.table.models.TableModelOrders;

public class ColumnMarkDelRenderer extends BooleanColumnRenderer {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.ui.table.renderers.BooleanColumnRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component rez = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		int modelRow = table.convertRowIndexToModel(row);
		TableModelOrders tableModel = (TableModelOrders) table.getModel();
		Order order = tableModel.getOrderAt(modelRow);
		if (order.isForDeletion()) {
			((JLabel)rez).setToolTipText("<html><b>Причина для отмены/удаления:</b><br/>" + 
					order.getForDeletionReason()+"</html>");
		} else {
			((JLabel)rez).setToolTipText(null);
		}
		
		return rez;
	}
	
	

}
