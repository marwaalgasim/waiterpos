package ua.cn.yet.waiter.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.ui.AbstractForm;
import ua.cn.yet.waiter.util.Config;

/**
 * Panel displaying discount buttons. 
 * Discount values should be defined in property file.
 * 
 * @author n0weak
 *
 */
public class DiscountInputPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private static final int BTN_DEFAULT_WIDTH = 60;
	private static final int BTN_DEFAULT_HEIGHT = 50;
	private static final int BTNS_PER_LINE = 5;

	private Set<DiscountInputListener> listeners = new HashSet<DiscountInputListener>();
		
	private Icon discountIcon = AbstractForm.createImageIcon("discount+.png");
	
	
	private JPanel btnsPanel;

	public DiscountInputPanel() {
		
		setLayout(new MigLayout("fillx, insets 5 5 5 5","","center"));
		
		add(getButtonsPanel(),"align center");
		
		
		
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(54,100,26)), "Величина скидки (в %):"));
	}
	
	private JPanel getButtonsPanel() {
		if (this.btnsPanel == null) {
			btnsPanel = new JPanel(new MigLayout("insets 0 0 0 0"));
						
			String discountValues = Config.getString(Config.DISCOUNT_VALUES);
			StringTokenizer st = new StringTokenizer(discountValues, ",");
			
			int cnt=1;
			while (st.hasMoreTokens()) {
				JButton btn = new JButton(st.nextToken(),discountIcon);
				btn.setPreferredSize(new Dimension(BTN_DEFAULT_WIDTH, BTN_DEFAULT_HEIGHT));
				
				btn.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JButton source = (JButton) arg0.getSource();
						double discount= Double.parseDouble(source.getText()) / 100.0;
						notifyListeners(discount);
					}
				});
				
				String migParam=cnt%BTNS_PER_LINE==0?"wrap":"";
				cnt++;
				btnsPanel.add(btn,migParam);
			}
		}
		return this.btnsPanel;
	}
	
	/**
	 * Notifying listeners about new sum value
	 */
	private void notifyListeners(double discount) {
		for (DiscountInputListener listener : listeners) {
			listener.discountApplied(discount);
		}
	}

	/**
	 * @param listener
	 *            new listener to add
	 */
	public void addDiscountInputListener(DiscountInputListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener
	 *            new listener to remove
	 */
	public void removeDiscountInputListener(DiscountInputListener listener) {
		listeners.remove(listener);
	}
}
