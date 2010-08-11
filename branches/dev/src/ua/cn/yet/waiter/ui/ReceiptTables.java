package ua.cn.yet.waiter.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import ua.cn.yet.common.ui.popup.PopupFactory;
import ua.cn.yet.common.ui.popup.PopupListener;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderedItem;
import ua.cn.yet.waiter.ui.events.OrderChangedEvent;
import ua.cn.yet.waiter.ui.table.editors.ColumnBtnEditor;
import ua.cn.yet.waiter.ui.table.models.TableModelReceipt;
import ua.cn.yet.waiter.ui.table.renderers.ColumnBtnRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnMassRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnPriceRenderer;
import ua.cn.yet.waiter.util.Utils;

public class ReceiptTables extends JPanel implements TableModelListener {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ReceiptTables.class);
	
	private TableModelReceipt tableModelFood;
	private TableModelReceipt tableModelSoftDrink;
	private TableModelReceipt tableModelAlcohol;
	private JLabel lbTotalSum;
	private Order order;
	private Icon editIcon;
	private Icon removeIcon;

	/** Specifies if editing of ordered items is allowed */
	private boolean allowEdit;

	public ReceiptTables(Order order, boolean allowEdit) {
		super(true);
		this.order = order;
		this.allowEdit = allowEdit;
		
		AnnotationProcessor.process(this);

		editIcon = AbstractForm.createImageIcon("edit.png");
		removeIcon = AbstractForm.createImageIcon("remove.png");
		
		setLayout(new MigLayout("insets 0", "[grow, fill]",
				"[grow, fill][grow,fill][grow,fill][fill]"));
		

		tableModelFood = new TableModelReceipt(allowEdit);
		createAndSetupTable(tableModelFood, "Блюда");

		tableModelSoftDrink = new TableModelReceipt(allowEdit);
		createAndSetupTable(tableModelSoftDrink, "Напитки/Бар");

		tableModelAlcohol = new TableModelReceipt(allowEdit);
		createAndSetupTable(tableModelAlcohol, "Алкоголь");

		createSummaryPanel();

		fillTableModels();
	}

	/**
	 * Filling table models with items from order
	 */
	private void fillTableModels() {
		for (OrderedItem item : order.getItems()) {
			if (item.isLiquid()) {
				if (item.isAlcohol()) {
					tableModelAlcohol.getItems().add(item);
				} else {
					tableModelSoftDrink.getItems().add(item);
				}
			} else {
				if (item.isBar()) {
					tableModelSoftDrink.getItems().add(item);
				} else {
					tableModelFood.getItems().add(item);
				}
			}
		}
		updateTotalSum();
	}

	/**
	 * Creating table for the supplied model and setting up it and the model. If
	 * {@link #allowEdit} is <code>true</code> then adding listeners to the
	 * table that will allow edit and delete ordered items.
	 * 
	 * @param tableModel
	 *            Model of the table to use
	 * @param title
	 *            Title of the table
	 * @return created table
	 */
	private JTable createAndSetupTable(TableModelReceipt tableModel,
			String title) {
		JTable table = new JTable(tableModel);
	
		table
				.addMouseListener(Utils
						.getTableRightClickRowSelectListener(table));
		

		if (allowEdit) {
			table.addMouseListener(new OrderItemDoubleClickListener(table));
			table.addMouseListener(new PopupListener(PopupFactory
					.getGeneralPopup(
							new EditOrderedItemAction(table), null,
							new RemoveOrderedItemAction(table))));
		}

		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(BorderFactory.createTitledBorder(title));
		scroll.setViewportView(table);

		configureColumnsInReceiptTable(table);

		tableModel.addTableModelListener(this);

		add(scroll, "wrap");

		return table;
	}

	/**
	 * Assigning renders to columns and configuring them
	 * 
	 * @param table
	 *            Table to configure columns
	 */
	private void configureColumnsInReceiptTable(JTable table) {
		TableColumn col = table.getColumnModel().getColumn(
				TableModelReceipt.COLUMN_NAME);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.LEADING);
		col.setCellRenderer(render);
		col.setPreferredWidth(170);

		col = table.getColumnModel().getColumn(
				TableModelReceipt.COLUMN_BASE_PRICE);
		render = new ColumnPriceRenderer(false);
		render.setHorizontalAlignment(SwingConstants.TRAILING);
		col.setPreferredWidth(55);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelReceipt.COLUMN_MASS);
		render = new ColumnMassRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setCellRenderer(render);
		col.setPreferredWidth(55);

		col = table.getColumnModel().getColumn(TableModelReceipt.COLUMN_COUNT);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setCellRenderer(render);
		col.setPreferredWidth(60);

		col = table.getColumnModel().getColumn(TableModelReceipt.COLUMN_PRICE);
		render = new ColumnPriceRenderer(true);
		render.setHorizontalAlignment(SwingConstants.TRAILING);
		col.setCellRenderer(render);
		
		col = table.getColumnModel().getColumn(TableModelReceipt.COLUMN_BTN_EDIT);
		JButton btn = new JButton(editIcon);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setBorderPainted(false);
		btn.setToolTipText("Изменить элемент");
		render = new ColumnBtnRenderer(btn);
		col.setCellRenderer(render);
		col.setCellEditor(new ColumnBtnEditor(new EditOrderedItemAction(table, "")));
		col.setPreferredWidth(20);
		
		col = table.getColumnModel().getColumn(TableModelReceipt.COLUMN_BTN_DEL);
		btn = new JButton(removeIcon);
		btn.setToolTipText("Удалить элемент");
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setBorderPainted(false);
		render = new ColumnBtnRenderer(btn);
		col.setCellRenderer(render);
		col.setCellEditor(new ColumnBtnEditor(new RemoveOrderedItemAction(table, "")));
		col.setPreferredWidth(20);
	}

	/**
	 * Creating panel for summary information
	 */
	private void createSummaryPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(""));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new MigLayout("", "[left,grow][right,grow]", "[]"));

		JLabel lbAll = new JLabel("Всего:");
		lbAll.setFont(new Font("", Font.BOLD, 14));
		panel.add(lbAll);

		lbTotalSum = new JLabel("0.0 грн.");
		lbTotalSum.setFont(new Font("", Font.BOLD + Font.ITALIC, 16));
		panel.add(lbTotalSum);

		this.add(panel);
	}

	/**
	 * Adding item to one of the tables. Displaying ordered item edit dialog if
	 * item is liquid that allows mass editing
	 * 
	 * @param item
	 *            Item to add
	 */
	public void addItem(Item item) {
		if (null == item) {
			return;
		}

		OrderedItem orderedItem = new OrderedItem(item, order, 1, item
				.getMass());

		int rez = OrderItemEditDialog.EDIT_OK_NO_CHANGES;

		if (item.isLiquid() && item.isMassEditableInOrder()) {
			rez = OrderItemEditDialog.editOrderItem(SwingUtilities
					.windowForComponent(this), "Добавляемs", orderedItem);
		}

		switch (rez) {
		case OrderItemEditDialog.EDIT_OK_CHANGES:
		case OrderItemEditDialog.EDIT_OK_NO_CHANGES:
			if (item.isLiquid()) {
				if (item.isAlcohol()) {
					tableModelAlcohol.addItem(orderedItem);
				} else {
					tableModelSoftDrink.addItem(orderedItem);
				}
			} else {
				if (item.isBar()) {
					tableModelSoftDrink.addItem(orderedItem);
				} else {
					tableModelFood.addItem(orderedItem);
				}
			}

			updateTotalSum();
			break;
		default:
			return;
		}
	}

	/**
	 * Updating total sum panel to reflect the changes
	 */
	private void updateTotalSum() {
		double totalSum = tableModelFood.getTotalSum();
		totalSum += tableModelSoftDrink.getTotalSum();
		totalSum += tableModelAlcohol.getTotalSum();

		lbTotalSum.setText(String.format("%.2f", totalSum) + " грн.");
	}
	
	/**
	 * Reassigning order after it was changed
	 * 
	 * @param e event object
	 */
	@EventSubscriber(eventClass=OrderChangedEvent.class)
	public void onOrderChangedEvent(OrderChangedEvent e) {
		if (null != e.getOrder()) {
			if (null != this.order) {
				if (this.order.equals(e.getOrder())) {
					this.order = e.getOrder();
					if (log.isTraceEnabled()) {
						log.trace("Updating reference to " + this.order);
					}
				}
			}
		} else {
			log.error("Order in the event is NULL");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.event.TableModelListener#tableChanged(javax.swing.event.
	 * TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		updateTotalSum();
	}

	private void editSelectedOrderedItem(JTable table) {
		int row = table.getSelectedRow();
		if (row > -1) {
			TableModelReceipt model = (TableModelReceipt) table
					.getModel();
			int modelRow = table.convertRowIndexToModel(row);

			OrderedItem item = model.getItemFromSet(modelRow);

			int rez = OrderItemEditDialog.editOrderItem(SwingUtilities
					.getWindowAncestor(ReceiptTables.this),
					"Изменяем элемент", item);
			
			if (OrderItemEditDialog.EDIT_OK_CHANGES == rez) {
				item.setUpdated(true);
				model.persistOrderItem(item);
			}
		}
	}

	/**
	 * Handling double clicks on table
	 * 
	 * @author Yuriy Tkach
	 */
	private class OrderItemDoubleClickListener extends MouseAdapter {

		private JTable table;

		public OrderItemDoubleClickListener(JTable table) {
			this.table = table;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if ((e.getClickCount() > 1)
					&& (e.getButton() == MouseEvent.BUTTON1)) {
				editSelectedOrderedItem(table);
			}
		}
	}
	
	/**
	 * Action for editing selected ordered item
	 * @author Yuriy Tkach
	 */
	private class EditOrderedItemAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private JTable table;
		
		public EditOrderedItemAction(JTable table) {
			super("Изменить",editIcon);
			this.table = table;
		}
		
		public EditOrderedItemAction(JTable table, String name) {
			super(name,editIcon);
			this.table = table;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			editSelectedOrderedItem(table);
		}
	}

	/**
	 * Action that is triggered on popup for deleting ordered item
	 * 
	 * @author Yuriy Tkach
	 */
	private class RemoveOrderedItemAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private JTable table;

		public RemoveOrderedItemAction(JTable table) {
			super("Удалить",removeIcon);
			this.table = table;
		}
		
		public RemoveOrderedItemAction(JTable table, String name) {
			super(name,removeIcon);
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TableModelReceipt model = (TableModelReceipt) table.getModel();

			int row = table.getSelectedRow();
			if (row > -1) {
				int modelRow = table.convertRowIndexToModel(row);

				OrderedItem item = model.getItemFromSet(modelRow);
				
				Object[] objs = {"Да","Нет","Редактировать"};
				
				int rez = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(ReceiptTables.this),
						"Удалить полностью из заказа: " + item.getName() + "?", "Точно?",
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE, null,
						objs, "Да");
				if (JOptionPane.YES_OPTION == rez) {
					model.deleteItem(item);
				} else if (JOptionPane.CANCEL_OPTION == rez) {
					editSelectedOrderedItem(table);
				}
			}
		}
	}

}
