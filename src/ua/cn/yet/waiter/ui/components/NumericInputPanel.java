package ua.cn.yet.waiter.ui.components;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Panel, which displays numeric buttons together with clear buttons, which
 * allow to input numbers: integers and doubles.
 * 
 * Note! The component allows input of ONLY 2 DECIMAL DIGITS for now. It is used
 * mostly for inputing price, thus I was not working on improving it.
 * 
 * @author Yuriy Tkach
 */
public class NumericInputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int NUMBER_BUTTON_SIZE = 50;

	/**
	 * Listeners to the input events
	 */
	private Set<NumericInputListener> listeners = new HashSet<NumericInputListener>();

	/** Number that is input */
	private double number = 0.0;

	/** Specifies decimal input place */
	private int decimalInput = 0;

	/** Specifies if notification should be sent on each change */
	private boolean notifyOnEachChange;

	private JButton btnEnter;

	/**
	 * Creating numeric input panel
	 * 
	 * @param onlyIntegers
	 *            If <code>true</code> then only integers will be allowed
	 * @param notifyOnEachChange
	 *            If <code>true</code> then on each digit input (or clearing)
	 *            the listeners will be notified. Otherwise, only after pressing
	 *            Enter button/key.
	 * @param image
	 *            Image to display under clear buttons. Can be <code>null</code>
	 */
	public NumericInputPanel(boolean onlyIntegers, boolean notifyOnEachChange,
			ImageIcon image) {

		this.notifyOnEachChange = notifyOnEachChange;

		this.setLayout(new MigLayout("insets 0", "[][]", "[][]"));

		JPanel panelNumbers = createNumberInputButtons(onlyIntegers);
		this.add(panelNumbers, "growy, span 1 2");

		JPanel panelClearBtn = createClearInputButtons();
		this.add(panelClearBtn, "wrap");

		if (image != null) {
			JLabel icon = new JLabel();
			icon.setIcon(image);
			icon.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE * 2,
					NUMBER_BUTTON_SIZE * 2));
			this.add(icon);
		}

	}

	/**
	 * Creating panel with number buttons
	 */
	private JPanel createNumberInputButtons(boolean onlyIntegers) {
		Font font = new Font("", Font.PLAIN, 12);
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 2"));

		for (int i = 7; i <= 9; i++) {
			JButton btn = new JButton(String.valueOf(i));
			btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
					NUMBER_BUTTON_SIZE));
			btn.setFont(font);
			btn.addActionListener(new NumberButtonListener(i));
			panel.add(btn);
		}
		panel.add(new JLabel(""), "wrap");

		for (int i = 4; i <= 6; i++) {
			JButton btn = new JButton(String.valueOf(i));
			btn.addActionListener(new NumberButtonListener(i));
			btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
					NUMBER_BUTTON_SIZE));
			btn.setFont(font);
			panel.add(btn);
		}
		panel.add(new JLabel(""), "wrap");

		for (int i = 1; i <= 3; i++) {
			JButton btn = new JButton(String.valueOf(i));
			btn.addActionListener(new NumberButtonListener(i));
			btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
					NUMBER_BUTTON_SIZE));
			btn.setFont(font);
			panel.add(btn);
		}
		panel.add(new JLabel(""), "wrap");

		JButton btn = new JButton(String.valueOf(0));
		btn.addActionListener(new NumberButtonListener(0));
		btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
				NUMBER_BUTTON_SIZE));
		btn.setFont(font);
		panel.add(btn, "span 2, growx");

		if (!onlyIntegers) {
			btn = new JButton(",");
			btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
					NUMBER_BUTTON_SIZE));
			btn.setFont(font);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (0 == decimalInput) {
						decimalInput++;
						if (notifyOnEachChange) {
							notifyListeners();
						}
					}
				}
			});
			panel.add(btn);
			panel.add(new JLabel(""), "wrap");
		}

		return panel;
	}

	/**
	 * Creating buttons that clear value
	 */
	private JPanel createClearInputButtons() {
		Font font = new Font("", Font.PLAIN, 12);
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 2"));

		JButton btn = new JButton(String.valueOf((char) 0x2190));
		btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
				NUMBER_BUTTON_SIZE));
		btn.setFont(font);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processBackspaceInput();
			}
		});
		panel.add(btn);

		btn = new JButton("CE");
		btn.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
				NUMBER_BUTTON_SIZE));
		btn.setFont(font);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearInput();
			}
		});
		panel.add(btn, "wrap");

		btnEnter = new JButton("Enter");
		btnEnter.setPreferredSize(new Dimension(NUMBER_BUTTON_SIZE,
				NUMBER_BUTTON_SIZE));
		btnEnter.setFont(font);
		btnEnter.setVisible(!notifyOnEachChange);
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyListeners();
			}
		});
		panel.add(btnEnter, "span,growx,wrap");

		return panel;
	}

	/**
	 * Processing number input
	 * 
	 * @param num
	 *            number that was input
	 */
	private void processNumberInput(int num) {
		switch (decimalInput) {
		case 0:
			number *= 10;
			number += num;
			break;
		case 1:
			number += ((double) num / 10);
			decimalInput++;
			break;
		case 2:
			number += ((double) num / 100);
			decimalInput++;
			break;
		default:
			break;
		}
		if (notifyOnEachChange) {
			notifyListeners();
		}
	}

	/**
	 * Deleting last input
	 */
	private void processBackspaceInput() {
		switch (decimalInput) {
		case 0:
			number = (int) (number / 10);
			break;
		case 1:
			decimalInput--;
			break;
		case 2:
			number = (int) number;
			decimalInput--;
			break;
		case 3:
			int num = (int) (number * 10);
			number = ((double)num) / 10;
			decimalInput--;
			break;
		}
		if (notifyOnEachChange) {
			notifyListeners();
		}
	}

	/**
	 * Clearing completely user's input
	 */
	private void clearInput() {
		number = 0;
		decimalInput = 0;
		if (notifyOnEachChange) {
			notifyListeners();
		}
	}

	/**
	 * Notifying listeners about number change
	 */
	private void notifyListeners() {
		for (NumericInputListener listener : listeners) {
			listener.numberChanged(number);
		}
	}

	/**
	 * @param listener
	 *            new listener to add
	 */
	public void addNumberInputListener(NumericInputListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener
	 *            new listener to remove
	 */
	public void removeNumberInputListener(NumericInputListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Listener to the number input buttons
	 * 
	 * @author Yuriy Tkach
	 */
	private class NumberButtonListener implements ActionListener {

		private int num;

		public NumberButtonListener(int num) {
			this.num = num;
		}

		public void actionPerformed(ActionEvent e) {
			processNumberInput(num);
		}
	}

	/**
	 * @return the notifyOnEachChange
	 */
	public boolean isNotifyOnEachChange() {
		return notifyOnEachChange;
	}

	/**
	 * @param notifyOnEachChange
	 *            the notifyOnEachChange to set
	 */
	public void setNotifyOnEachChange(boolean notifyOnEachChange) {
		this.notifyOnEachChange = notifyOnEachChange;
		btnEnter.setVisible(!notifyOnEachChange);
	}

	/**
	 * @return the number
	 */
	public double getNumber() {
		return number;
	}

	/**
	 * @return the decimalInput
	 */
	public int getDecimalInput() {
		return decimalInput;
	}
}
