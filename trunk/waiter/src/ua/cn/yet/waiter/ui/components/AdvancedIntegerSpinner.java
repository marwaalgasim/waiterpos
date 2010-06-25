package ua.cn.yet.waiter.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import net.miginfocom.swing.MigLayout;

/**
 * Advanced spinner that has bigger buttons to change values. 
 * 
 * @author Yuriy Tkach
 */
public class AdvancedIntegerSpinner extends JPanel {

	private static final long serialVersionUID = 1L;
	private Integer step = 1;
	private Integer minimum = Integer.MIN_VALUE;
	private JFormattedTextField textNumber;

	private Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();

	public AdvancedIntegerSpinner(Integer step) {
		this(step, null);
	}

	public AdvancedIntegerSpinner(Integer step, Integer minimum) {
		if (step != null) {
			this.step = step;
		}
		if (minimum != null) {
			this.minimum = minimum;
		}
		makeUI();
	}

	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}

	/**
	 * Making UI
	 */
	private void makeUI() {
		setLayout(new MigLayout("insets 1, gap 1"));

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setParseIntegerOnly(true);
		numberFormat.setGroupingUsed(false);

		NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
		numberFormatter.setMinimum(minimum);

		textNumber = new JFormattedTextField(numberFormatter);
		textNumber.setValue(step);
		textNumber.addKeyListener(new TextNumberKeyListener());
		this.add(textNumber, "gapright 3, w 60::");

		JButton btn;

		btn = new JButton();
		btn.setText("+" + step);
		btn.addActionListener(new ValueChangeActionListener(step, true));
		this.add(btn, "w 40::, growx");

		btn = new JButton();
		btn.setText("-" + step);
		btn.addActionListener(new ValueChangeActionListener(step, false));
		this.add(btn, "w 40::, growx");
	}

	/**
	 * Action listener for buttons that change value
	 * 
	 * @author Yuriy Tkach
	 */
	private class ValueChangeActionListener implements ActionListener {

		private int num;
		private boolean increase;

		public ValueChangeActionListener(int num, boolean increase) {
			this.num = num;
			this.increase = increase;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			changeValue(num, increase);
		}
	}

	/**
	 * Notifying listeners that value had changed
	 */
	private void notifyListeners() {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
		}
	}

	/**
	 * @return value of the spinner
	 */
	public Integer getValue() {
		return (Integer) textNumber.getValue();
	}

	/**
	 * @param newValue
	 *            new value to set.
	 */
	public void setValue(int newValue) {
		if (newValue >= minimum) {
			textNumber.setValue(Integer.valueOf(newValue));
		}
	}
	
	/**
	 * Changing value
	 * 
	 * @param num
	 *            Number to change to
	 * @param increase
	 *            Increase or decrease
	 */
	private void changeValue(int num, boolean increase) {
		Integer value = (Integer) textNumber.getValue();
		if (increase) {
			value += num;
		} else {
			if ((value - num) >= minimum) {
				value -= num;
			} else {
				return;
			}
		}
		textNumber.setValue(value);
		notifyListeners();
	}

	private class TextNumberKeyListener extends KeyAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				changeValue(step, true);
				e.consume();
				break;
			case KeyEvent.VK_DOWN:
				changeValue(step, false);
				e.consume();
				break;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#addKeyListener(java.awt.event.KeyListener)
	 */
	@Override
	public synchronized void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			this.getComponent(i).addKeyListener(l);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#removeKeyListener(java.awt.event.KeyListener)
	 */
	@Override
	public synchronized void removeKeyListener(KeyListener l) {
		super.removeKeyListener(l);
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			this.getComponent(i).removeKeyListener(l);
		}
	}

}
