package ua.cn.yet.waiter.ui.table.editors;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.service.CategoryService;
import ua.cn.yet.waiter.service.ItemService;
import ua.cn.yet.waiter.ui.edit.ItemsEditPanel;
import ua.cn.yet.waiter.ui.table.models.TableModelItemEdit;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Editor for delete column in item's table
 * 
 * @author Yuriy Tkach
 */
public class ColumnItemDelEditor extends AbstractColumnDelEditor {

	private static final long serialVersionUID = 1L;

	private Category category;

	private TableModelItemEdit tableModel;

	private ItemsEditPanel itemsEditPanel;

	public ColumnItemDelEditor(Category category,
			TableModelItemEdit tableModel, ItemsEditPanel itemsEditPanel) {
		super();
		this.category = category;
		this.tableModel = tableModel;
		this.itemsEditPanel = itemsEditPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.table.editors.AbstractColumnDelEditor#getDelObjectName
	 * (java.lang.Object)
	 */
	@Override
	protected String getDelObjectName(Object delObject) {
		if (null == delObject) {
			return null;
		} else {
			return ((Item) delObject).getName();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.table.editors.AbstractColumnDelEditor#performObjectDelete
	 * (java.lang.Object)
	 */
	@Override
	protected void performObjectDelete(Object delObject) throws Exception {
		ItemService itemService = WaiterInstance
				.forId(WaiterInstance.ITEM_SERVICE);
		CategoryService catService = WaiterInstance
				.forId(WaiterInstance.CATEGORY_SERVICE);

		category.getItems().remove(delObject);

		catService.save(category);

		itemService.delEntity((Item) delObject);

		tableModel.setCategory(category);
		tableModel.fireTableDataChanged();

		itemsEditPanel.updateTotalItemCount();
	}
}
