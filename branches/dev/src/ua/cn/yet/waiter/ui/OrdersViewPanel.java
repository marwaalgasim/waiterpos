package ua.cn.yet.waiter.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.ui.table.editors.BooleanColumnEditor;
import ua.cn.yet.waiter.ui.table.editors.ColumnOrderDelEditor;
import ua.cn.yet.waiter.ui.table.models.TableModelOrders;
import ua.cn.yet.waiter.ui.table.renderers.ColumnChangedRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnDateRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnDelRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnMarkDelRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnPriceRenderer;
import ua.cn.yet.waiter.util.Utils;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Panel, which displays orders. Supports filtering of orders
 * 
 * @author Yuriy Tkach
 */
public class OrdersViewPanel extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private TableModelOrders tableModelOrders;

	private JTable tableOrders;

	private boolean sortByClosed = false;

	private User waiter;

	public OrdersViewPanel(User waiter, boolean sortByClosed, boolean loadOrdersAfterCreation) {
		this.waiter = waiter;
		this.sortByClosed = sortByClosed;
		createOrdersTable(loadOrdersAfterCreation);
	}

	private void createOrdersTable(boolean loadOrdersAfterCreation) {
				
		tableModelOrders = new TableModelOrders(SwingUtilities
				.windowForComponent(this), waiter, loadOrdersAfterCreation);
		tableOrders = new JTable(tableModelOrders);
		this.setViewportView(tableOrders);

		configureColumnsInOrdersTable(tableOrders);

		tableOrders
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableRowSorter<TableModelOrders> sorter = new TableRowSorter<TableModelOrders>(
				tableModelOrders);
		List<SortKey> listSortKeys = new LinkedList<SortKey>();

		SortKey sortKeyCreated = new SortKey(TableModelOrders.COLUMN_CREATED,
				SortOrder.DESCENDING);
		SortKey sortKeyClosed = new SortKey(TableModelOrders.COLUMN_CLOSED,
				SortOrder.DESCENDING);

		listSortKeys.add(sortKeyCreated);
		if (sortByClosed) {
			listSortKeys.add(0, sortKeyClosed);
		} else {
			listSortKeys.add(sortKeyClosed);
		}

		sorter.setSortKeys(listSortKeys);
		sorter.setSortable(TableModelOrders.COLUMN_ID, false);
		if (null == waiter) {
			sorter.setSortable(TableModelOrders.COLUMN_DEL, false);
		}
		tableOrders.setRowSorter(sorter);

		tableOrders.addMouseListener(Utils
				.getTableRightClickRowSelectListener(tableOrders));

		tableOrders.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {	
					if(tableOrders.getSelectedColumn()==TableModelOrders.COLUMN_CHANGED &&
						tableModelOrders.getOrderAt(tableOrders.convertRowIndexToModel(tableOrders.getSelectedRow())).isChanged()){
						
						viewChangesLog();
					}
					else{	
						viewOrderItems();
					}
				}
			}
		});

		tableOrders.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					e.consume();
					viewOrderItems();
				}
			}
		});
		
		if (tableModelOrders.getRowCount() > 0) {
			tableOrders.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	/**
	 * @param table
	 *            table to configure columns in
	 */
	private void configureColumnsInOrdersTable(JTable table) {
		TableColumn col = table.getColumnModel().getColumn(
				TableModelOrders.COLUMN_ID);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(80);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelOrders.COLUMN_TABLE);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.LEADING);
		col.setPreferredWidth(80);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelOrders.COLUMN_CREATED);
		render = new ColumnDateRenderer();
		render.setHorizontalAlignment(SwingConstants.LEADING);
		col.setPreferredWidth(100);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelOrders.COLUMN_CLOSED);
		render = new ColumnDateRenderer();
		render.setHorizontalAlignment(SwingConstants.LEADING);
		col.setPreferredWidth(100);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelOrders.COLUMN_DISCOUNT);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(80);
		col.setCellRenderer(render);
		
		col = table.getColumnModel().getColumn(TableModelOrders.COLUMN_SUM);
		render = new ColumnPriceRenderer(true);
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(100);
		col.setCellRenderer(render);

		col = table.getColumnModel()
				.getColumn(TableModelOrders.COLUMN_MARK_DEL);
		render = new ColumnMarkDelRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(80);
		col.setCellRenderer(render);
		if (waiter != null) {
			col.setCellEditor(new BooleanColumnEditor());
		}
		
		col = table.getColumnModel()
				.getColumn(TableModelOrders.COLUMN_CANCELLED);
		render = new ColumnMarkDelRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(80);
		col.setCellRenderer(render);
		if (waiter != null) {
			col.setCellEditor(new BooleanColumnEditor());
		}
		
		col = table.getColumnModel()
				.getColumn(TableModelOrders.COLUMN_CHANGED);
		render = new ColumnChangedRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(80);
		col.setCellRenderer(render);
		if (waiter != null) {
			col.setCellEditor(new BooleanColumnEditor());
		}

		if (null == waiter) {
			col = table.getColumnModel().getColumn(TableModelOrders.COLUMN_DEL);
			render = new ColumnDelRenderer();
			render.setHorizontalAlignment(SwingConstants.CENTER);
			col.setMaxWidth(180);
			col.setMinWidth(100);
			col.setCellRenderer(render);
			col.setCellEditor(new ColumnOrderDelEditor(tableModelOrders));
		}

	}

	/**
	 * Display dialog to view items of the order
	 */
	protected void viewOrderItems() {
		int row = tableOrders.getSelectedRow();
		if (row > -1) {
			int modelRow = tableOrders.convertRowIndexToModel(row);
			Order order = tableModelOrders.getOrderAt(modelRow);
			if (order != null) {
				new ItemsViewDialog(SwingUtilities.getWindowAncestor(this),
						order);
			}
		}
	}
	
	/**
	 * Display dialog to view items of the order
	 */
	protected void viewChangesLog() {
		int row = tableOrders.getSelectedRow();
		if (row > -1) {
			int modelRow = tableOrders.convertRowIndexToModel(row);
			Order order = tableModelOrders.getOrderAt(modelRow);
			if (order != null) {
				new ChangesLogViewDialog(SwingUtilities.getWindowAncestor(this),
						order);
			}
		}
	}

	/**
	 * Dialog to view items of the order
	 * 
	 * @author Yuriy Tkach
	 */
	private class ItemsViewDialog extends JDialog {

		private static final long serialVersionUID = 1L;

		public ItemsViewDialog(Window parent, final Order order) {
			super(parent, "Заказ " + order.getTitle(),
					ModalityType.APPLICATION_MODAL);

			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setPreferredSize(new Dimension(400, 500));
			setLayout(new MigLayout("insets 5",
					"[grow,center][grow,center][grow,center]", "[grow, fill][]"));

			this.add(new ReceiptTables(order, false), "span, wrap");

			final PrintingService printService = WaiterInstance
					.forId(WaiterInstance.PRINTING_SERVICE);

			JButton btnChangeCalc = new JButton();
			btnChangeCalc.setIcon(AbstractForm.createImageIcon("coins16.png"));
			btnChangeCalc.setText("Расчет сдачи");
			btnChangeCalc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new ChangeCalcDialog(ItemsViewDialog.this, order);
				}
			});
			this.add(btnChangeCalc, "w 80::");

			JButton btnReceipt = new JButton("Чек");
			btnReceipt.setEnabled(order.isClosed());
			btnReceipt.setIcon(AbstractForm
					.createImageIcon("receipt_invoice.png"));
			btnReceipt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					printService.printReceipt(order);
				}
			});
			this.add(btnReceipt, "w 80::");

			JButton btnClose = new JButton("Закрыть");
			btnClose.setIcon(AbstractForm.createImageIcon("no.png"));
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ItemsViewDialog.this.dispose();
				}
			});
			this.add(btnClose, "w 80::");

			this.pack();
			this.setLocationRelativeTo(parent);
			this.setVisible(true);
		}

	}
	

	/**
	 * @return the sortByClosed
	 */
	public boolean isSortByClosed() {
		return sortByClosed;
	}

	/**
	 * @param sortByClosed
	 *            the sortByClosed to set
	 */
	public void setSortByClosed(boolean sortByClosed) {
		this.sortByClosed = sortByClosed;
	}

	/**
	 * @return the waiter
	 */
	public User getWaiter() {
		return waiter;
	}

	/**
	 * @param waiter
	 *            the waiter to set
	 */
	public void setWaiter(User waiter) {
		this.waiter = waiter;
	}

	/**
	 * @return table orders model
	 */
	public TableModelOrders getOrdersModel() {
		return tableModelOrders;
	}

	/**
	 * Called to filter orders
	 */
	public void filterOrders(Calendar from, Calendar to, User user,
			Boolean closed) {
		tableModelOrders.filterOrders(from, to, user, closed);
	}

	/**
	 * Getting orders that user selected in the table
	 * 
	 * @return collection of selected orders
	 */
	public Collection<Order> getSelectedOrders() {
		Collection<Order> rez = new LinkedList<Order>();

		if (tableOrders.getSelectedRowCount() < 1) {
			return rez;
		}

		int[] rows = tableOrders.getSelectedRows();
		for (int i : rows) {
			int modelRow = tableOrders.convertRowIndexToModel(i);
			Order order = tableModelOrders.getOrderAt(modelRow);
			rez.add(order);
		}

		return rez;
	}
	
	/**
	 * Marking selected order for deletion
	 */
	public void markSelectedForDeletion() {
		int[] rows = tableOrders.getSelectedRows();
		for (int i : rows) {
			int modelRow = tableOrders.convertRowIndexToModel(i);
			tableModelOrders.setValueAt(true, modelRow, TableModelOrders.COLUMN_MARK_DEL);
		}
	}
	
	/**
	 * Dialog to view changes log
	 * 
	 * @author n0weak
	 */
	private class ChangesLogViewDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		
		private PrintingService printService = WaiterInstance.forId(WaiterInstance.PRINTING_SERVICE);

		public ChangesLogViewDialog(Window parent, final Order order) {
			super(parent, "Заказ " + order.getTitle(),
					ModalityType.APPLICATION_MODAL);

			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			JLabel label=new JLabel("<html>"+order.getLoggedChangesHtml()+"</html>");
			
			JScrollPane scrollPane=new JScrollPane(label);
			this.add(scrollPane,BorderLayout.CENTER);
			scrollPane.setBorder(new TitledBorder("Изменения заказа"));
			
			JPanel buttonsPanel=new JPanel();
			buttonsPanel.setLayout(new FlowLayout());
			
			JButton btnPrint = new JButton("Печать");
			btnPrint.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					printService.printOrderChanges(order);
				}
			});
			
			buttonsPanel.add(btnPrint);
			btnPrint.setIcon(AbstractForm.createImageIcon("fileprint.png"));

			JButton btnClose = new JButton("Закрыть");
			btnClose.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					ChangesLogViewDialog.this.dispose();
				}
			});
			
			buttonsPanel.add(btnClose);
			btnClose.setIcon(AbstractForm.createImageIcon("no.png"));

			this.add(buttonsPanel,BorderLayout.SOUTH);
			
			Dimension dimension=label.getPreferredSize();
			dimension.setSize(dimension.getWidth()+40, 
					dimension.getHeight()+buttonsPanel.getPreferredSize().getHeight()+80);
			
			if(dimension.getHeight()>700){
				dimension.setSize(dimension.getWidth(), 700);
			}
			
			this.setPreferredSize(dimension);

			
			this.pack();
			this.setLocationRelativeTo(parent);
			this.setVisible(true);
		}

	}
}
