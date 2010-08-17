package ua.cn.yet.waiter.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderedItem;
import ua.cn.yet.waiter.model.OutputElement;
import ua.cn.yet.waiter.service.CategoryService;
import ua.cn.yet.waiter.service.OrderedItemService;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.ui.components.QuickSearchTextField;
import ua.cn.yet.waiter.ui.events.OrderChangedEvent;
import ua.cn.yet.waiter.util.Utils;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Class that represents tab for a single order
 * 
 * @author Yuriy Tkach
 */
@SuppressWarnings("serial")
public class OrderTab extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(OrderTab.class);

	private static final int ITEM_BUTTON_WIDTH = 120;
	private static final int ITEM_BUTTON_HEIGHT = 140;

	private static final Integer INSETS_ITEMS = 5;
	
	private static final String DEFAULT_CATEGORY_LABEL_TEXT="Категории:";
	
	private static final Color COLOR_GREEN = new Color(54,100,26);

	private Order order;

	private JPanel panelItems;

	private JScrollPane scrollPaneItems;

	private Collection<OutputElement> categories;
	private ReceiptTables receiptTables;

	private CategoryService categoryService;

	private OrderTabListener tabListener;

	private PrintingService printService;
	
	private boolean categoriesDisplay = true;
	
	private JLabel categoryNameLabel;
	
	private JTextField itemSearchField;
	
	private JPanel northPanel;
		
	private JPanel getNorthPanel(){
		if (northPanel == null) {
			MigLayout layout = new MigLayout("fillx, insets n n 0 n","","center");

			northPanel= new JPanel(layout);
			northPanel.add(getCategoryNameLabel(),"gapleft 3, align left");
			northPanel.add(getItemSearchField()," align right, split 2");
			
			JButton btnClear = new JButton(AbstractForm.createImageIcon("clear_left.png"));
			btnClear.setToolTipText("Очистить поле");
			btnClear.setMargin(new Insets(0, 0, 0, 0));
			btnClear.setBorderPainted(false);
			
			btnClear.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					getItemSearchField().setText("");	
					getItemSearchField().requestFocus();
				}
			});
			
			northPanel.add(btnClear," align right, gapleft 0, width 24!");

		}
		northPanel.doLayout();
		return northPanel;		
	}
	
	private JTextField getItemSearchField(){
		if (itemSearchField == null) {
			itemSearchField = new QuickSearchTextField("Быстрый поиск (Ctrl+F)");
			Dimension size = itemSearchField.getPreferredSize();
			size.setSize(size.getWidth()+20, size.getHeight());
			itemSearchField.setPreferredSize(size);
			itemSearchField.addKeyListener(new QuickSearchListener());

			InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
			ActionMap am = getActionMap();
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK, true), "ctrl+f");
			am.put("ctrl+f", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getItemSearchField().requestFocus();
				}
			});
		}
		return itemSearchField;
	}
	
	private JLabel getCategoryNameLabel(){
		if(categoryNameLabel==null){
			categoryNameLabel = new JLabel(DEFAULT_CATEGORY_LABEL_TEXT);
			categoryNameLabel.setForeground(COLOR_GREEN);
			categoryNameLabel.setFont(categoryNameLabel.getFont().deriveFont(18.0f));
		}
		return categoryNameLabel;
	}
	
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
		panel.add(btnPrintOrder, "split 2, w 80::");
		btnPrintOrder.addActionListener(new PrintOrderListener());
		btnPrintOrder.setIcon(AbstractForm.createImageIcon("fileprint.png"));
		
		JButton btnDiscount = new JButton("Скидка");
		btnDiscount.setToolTipText("Сделать скидку");
		panel.add(btnDiscount, "w 80::");
		btnDiscount.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				new DiscountDialog(SwingUtilities.windowForComponent(OrderTab.this), order);				
			}
		});
		btnDiscount.setIcon(AbstractForm.createImageIcon("discount16.png"));

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
		
		if (categoriesDisplay) {
			getCategoryNameLabel().setText(DEFAULT_CATEGORY_LABEL_TEXT);
		}

		contentPane.add(getNorthPanel(),"dock north, width 630!");
		
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
				Category category = (Category) outputElement;
				getCategoryNameLabel().setText(category.getName()+":");
				updateItemsPane(panelItems, new TreeSet<OutputElement>(category.getItems()));
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

			boolean printUpdatesOnly = false;
			
			if(order.isPrinted() && order.isUpdated()){	
				Object[] options = {"Только изменения", "Весь заказ","Отмена"};
				int choise = JOptionPane.showOptionDialog(
						OrderTab.this,
						"Распечатать только последние изменения в заказе?", 
						"Заказ " + OrderTab.this.order.getTitle(), 
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						AbstractForm.createImageIcon("receipt_print_question.png"),
						options,options[0]);
				
				switch (choise){
					//Only changes option
					case 0: 
						printUpdatesOnly = true;
						break;
					//Cancel option
					case 2: 
						return; 
				}
			}
						
			if (!order.getItems().isEmpty()) {
				if(printService.printOrder(order,printUpdatesOnly)){
					processOrderAfterPrinting(order);
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
	 * Updates OrderedItem entities after printing
	 * @param order
	 */
	private void processOrderAfterPrinting(Order order) {
		OrderedItemService orderedItemService = WaiterInstance.forId(WaiterInstance.ORDERED_ITEM_SERVICE);
		try{
			for(OrderedItem item: order.getItems()){
					orderedItemService.save(item);
			}
		} catch (Exception e){
			log.error("Failed to update ordered items after printing for order: " + order, e);
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"Не получилось обновить заказ после печати :(",
					JOptionPane.ERROR_MESSAGE);
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
	
	
	private class QuickSearchListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				JButton foundButton = null;
				for(Component comp: panelItems.getComponents()){
					if(comp instanceof JButton){
						JButton btn = (JButton) comp;
						if(btn.getText().toLowerCase().contains(getItemSearchField().getText().toLowerCase())){
							foundButton = btn;
							break;
						}
					}
				}
				
				if(foundButton == null) {
					return;
				}
				
				foundButton.setBorder(BorderFactory.createLineBorder(COLOR_GREEN,3));
				foundButton.repaint();
				foundButton.requestFocus();
				
				final JButton btnForThread = foundButton;
				
				Thread t = new Thread(){
					public void run(){
						try {
							sleep(3000);
						} catch (InterruptedException e) {}
						
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								btnForThread.setBorder(null);
								btnForThread.repaint();
							}
						});
					}
				};
				
				t.start();
				
			}
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {

		}

		@Override
		public void keyTyped(KeyEvent arg0) {
						
		}
		
	}

}
