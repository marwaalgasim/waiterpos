package ua.cn.yet.waiter.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.ui.components.DiscountInputListener;
import ua.cn.yet.waiter.ui.components.DiscountInputPanel;
import ua.cn.yet.waiter.ui.components.NumericInputListener;
import ua.cn.yet.waiter.ui.components.NumericInputPanel;

/**
 * Dialog for calculating change return for the client
 * 
 * @author Yuriy Tkach
 */
public class ChangeCalcDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	private Order order;
	private JLabel lbClientMoney;
	private JLabel lbChangeSum;
	private JLabel lbSum;

	private double orderSum = 0.0;
	private double clientMoney = 0.0;
	private NumericInputPanel numericInputPanel;

	public ChangeCalcDialog(Window parent, Order order) {
		super(parent, "Расчет сдачи клиенту", ModalityType.APPLICATION_MODAL);

		this.order = order;
		this.orderSum = order.getSum();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		setLayout(new MigLayout("insets 3", "[fill,grow]"));

		createOrderTitle();
		createDiscountPanel();
		createInputPanel();
		createChangePanel();
		createDialogButtons();

		pack();
		setLocationRelativeTo(parent);
		setVisible(true);

	}
	
	public void createDiscountPanel(){
		DiscountInputPanel discountPanel = new DiscountInputPanel(orderSum);
		discountPanel.addDiscountInputListener(new DiscountInputListener() {
			
			@Override
			public void discountApplied(double newValue) {
				lbSum.setText(String.format("%.2f грн.", newValue));
				orderSum = newValue;
				updateChangeSum(clientMoney, newValue);
			}
		});

		this.add(discountPanel,"wrap");
	}

	/**
	 * Creating order title panel where we display order number and order sum
	 */
	private void createOrderTitle() {
		JPanel panelTitle = new JPanel();
		panelTitle.setBorder(BorderFactory.createTitledBorder(""));
		panelTitle.setBackground(Color.WHITE);
		panelTitle.setLayout(new MigLayout("insets 2",
				"[left][right,grow]", ""));

		Font boldFont = new Font("", Font.BOLD, 14);

		JLabel lbOrder = new JLabel();
		lbOrder.setText("Заказ №" + order.getId() +":");
		lbOrder.setFont(boldFont);
		panelTitle.add(lbOrder);

		JLabel lbSum = new JLabel();
		lbSum.setText(String.format("%.2f грн.", orderSum));
		lbSum.setFont(boldFont);
		panelTitle.add(lbSum, "wrap");
		
		this.lbSum = lbSum;

		JLabel lbClient = new JLabel();
		lbClient.setText("Деньги клиента:");
		lbClient.setFont(boldFont);
		lbClient.setForeground(Color.BLUE);
		panelTitle.add(lbClient);

		lbClientMoney = new JLabel();
		lbClientMoney.setText("0 грн.");
		lbClientMoney.setFont(boldFont);
		lbClientMoney.setForeground(Color.BLUE);
		panelTitle.add(lbClientMoney, "wrap");

		this.add(panelTitle, "wrap");
	}

	/**
	 * Creating panel with number input buttons, clear buttons and image
	 */
	private void createInputPanel() {
		numericInputPanel = new NumericInputPanel(false, true, 
				AbstractForm.createImageIcon("coins100.png"));
		
		numericInputPanel.addNumberInputListener(new NumericInputListener() {
			@Override
			public void numberChanged(double newNumber) {
				clientMoney = newNumber;
				updateClientMoneyAndChange(numericInputPanel.getDecimalInput(), clientMoney);				
			}
		});
		
		this.add(numericInputPanel, "wrap");
	}

	
	/**
	 * Creating panel, which displays change
	 */
	private void createChangePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(""));
		panel.setBackground(Color.WHITE);
		panel.setLayout(new MigLayout("insets 2", "[left][right,grow]",
				"[]"));

		JLabel lbChange = new JLabel("Сдача клиенту:");
		lbChange.setFont(new Font("", Font.BOLD, 14));
		panel.add(lbChange);

		lbChangeSum = new JLabel(String.format("%.2f грн.", 0.0));
		lbChangeSum.setFont(new Font("", Font.BOLD, 16));
		panel.add(lbChangeSum);

		this.add(panel, "wrap");
	}

	/**
	 * Creating panel for dialog buttons
	 */
	private void createDialogButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 2", "[center,grow]", ""));

		JButton btnClose = new JButton("Закрыть");
		btnClose.setIcon(AbstractForm.createImageIcon("no.png"));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChangeCalcDialog.this.dispose();
			}
		});
		panel.add(btnClose);

		this.add(panel, "wrap");
	}

	/**
	 * Updating client money and change display
	 */
	private void updateClientMoneyAndChange(int decimalInput, double clientMoney) {
		switch (decimalInput) {
		case 0:
			lbClientMoney.setText(String.format("%d грн.", (int)clientMoney));
			break;
		case 1:
			lbClientMoney.setText(String.format("%d, грн.", (int)clientMoney));
			break;
		case 2:
			lbClientMoney.setText(String.format("%.1f грн.", clientMoney));
			break;
		default:
			lbClientMoney.setText(String.format("%.2f грн.", clientMoney));
			break;
		}
		
		updateChangeSum(clientMoney, orderSum);

	}
	
	/**
	 * Updating change display
	 */
	private void updateChangeSum(double clientMoney, double orderSum){
		lbChangeSum.setText(String.format("%.2f грн.", clientMoney - orderSum));
		if (clientMoney < orderSum) {
			lbChangeSum.setForeground(Color.RED);
		} else {
			lbChangeSum.setForeground(new Color(0, 100, 0));
		}
	}

}
