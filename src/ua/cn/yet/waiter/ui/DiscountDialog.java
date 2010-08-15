package ua.cn.yet.waiter.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.EventBus;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.ui.components.DiscountInputListener;
import ua.cn.yet.waiter.ui.components.DiscountInputPanel;
import ua.cn.yet.waiter.ui.events.OrderChangedEvent;
import ua.cn.yet.waiter.util.WaiterInstance;

public class DiscountDialog extends JDialog {

	private static final Log log = LogFactory.getLog(DiscountDialog.class);
	
	private static final long serialVersionUID = 1L;
	private Order order;
	
	private OrderService orderService = WaiterInstance.forId(WaiterInstance.ORDER_SERVICE);
	
	public DiscountDialog(Window parent, Order order) {
		super(parent, "Скидка клиенту", ModalityType.APPLICATION_MODAL);

		this.order = order;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);

		setLayout(new MigLayout("insets 3", "[fill,grow]"));
		
		createDiscountPanel();
		
		createDialogButtons();

		pack();
		setLocationRelativeTo(parent);
		setVisible(true);

	}
	
	/**
	 * Creating panel for discount buttons
	 */
	public void createDiscountPanel(){
		DiscountInputPanel discountPanel = new DiscountInputPanel();
		discountPanel.addDiscountInputListener(new DiscountInputListener() {
			
			@Override
			public void discountApplied(double newValue) {
				order.setDiscount(newValue);
			
				try {
					orderService.save(order);
				} catch (Exception e) {
					log.error("Failed to update order: " + order, e);
					JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
							"Не получилось обновить элемент :(",
							JOptionPane.ERROR_MESSAGE);
				}
				
				EventBus.publish(new OrderChangedEvent(this, order));
			}
		});

		this.add(discountPanel,"wrap");
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
				DiscountDialog.this.dispose();
			}
		});
		panel.add(btnClose);

		this.add(panel, "wrap");
	}

}
