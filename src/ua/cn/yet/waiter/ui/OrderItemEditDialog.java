package ua.cn.yet.waiter.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.model.OrderedItem;
import ua.cn.yet.waiter.ui.components.AdvancedIntegerSpinner;

/**
 * Dialog for changing ordered item, which allows to change count and mass.
 * 
 * @author Yuriy Tkach
 */
public class OrderItemEditDialog extends JDialog {

	public static final int EDIT_OK_CHANGES = 0;
	public static final int EDIT_OK_NO_CHANGES = 1;
	public static final int EDIT_CANCEL = 2;

	private static final long serialVersionUID = 1L;
	private OrderedItem orderedItem;
	private AdvancedIntegerSpinner spinnerCount;
	private JButton btnOK;
	private JButton btnCancel;
	private AdvancedIntegerSpinner spinnerMass;

	private int editResult = EDIT_CANCEL;
	private JLabel totalSum;

	/**
	 * Edit ordered item
	 * 
	 * @param parent
	 *            Parent window of the dialog
	 * @param title
	 *            Title of the dialog
	 * @param item
	 *            Item to edit
	 * @return {@link #EDIT_CANCEL} if item was canceled,
	 *         {@link #EDIT_OK_CHANGES} if was edited, and
	 *         {@link #EDIT_OK_NO_CHANGES} if OK is pressed, but was not edited.
	 */
	public static int editOrderItem(Window parent, String title,
			OrderedItem item) {
		OrderItemEditDialog dialog = new OrderItemEditDialog(parent, title,
				item);
		return dialog.editResult;
	}

	public OrderItemEditDialog(Window parent, String title, OrderedItem item) {
		super(parent, title, ModalityType.APPLICATION_MODAL);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);

		this.orderedItem = item;

		makeUI();

		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Creating UI components for the dialog
	 */
	private void makeUI() {
		this.setLayout(new MigLayout("insets 5", "[grow,fill]", "[]"));

		createItemTitleBlock();
		createItemChangeBlock();
		createItemSummaryBlock();
		createButtonsBlock();
	}

	/**
	 * Creating components in the item's title block
	 */
	private void createItemTitleBlock() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(""));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new MigLayout("insets 0", "[left,grow][right,grow]",
						""));

		JLabel itemName = new JLabel(orderedItem.getName());
		itemName.setFont(new Font("", Font.BOLD, itemName.getFont().getSize()));
		panel.add(itemName, "span 2, align center, wrap");

		StringBuilder sb = new StringBuilder();
		sb.append("Цена за ");
		if (orderedItem.isMassEditableInOrder()) {
			sb.append(orderedItem.getMass());
			if (orderedItem.isLiquid()) {
				sb.append(" мл");
			} else {
				sb.append(" г");
			}
		} else {
			sb.append("1 порцию");
		}
		panel.add(new JLabel(sb.toString()));

		JLabel itemPrice = new JLabel(String.format("%.2f грн.", orderedItem
				.getPriceBillsAndCoins()));
		panel.add(itemPrice, "wrap");

		this.add(panel, "wrap");
	}

	/**
	 * Creating components for changing item's properties, like count and mass
	 */
	private void createItemChangeBlock() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0",
				"[left][right,grow][]", ""));

		panel.add(new JLabel("Количество:"));

		spinnerCount = new AdvancedIntegerSpinner(1,1);
		spinnerCount.setValue(orderedItem.getCount());
		spinnerCount.addKeyListener(new ControlsKeyListener());
		spinnerCount.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateTotalSum();
			}
		});
		panel.add(spinnerCount, "wrap");

		if (orderedItem.isMassEditableInOrder()) {
			JLabel lbMass = new JLabel();
			if (orderedItem.isLiquid()) {
				lbMass.setText("Объём:");
			} else {
				lbMass.setText("Масса:");
			}
			panel.add(lbMass);

			if (orderedItem.isLiquid()) {
				spinnerMass = new AdvancedIntegerSpinner(50, 50);
			} else {
				spinnerMass = new AdvancedIntegerSpinner(10, 10);
			}
			spinnerMass.setValue(orderedItem.getNewMass());
			spinnerMass.addKeyListener(new ControlsKeyListener());
			spinnerMass.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updateTotalSum();
				}
			});
			panel.add(spinnerMass);

			if (orderedItem.isLiquid()) {
				panel.add(new JLabel(" мл"), "wrap");
			} else {
				panel.add(new JLabel(" г"), "wrap");
			}
		}

		this.add(panel, "wrap");
	}

	/**
	 * Creating components for displaying item's summary, like total price
	 */
	private void createItemSummaryBlock() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(""));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new MigLayout("insets 0", "[grow,left][grow,right]",
						""));

		panel.add(new JLabel("Сумма:"));

		totalSum = new JLabel();
		totalSum.setFont(new Font("", Font.BOLD, totalSum.getFont().getSize()));
		panel.add(totalSum);
		updateTotalSum();

		this.add(panel, "span 2, wrap");
	}

	/**
	 * Updating total sum label based on selected count and mass
	 */
	private void updateTotalSum() {
		int newCount = ((Integer) spinnerCount.getValue()).intValue();
		int newMass = orderedItem.getNewMass();
		if (orderedItem.isMassEditableInOrder()) {
			newMass = ((Integer) spinnerMass.getValue()).intValue();
		}
		totalSum.setText(String.format("%.2f грн.", orderedItem
				.getOrderedPriceBillAndCoins(newMass, newCount)));
	}

	/**
	 * Creating buttons for submitting changed item
	 */
	private void createButtonsBlock() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		btnCancel = new JButton("Отмена");
		btnCancel.setIcon(AbstractForm.createImageIcon("no.png"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OrderItemEditDialog.this.editResult = EDIT_CANCEL;
				OrderItemEditDialog.this.dispose();
			}
		});

		btnOK = new JButton("Сохранить");
		btnOK.setIcon(AbstractForm.createImageIcon("ok.png"));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editResult = EDIT_OK_NO_CHANGES;

				int newCount = ((Integer) spinnerCount.getValue()).intValue();
				if (newCount != orderedItem.getCount()) {
					editResult = EDIT_OK_CHANGES;
					orderedItem.setCount(newCount);
				}

				if ((orderedItem.isMassEditableInOrder())
						&& (spinnerMass != null)) {
					int newMass = ((Integer) spinnerMass.getValue()).intValue();
					if (newMass != orderedItem.getNewMass()) {
						editResult = EDIT_OK_CHANGES;
						orderedItem.setNewMass(newMass);
					}
				}
				OrderItemEditDialog.this.dispose();
			}
		});

		panel.add(btnOK);
		panel.add(btnCancel);

		this.add(panel);
	}

	/**
	 * Listener for escape and enter keys
	 * 
	 * @author Yuriy Tkach
	 */
	private class ControlsKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				OrderItemEditDialog.this.dispose();
				break;
			case KeyEvent.VK_ENTER:
				btnOK.doClick();
				break;
			}
		}
	}

}
