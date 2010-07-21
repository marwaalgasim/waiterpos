package ua.cn.yet.waiter.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bushe.swing.event.EventBus;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.FormListener;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.ui.events.OrderCreatedEvent;
import ua.cn.yet.waiter.util.Config;
import ua.cn.yet.waiter.util.WaiterInstance;

public class WaiterForm extends AbstractForm implements OrderTabListener {

	private User user;
	private Component welcomePanel;
	private JTabbedPane ordersTabs;
	private OrderService orderService;

	private CategoriesAction categoriesAction;
	private Container contentPane;
	private boolean welcomeActive;

	private List<OrderTab> orderTabsComponents = new ArrayList<OrderTab>();

	public WaiterForm(String title, FormListener formListener, User user) {
		super(title, formListener);

		this.user = user;

		orderService = WaiterInstance.forId(WaiterInstance.ORDER_SERVICE);

		createAndShowGUI();
	}

	/**
	 * Creating all visual components
	 * 
	 * @param contentPane
	 *            Pane to add components to
	 */
	protected void createComponents(Container contentPane) {

		this.contentPane = contentPane;

		contentPane.setLayout(new MigLayout("insets 5", "[fill,grow]",
				"[][fill, grow]"));

		contentPane.add(createToolbar(), "wrap");

		welcomePanel = createWelcomePanel();
		ordersTabs = new JTabbedPane();
		ordersTabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = ordersTabs.getSelectedIndex();
				if (selectedIndex > -1) {
					OrderTab tab = orderTabsComponents.get(selectedIndex);
					categoriesAction.setEnabled(!tab.isCategoriesDisplay());
				} else {
					categoriesAction.setEnabled(false);
				}
			}
		});
		loadUserOrders();
		welcomeActive = (ordersTabs.getTabCount() != 0);
		updateContentPane();
	}

	/**
	 * Loading all user's open orders. Setting either welcome panel or ordertabs
	 * on the content pane.
	 */
	private void updateContentPane() {
		if (ordersTabs.getTabCount() == 0) {

			if (!welcomeActive) {
				contentPane.remove(ordersTabs);
				contentPane.add(welcomePanel);
				contentPane.validate();
				contentPane.repaint();
				categoriesAction.setEnabled(false);
				this.welcomeActive = true;
			}

		} else {

			if (welcomeActive) {
				contentPane.remove(welcomePanel);
				contentPane.add(ordersTabs);
				contentPane.validate();
				contentPane.repaint();
				this.welcomeActive = false;
			}
		}

	}

	/**
	 * Loading all user orders into tabs
	 */
	private void loadUserOrders() {
		try {
			Collection<Order> userOrders = orderService
					.getOpenUserOrders(this.user);
			for (Order order : userOrders) {
				if (!hasTab(order.getTitle())) {
					OrderTab orderTab = new OrderTab(order, this);
					orderTabsComponents.add(orderTab);

					StringBuilder sb = new StringBuilder();
					sb.append("<html><b>");
					sb.append(order.getTitle());
					sb.append("</b></html>");
					ordersTabs.addTab(sb.toString(), orderTab);
				}
			}
		} catch (Exception e) {
			log.error("Failed to get opened user orders for " + this.user);
			JOptionPane.showMessageDialog(this.frame,
					"Не могу получить все открытые заказы.\n"
							+ e.getLocalizedMessage()
							+ "\n\nОбратитесь к администратору.",
					"Что-то странное :(", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Checking if tab with provides name exists in ordersTabs
	 * 
	 * @param title
	 *            name of the tab
	 * @return true if tab with such name exists
	 */
	private boolean hasTab(String title) {
		for (int i = 0; i < ordersTabs.getTabCount(); i++) {
			if (ordersTabs.getTitleAt(i).indexOf(title) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return toolbar panel
	 */
	private Component createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setAutoscrolls(true);
		toolbar.setFloatable(false);

		JButton btnNewOrder = new JButton("Новый заказ");
		btnNewOrder.addActionListener(new NewOrderListener());
		btnNewOrder.setIcon(AbstractForm.createImageIcon("filenew.png"));
		toolbar.add(btnNewOrder);

		toolbar.addSeparator();

		categoriesAction = new CategoriesAction();
		JButton btnDisplayCategories = new JButton(categoriesAction);
		categoriesAction.setEnabled(false);
		toolbar.add(btnDisplayCategories);

		toolbar.addSeparator();

		JButton btnAllOrders = new JButton("Прошлые заказы");
		btnAllOrders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new OrdersViewDialog(WaiterForm.this.frame,
						WaiterForm.this.user);
			}
		});
		btnAllOrders.setIcon(AbstractForm.createImageIcon("history.png"));
		toolbar.add(btnAllOrders);
		
		JButton btnReportForDay = new JButton("Отчет за день");
		btnReportForDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ReportForDayDialog(WaiterForm.this.frame);
			}
		});
		btnReportForDay.setIcon(AbstractForm.createImageIcon("report.png"));
		toolbar.add(btnReportForDay);

		// toolbar.addSeparator();
		//
		// final JTextField textFilter = new JTextField();
		// textFilter.setPreferredSize(new Dimension(200, 22));
		// toolbar.add(textFilter);
		// textFilter.addKeyListener(new KeyAdapter() {
		//
		// public void keyReleased(KeyEvent e) {
		// String searchStr = textFilter.getText();
		//
		// switch (e.getKeyCode()) {
		// case KeyEvent.VK_BACK_SPACE:
		// case KeyEvent.VK_DELETE:
		// if (searchStr.isEmpty()) {
		// textFilter.setBackground(Color.WHITE);
		// return;
		// }
		// break;
		// }
		// }
		//
		// @Override
		// public void keyTyped(KeyEvent e) {
		// String searchStr = textFilter.getText();
		// switch (e.getKeyChar()) {
		// case 0x08:
		// case 0x7F:
		// break;
		// default:
		// searchStr += e.getKeyChar();
		// }
		//
		// if (searchStr.isEmpty()) {
		// return;
		// }
		//
		// // TODO: Send event to EDT for searching!
		//
		// System.out.println("Searching: " + searchStr);
		//
		// for (int i = 0; i < panelItems.getComponentCount(); i++) {
		// String caption = panelItems.getComponent(i).getName();
		//
		// if (caption.toLowerCase().startsWith(
		// searchStr.toLowerCase())) {
		// System.err.println("Found!");
		// final Component c = panelItems.getComponent(i);
		// c.setBackground(Color.GREEN);
		// c.repaint();
		//
		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// c.setBackground(null);
		// }
		// });
		//
		// textFilter.setBackground(Color.GREEN);
		// textFilter.repaint();
		//
		// return;
		// }
		// }
		//
		// textFilter.setBackground(Color.RED);
		// textFilter.repaint();
		// }
		//
		// });

		return toolbar;
	}

	/**
	 * Creating welcome panel
	 * 
	 * @return welcome panel for the waiter
	 */
	private Component createWelcomePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[grow,center]", "[grow,center]"));

		JButton btnNewOrder = new JButton("Новый заказ");
		btnNewOrder.addActionListener(new NewOrderListener());
		panel.add(btnNewOrder);
		btnNewOrder.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnNewOrder.setVerticalAlignment(SwingConstants.TOP);
		btnNewOrder.setHorizontalTextPosition(SwingConstants.CENTER);
		btnNewOrder.setIcon(AbstractForm.createImageIcon("no_pic.png"));

		JButton btnExit = new JButton("Выход");
		panel.add(btnExit);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WaiterForm.this.closeWindow();
			}
		});
		btnExit.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExit.setVerticalAlignment(SwingConstants.TOP);
		btnExit.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExit.setIcon(AbstractForm.createImageIcon("no_cat.png"));

		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.OrderTabListener#categoryDisplayed(ua.cn.yet.waiter
	 * .ui.OrderTab)
	 */
	@Override
	public void categoryDisplayed(OrderTab orderTab) {
		categoriesAction.setEnabled(true);
	}
	
	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.ui.OrderTabListener#categoriesListDisplayed(ua.cn.yet.waiter.ui.OrderTab)
	 */
	@Override
	public void categoriesListDisplayed(OrderTab orderTab) {
		categoriesAction.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.OrderTabListener#orderCanceled(ua.cn.yet.waiter.ui
	 * .OrderTab)
	 */
	@Override
	public void orderCanceled(OrderTab orderTab) {
		try {
			orderService.delEntity(orderTab.getOrder());

			removeActiveTab();

		} catch (Exception e) {
			log.error("Failed to delete order: " + orderTab.getOrder(), e);
			JOptionPane.showMessageDialog(this.frame, e.getLocalizedMessage(),
					"Не получилось отменить заказ :(",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.OrderTabListener#orderClosed(ua.cn.yet.waiter.ui.
	 * OrderTab)
	 */
	@Override
	public void orderClosed(OrderTab orderTab) {
		try {
			orderTab.getOrder().closeOrder();

			Order order = orderService.save(orderTab.getOrder());

			removeActiveTab();
			
			if (Config.getBoolean(Config.SHOW_CHANGE_COUNT_ON_ORDER_CLOSE)) {
				new ChangeCalcDialog(this.frame, order);
			}

		} catch (Exception e) {
			log.error("Failed to delete order: " + orderTab.getOrder(), e);
			JOptionPane
					.showMessageDialog(this.frame, e.getLocalizedMessage(),
							"Не получилось закрыть заказ :(",
							JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Removing active tab
	 */
	private void removeActiveTab() {
		int index = ordersTabs.getSelectedIndex();
		ordersTabs.remove(index);
		orderTabsComponents.remove(index);

		if (ordersTabs.getTabCount() == 0) {
			updateContentPane();
		}
	}

	/**
	 * Makes tab with order an active tab
	 * 
	 * @param order
	 *            order to make active
	 */
	public void makeActiveTab(Order order) {
		for (OrderTab tab : orderTabsComponents) {
			if (tab.getOrder().equals(order)) {
				ordersTabs.setSelectedComponent(tab);
				break;
			}
		}
	}

	/**
	 * Action for viewing all categories for the order panel
	 * 
	 * @author Yuriy Tkach
	 */
	private class CategoriesAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CategoriesAction() {
			super("Категории", AbstractForm.createImageIcon("cats.png"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedIndex = ordersTabs.getSelectedIndex();
			OrderTab tab = orderTabsComponents.get(selectedIndex);

			tab.displayCategories();
		}

	}

	/**
	 * Listener for the new order button
	 * 
	 * @author Yuriy Tkach
	 */
	private class NewOrderListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Order order = NewOrderDialog.createOrder(WaiterForm.this.frame,
					WaiterForm.this.user);

			if (order != null) {
				if (log.isDebugEnabled()) {
					log.debug("Created " + order);
				}
				
				EventBus.publish(new OrderCreatedEvent(WaiterForm.this, order));

				loadUserOrders();
				updateContentPane();

				makeActiveTab(order);
			}
		}

	}
}
