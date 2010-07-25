package ua.cn.yet.waiter.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OutputElement;
import ua.cn.yet.waiter.service.CategoryService;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.ui.events.OrderChangedEvent;
import ua.cn.yet.waiter.util.Utils;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Class that represents tab for a single order
 * 
 * @author Yuriy Tkach
 */
public class OrderTab extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(OrderTab.class);

	private static final int ITEM_BUTTON_WIDTH = 120;
	private static final int ITEM_BUTTON_HEIGHT = 140;

	private static final Integer INSETS_ITEMS = 5;

	private Order order;

	private JPanel panelItems;

	private JScrollPane scrollPaneItems;

	private Collection<OutputElement> categories;
	private ReceiptTables receiptTables;

	private CategoryService categoryService;

	private OrderTabListener tabListener;

	private PrintingService printService;
	
	private boolean categoriesDisplay = true;

	public OrderTab(Order order, OrderTabListener tabListener) {
		this.order = order;
		this.tabListener = tabListener;

		AnnotationProcessor.process(this);
		
		printService = WaiterInstance.forId(WaiterInstance.PRINTING_SERVICE);

		categoryService = WaiterInstance.forId(WaiterInstance.CATEGORY_SERVICE);
		categories = categoryService.getAllSortedAsOutputElements();

		setLayout(new MigLayout("insets 5", "[fill,grow]", "[fill,grow]"));

		scrollPaneItems = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPaneItems, "width 660!");

		panelItems = new JPanel();
		displayCategories();
		scrollPaneItems.setViewportView(panelItems);

		add(createReceiptPanel());
	}

	/**
	 * @return Panel for receipt table
	 */
	private Component createReceiptPanel() {
		JPanel panel = new JPanel(new MigLayout("insets 0", "[fill,grow]",
				"[grow,fill][]"));

		receiptTables = new ReceiptTables(order, true);
		panel.add(receiptTables, "wrap");

		// ------------------------------------------

		panel.add(createOrderButtonsPanel());

		return panel;
	}

	/**
	 * @return panel with buttons for manipulating the receipt
	 */
	private Component createOrderButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0", "[left][grow,center][right]",
				""));

		JButton btnCancelOrder = new JButton("Отменить");
		btnCancelOrder.setToolTipText("Отменить заказ");
		panel.add(btnCancelOrder, "w 80::");
		btnCancelOrder.addActionListener(new CancelOrderListener());
		btnCancelOrder.setIcon(AbstractForm.createImageIcon("cancelorder.png"));

		JButton btnPrintOrder = new JButton("Печать");
		btnPrintOrder.setToolTipText("Напечатать отдельные квитанции заказа");
		panel.add(btnPrintOrder, "w 80::");
		btnPrintOrder.addActionListener(new PrintOrderListener());
		btnPrintOrder.setIcon(AbstractForm.createImageIcon("fileprint.png"));

		JButton btnCloseOrder = new JButton("Чек");
		btnCloseOrder.setToolTipText("Напечатать чек и закрыть заказ");
		btnCloseOrder.setFont(new Font("", Font.BOLD, 12));
		panel.add(btnCloseOrder, "w 80::");
		btnCloseOrder.addActionListener(new CloseOrderListener());
		btnCloseOrder.setIcon(AbstractForm
				.createImageIcon("receipt_invoice.png"));

		return panel;
	}

	/**
	 * Displaying all available categories
	 */
	public void displayCategories() {
		categoriesDisplay = true;
		updateItemsPane(panelItems, categories);
		tabListener.categoriesListDisplayed(this);
	}

	/**
	 * 
	 * @param contentPane
	 *            Pane where to add items
	 */
	private void updateItemsPane(Container contentPane,
			Collection<OutputElement> elems) {
		contentPane.removeAll();

		StringBuilder layoutPanelConstraint = new StringBuilder("wrap ");
		layoutPanelConstraint.append(5).append(", insets ")
				.append(INSETS_ITEMS);

		contentPane.setLayout(new MigLayout(layoutPanelConstraint.toString(),
				"", ""));

		StringBuilder layoutButtonConstraint = new StringBuilder("width ");
		layoutButtonConstraint.append(ITEM_BUTTON_WIDTH).append("!, height ")
				.append(ITEM_BUTTON_HEIGHT).append("!");
		
		if (!categoriesDisplay) {
			JButton button = createBackToCategoriesButton();
			contentPane.add(button);
		}

		for (OutputElement outputElement : elems) {
			JButton button = createItemButton(outputElement);
			contentPane.add(button, layoutButtonConstraint.toString());
		}

		contentPane.doLayout();
		scrollPaneItems.doLayout();
		contentPane.repaint();
	}

	/**
	 * Creating back to categories button
	 * 
	 * @return back to categories button
	 */
	private JButton createBackToCategoriesButton() {
		OutputElement elem = new OutputElement() {
			@Override
			public boolean isDisabled() {
				return false;
			}

			@Override
			public String getPicture() {
				return null;
			}

			@Override
			public String getName() {
				return "Категории";
			}
		};
		JButton button = createItemButton(elem);
		button.setIcon(AbstractForm.createImageIcon("cat_back.png"));
		ActionListener[] listeners = button.getActionListeners();
		for (int i = 0; i < listeners.length; i++) {
			button.removeActionListener(listeners[i]);
		}
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayCategories();
			}
		});
		return button;
	}

	/**
	 * Creating button for the output element
	 * 
	 * @param outputElement
	 *            Element where to take information from
	 * @return Button for the item
	 */
	private JButton createItemButton(OutputElement outputElement) {
		String text = "<html><center><b>" + outputElement.getName()
				+ "</b></center></html>";

		JButton button = new JButton(text, Utils
				.getImageIconForElem(outputElement));
		button.setPreferredSize(new Dimension(ITEM_BUTTON_WIDTH,
				ITEM_BUTTON_HEIGHT));
		button.setMinimumSize(new Dimension(ITEM_BUTTON_WIDTH,
				ITEM_BUTTON_HEIGHT));
		button.setSize(new Dimension(ITEM_BUTTON_WIDTH, ITEM_BUTTON_HEIGHT));
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setVerticalAlignment(SwingConstants.TOP);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.addActionListener(new ItemButtonListener(outputElement));
		button.setName(outputElement.getName());
		button.setEnabled(!outputElement.isDisabled());
		return button;
	}

	/**
	 * Listener for item buttons
	 * 
	 * @author Yuriy Tkach
	 */
	private class ItemButtonListener implements ActionListener {

		private OutputElement outputElement;

		public ItemButtonListener(OutputElement outputElement) {
			this.outputElement = outputElement;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Category.class.isAssignableFrom(outputElement.getClass())) {
				// Displaying category items
				categoriesDisplay = false;
				updateItemsPane(panelItems, new TreeSet<OutputElement>(
						((Category) outputElement).getItems()));
				tabListener.categoryDisplayed(OrderTab.this);
			} else {
				// Adding item to the receipt
				Item item = (Item) outputElement;
				receiptTables.addItem(item);
			}
		}
	}

	/**
	 * Listener for cancel order button
	 * 
	 * @author Yuriy Tkach
	 */
	private class CancelOrderListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int rez = JOptionPane.showConfirmDialog(OrderTab.this,
					"Отменить заказ " + OrderTab.this.order.getTitle() + "?",
					"Точно?", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (JOptionPane.YES_OPTION == rez) {
				tabListener.orderCanceled(OrderTab.this);
			}
		}
	}

	/**
	 * Listener for print order button
	 * 
	 * @author Yuriy Tkach
	 */
	private class PrintOrderListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!order.getItems().isEmpty()) {
				if(printService.printOrder(order)){
					tabListener.orderPrinted(OrderTab.this);
				}
			} else {
				JOptionPane.showMessageDialog(OrderTab.this,
						"Добавьте элементы в заказ, чтобы их распечатать.",
						"Печатать нечего :(", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Listener for close order button
	 * 
	 * @author Yuriy Tkach
	 */
	private class CloseOrderListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!order.getItems().isEmpty()) {
				int rez = JOptionPane
						.showConfirmDialog(
								OrderTab.this,
								"Напечатать чек для клиента и закрыть заказ?\n"
										+ "Заказ нельзя будет редактировать после этого.",
								"Заказ " + OrderTab.this.order.getTitle(),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								AbstractForm
										.createImageIcon("receipt_print_question.png"));

				if (JOptionPane.YES_OPTION == rez) {
					if (printService.printReceipt(order)) {
						tabListener.orderClosed(OrderTab.this);
					}
				}
			} else {
				JOptionPane
						.showMessageDialog(
								OrderTab.this,
								"Добавьте элементы в заказ, чтобы их распечатать и закрыть заказ.",
								"Закрывать нечего :(",
								JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Reassigning order after it was changed
	 * 
	 * @param e
	 *            event object
	 */
	@EventSubscriber(eventClass = OrderChangedEvent.class)
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

	/**
	 * @return the categoriesDisplay
	 */
	public boolean isCategoriesDisplay() {
		return categoriesDisplay;
	}

}
