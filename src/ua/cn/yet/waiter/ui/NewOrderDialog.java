package ua.cn.yet.waiter.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.util.Config;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Dialog for creating new order
 * 
 * @author Yuriy Tkach
 */
public class NewOrderDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(NewOrderDialog.class);

	private static final int BUTTON_SIZE = 40;

	private Font fontForBtn = new Font("", Font.PLAIN, 12);

	private boolean doCreateOrder = false;
	private int tableNum = Order.TABLE_NONE;

	/**
	 * Creating new order with params
	 * 
	 * @param parent
	 *            Parent frame
	 * @param user
	 *            User of the order
	 * @return Created order or <code>null</code>
	 */
	public static Order createOrder(JFrame parent, User user) {
		OrderService service = WaiterInstance
				.forId(WaiterInstance.ORDER_SERVICE);

		Set<Integer> occupiedTables = service.getOccupiedTables();

		NewOrderDialog dialog = new NewOrderDialog(parent, occupiedTables);
		dialog.setVisible(true);

		if (dialog.doCreateOrder) {
			Order order = new Order();
			order.setWaiter(user);
			order.setTableNumber(dialog.tableNum);

			try {
				order = service.save(order);
				return order;
			} catch (Exception e1) {
				log.error("Failed to save order " + order, e1);
				JOptionPane.showMessageDialog(parent, e1.getLocalizedMessage(),
						"Не могу создать заказ :(", JOptionPane.ERROR_MESSAGE);
			}
		}

		return null;
	}

	public NewOrderDialog(JFrame parent, Set<Integer> occupiedTables) {
		super(parent, "Новый заказ...", ModalityType.APPLICATION_MODAL);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		setLayout(new MigLayout("insets 5"));

		createTableNumButtons(occupiedTables);

		createCancelButton();

		pack();
		setLocationRelativeTo(parent);
	}

	/**
	 * Creating buttons with table buttons
	 * 
	 * @param occupiedTables
	 *            set of occupied table numbers
	 */
	private void createTableNumButtons(Set<Integer> occupiedTables) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Номер столика:"));
		panel.setLayout(new MigLayout("insets 5"));

		int tableCount = Config.getInteger(Config.TABLE_COUNT);
		int tableColNum = Config.getInteger(Config.TABLE_COL_COUNT);

		int i = 0;
		for (; i < tableCount; i++) {
			JButton btnTable = createTableNumberButton(i + 1, String
					.valueOf(i + 1), !occupiedTables.contains(i + 1));

			String layoutConf = "";
			if ((0 == ((i + 1) % tableColNum)) || ((i+1) == tableCount)) {
				layoutConf = "wrap";
			}
			panel.add(btnTable, layoutConf);
		}

		panel.add(createTableNumberButton(Order.TABLE_BAR, "Бар", true),
				"growx, span, wrap");

		panel.add(
				createTableNumberButton(Order.TABLE_NONE, "Без столика", true),
				"growx, span, wrap");

		this.add(panel, "growx, wrap");
	}

	/**
	 * Creating common button for the table number
	 * 
	 * @param number
	 *            Table number to set
	 * @param caption
	 *            Button's caption
	 * @param enabled
	 *            specifies if button should be enabled
	 * @return button
	 */
	private JButton createTableNumberButton(int number, String caption,
			boolean enabled) {
		JButton btnTable = new JButton();
		btnTable.setText(caption);
		btnTable.setFont(fontForBtn);
		btnTable.addActionListener(new TableNumberListener(number));
		btnTable.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
		btnTable.setEnabled(enabled);
		return btnTable;
	}

	/**
	 * Creating cancel button panel
	 */
	private void createCancelButton() {
		JPanel cancelBtnPanel = new JPanel();
		cancelBtnPanel.setLayout(new MigLayout("insets 0", "[right,grow]"));

		JButton btnCancel = new JButton("Отмена");
		btnCancel.setIcon(AbstractForm.createImageIcon("no.png"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCreateOrder = false;
				NewOrderDialog.this.dispose();
			}
		});
		cancelBtnPanel.add(btnCancel, "w 100::");
		this.add(cancelBtnPanel, "growx");
	}

	/**
	 * Listener to the table number button that sets that number in the field
	 * and closes dialog
	 * 
	 * @author Yuriy Tkach
	 */
	private class TableNumberListener implements ActionListener {

		private int num;

		public TableNumberListener(int num) {
			super();
			this.num = num;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doCreateOrder = true;
			tableNum = num;
			NewOrderDialog.this.dispose();
		}
	}

}
