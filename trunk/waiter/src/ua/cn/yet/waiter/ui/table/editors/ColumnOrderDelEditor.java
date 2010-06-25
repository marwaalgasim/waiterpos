package ua.cn.yet.waiter.ui.table.editors;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.ui.table.models.TableModelOrders;

public class ColumnOrderDelEditor extends AbstractColumnDelEditor {

	private static final long serialVersionUID = 1L;
	private TableModelOrders tableModel;
	
	public ColumnOrderDelEditor(TableModelOrders tableModel) {
		this.tableModel = tableModel;
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.ui.table.editors.AbstractColumnDelEditor#getDelObjectName(java.lang.Object)
	 */
	@Override
	protected String getDelObjectName(Object delObject) {
		if (null == delObject) {
			return null;
		} else {
			return ((Order)delObject).getTitle();
		}
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.ui.table.editors.AbstractColumnDelEditor#performObjectDelete(java.lang.Object)
	 */
	@Override
	protected void performObjectDelete(Object delObject) throws Exception {
		tableModel.deleteOrder((Order) delObject);
	}

}
