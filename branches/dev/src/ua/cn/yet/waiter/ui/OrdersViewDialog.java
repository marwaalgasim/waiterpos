package ua.cn.yet.waiter.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Dialog to view orders of the waiter
 * 
 * @author Yuriy Tkach
 */
class OrdersViewDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private OrdersViewPanel ordersPanel;

	private PrintingService printService;

	public OrdersViewDialog(Window parent, User waiter) {
		super(parent, "Заказы " + waiter.getFullName(),
				ModalityType.APPLICATION_MODAL);

		printService = WaiterInstance.forId(WaiterInstance.PRINTING_SERVICE);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(700, 500));

		this.setLayout(new MigLayout("insets 5", "[fill,grow]", "[]"));

		createToolbar();

		ordersPanel = new OrdersViewPanel(waiter, true, true);
		this.add(ordersPanel, "wrap");

		createDialogButtons();

		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Creating toolbar for actions on orders
	 */
	private void createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setAutoscrolls(true);
		toolbar.setFloatable(false);

		JButton btnReceiptPrint = new JButton();
		btnReceiptPrint.setIcon(AbstractForm
				.createImageIcon("receipt_invoice.png"));
		btnReceiptPrint.setText("Печать чека");
		toolbar.add(btnReceiptPrint);
		btnReceiptPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Collection<Order> orders = ordersPanel.getSelectedOrders();
				for (Order order : orders) {
					printService.printReceipt(order);
				}
			}
		});

		toolbar.addSeparator();

		JButton btnChangeCalc = new JButton();
		btnChangeCalc.setIcon(AbstractForm.createImageIcon("coins16.png"));
		btnChangeCalc.setText("Расчет сдачи");
		toolbar.add(btnChangeCalc);
		btnChangeCalc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Collection<Order> orders = ordersPanel.getSelectedOrders();
				if (orders.size() > 0) {
					new ChangeCalcDialog(OrdersViewDialog.this, orders
							.iterator().next());
				}
			}
		});

		toolbar.addSeparator();
		
		JButton btnMarkForDel = new JButton();
		btnMarkForDel.setIcon(AbstractForm
				.createImageIcon("remove.png"));
		btnMarkForDel.setText("Удалить");
		toolbar.add(btnMarkForDel);
		btnMarkForDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ordersPanel.markSelectedForDeletion();
			}
		});
		
		toolbar.addSeparator();

		JLabel lbTip = new JLabel("Выберите заказ из таблицы ниже...");
		lbTip.setEnabled(false);
		toolbar.add(lbTip);

		this.add(toolbar, "wrap");
	}

	/**
	 * Creating panel for dialog buttons
	 */
	private void createDialogButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0", "[center,grow]", ""));

		JButton btnClose = new JButton("Закрыть");
		btnClose.setIcon(AbstractForm.createImageIcon("no.png"));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OrdersViewDialog.this.dispose();
			}
		});
		panel.add(btnClose);

		this.add(panel, "wrap");
	}

}