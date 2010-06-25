package ua.cn.yet.waiter.ui.table.editors;

import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.ui.table.models.TableModelUsers;

/**
 * Editor for delete column in users table
 * 
 * @author Yuriy Tkach
 */
public class ColumnUserDelEditor extends AbstractColumnDelEditor {

	private static final long serialVersionUID = 1L;
	private TableModelUsers tableModel;
	
	public ColumnUserDelEditor(TableModelUsers tableModel) {
		super();
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
			return ((User)delObject).getUsername();
		}
	}

	@Override
	protected void performObjectDelete(Object delObject) throws Exception {
		tableModel.deleteUser((User)delObject);
	}

}
