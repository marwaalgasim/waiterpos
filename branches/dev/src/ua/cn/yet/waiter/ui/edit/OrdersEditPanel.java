package ua.cn.yet.waiter.ui.edit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderReport;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.service.UserService;
import ua.cn.yet.waiter.ui.AbstractForm;
import ua.cn.yet.waiter.ui.OrdersViewPanel;
import ua.cn.yet.waiter.ui.events.OrderDeletedEvent;
import ua.cn.yet.waiter.ui.table.models.TableModelOrderReport;
import ua.cn.yet.waiter.util.WaiterInstance;

import com.toedter.calendar.JDateChooser;

/**
 * Edit panel for orders, which allows to filter orders by several criteria
 * 
 * @author Yuriy Tkach
 */
public class OrdersEditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private OrdersViewPanel ordersPanel;
	private JDateChooser filterDateTo;
	private JDateChooser filterDateFrom;

	private UserService userService;
	private PrintingService printService;

	private JComboBox comboWaiters;
	private JComboBox comboPeriodType;
	private JComboBox comboStatus;
	private OrderReport report;
	private TableModelOrderReport modelOrderReport;
	private ActionListener filterOrdersListener;
	private JButton btnFilterOrders;

	public OrdersEditPanel(JFrame parentFrame) {
		super();
		AnnotationProcessor.process(this);
		
		setLayout(new MigLayout("insets 5", "[grow, fill]",
				"[fill][grow,fill][fill]"));

		userService = WaiterInstance.forId(WaiterInstance.USER_SERVICE);
		printService = WaiterInstance.forId(WaiterInstance.PRINTING_SERVICE);

		createFilterControlsPanel();
		createOrdersPanel();
		createReportPanel();
		
		comboPeriodType.setSelectedIndex(1);
		btnFilterOrders.doClick();
		
	}

	/**
	 * Creating controls for filtering orders
	 */
	private void createFilterControlsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 5", "", ""));

		panel.add(new JLabel("Заказы"));

		comboStatus = new JComboBox();
		comboStatus.addItem("Все");
		comboStatus.addItem("Закрытые");
		comboStatus.addItem("Открытые");
		comboStatus.addItem("Удал./отм.");
		comboStatus.addItem("Без удал./отм.");
		panel.add(comboStatus);

		panel.add(new JLabel("за"));

		comboPeriodType = new JComboBox();
		comboPeriodType.addItem("Все время");
		comboPeriodType.addItem("День");
		comboPeriodType.addItem("Период");
		comboPeriodType.setEditable(false);
		panel.add(comboPeriodType);

		final JLabel lbFrom = new JLabel("с");
		panel.add(lbFrom);

		filterDateFrom = new JDateChooser(Calendar.getInstance().getTime());
		panel.add(filterDateFrom, "w 150::");
		filterDateFrom.addPropertyChangeListener("date",
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (filterDateTo.getDate().compareTo(
								filterDateFrom.getDate()) < 0) {
							filterDateTo.setDate(filterDateFrom.getDate());
						}
						filterDateTo.setMinSelectableDate(filterDateFrom
								.getDate());
					}
				});

		final JLabel lbTo = new JLabel("по");
		panel.add(lbTo);

		filterDateTo = new JDateChooser(Calendar.getInstance().getTime());
		filterDateTo.setMinSelectableDate(filterDateFrom.getDate());
		panel.add(filterDateTo, "w 150::,wrap");

		comboPeriodType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				int index = box.getSelectedIndex();
				filterDateFrom.setVisible(index > 0);
				filterDateTo.setVisible(index > 1);
				lbFrom.setVisible(index > 1);
				lbTo.setVisible(index > 1);
				filterDateTo.setDate(filterDateFrom.getDate());
			}
		});

		panel.add(new JLabel("Официант:"));
		comboWaiters = new JComboBox();
		comboWaiters.addItem("Все");
		Collection<User> waiters = userService.getAllNonAdmins();
		for (User user : waiters) {
			comboWaiters.addItem(new WaiterWrapper(user));
		}
		comboWaiters.setEditable(false);
		panel.add(comboWaiters, "growx, span 3");

		panel.add(new JLabel());

		btnFilterOrders = new JButton("Отобразить");
		btnFilterOrders.setIcon(AbstractForm.createImageIcon("view_table.png"));
		filterOrdersListener = new FilterOrdersListener();
		btnFilterOrders.addActionListener(filterOrdersListener);
		panel.add(btnFilterOrders, "growx");

		add(panel, "wrap");
	}

	/**
	 * Creating panel with orders table
	 */
	private void createOrdersPanel() {
		ordersPanel = new OrdersViewPanel(null, false, false);

		add(ordersPanel, "wrap");
	}

	/**
	 * Creating panel to display orders report
	 */
	private void createReportPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 5", "[grow,fill][]", ""));

		modelOrderReport = new TableModelOrderReport();
		JTable tableOrderReport = new JTable(modelOrderReport);
		tableOrderReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableOrderReport.setDefaultRenderer(String.class,
				new DefaultTableCellRenderer() {
					private static final long serialVersionUID = 1L;

					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component rez = super
								.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, column);
						if (0 == row) {
							rez.setFont(new Font("", Font.BOLD, 12));
						}
						if (3 == row) {
							rez.setForeground(Color.RED);
						} else {
							rez.setForeground(Color.BLACK);
						}
						return rez;
					}
				});

		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(BorderFactory.createTitledBorder("Отчет"));
		scroll.setViewportView(tableOrderReport);
		panel.add(scroll, "h 140!");

		JButton btnPrintReport = new JButton("Печать");
		btnPrintReport.setToolTipText("Распечатать отчет");
		btnPrintReport.setIcon(AbstractForm.createImageIcon("fileprint.png"));
		panel.add(btnPrintReport, "wrap");
		btnPrintReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append("<html>Распечатать отчет за <b>").append(
						report.getFromDateStr());
				if (StringUtils.isNotEmpty(report.getToDateStr())) {
					sb.append(" - ").append(report.getToDateStr());
				}
				sb.append("</b> ?</html>");

				int rez = JOptionPane
						.showConfirmDialog(OrdersEditPanel.this, sb.toString(),
								"Печатаем отчет?", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (JOptionPane.YES_OPTION == rez) {
					printService.printOrderReport(report);
				} 
			}
		});

		add(panel, "wrap");
		updateOrderReport();

	}

	/**
	 * Generating order report and update labels to it
	 */
	private void updateOrderReport() {
		Calendar from = null;
		Calendar to = null;
		switch (comboPeriodType.getSelectedIndex()) {
		case 2:
			to = filterDateTo.getCalendar();
		case 1:
			from = filterDateFrom.getCalendar();
			break;
		}
		report = new OrderReport(from, to);
		
		switch (comboStatus.getSelectedIndex()) {
		case 1:
			report.setOnlyClosed(true);
			break;
		case 2:
			report.setOnlyClosed(false);
			break;
		case 3:
			report.setOnlyDeleted(true);
			break;
		case 4:
			report.setOnlyDeleted(false);
			break;
		}	
		
		User waiter = null;
		if (comboWaiters.getSelectedIndex() > 0) {
			waiter = ((WaiterWrapper) comboWaiters.getSelectedItem()).waiter;
		}
		
		report.setWaiter(waiter);
		
		int orderCount = ordersPanel.getOrdersModel().getRowCount();
		for (int i = 0; i < orderCount; i++) {
			Order order = ordersPanel.getOrdersModel().getOrderAt(i);
			report.addOrder(order);
		}
		modelOrderReport.setReport(report);
	}

	/**
	 * Wrapper for waiter to store then in the comboBox
	 * 
	 * @author Yuriy Tkach
	 */
	private class WaiterWrapper {
		User waiter;

		WaiterWrapper(User waiter) {
			this.waiter = waiter;
		}

		public String toString() {
			return waiter.getFullName();
		}
	}

	/**
	 * Clicking of filter order button after order is deleted
	 * 
	 * @param e
	 *            event object
	 */
	@EventSubscriber(eventClass = OrderDeletedEvent.class)
	public void onOrderDeletedEvent(OrderDeletedEvent e) {
		filterOrdersListener.actionPerformed(null);
	}

	/**
	 * Listener for filter orders button
	 * 
	 * @author Yuriy Tkach
	 */
	private class FilterOrdersListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Getting if closed/open orders should be displayed
			Boolean closed = null;
			Boolean forDeletion = null;
			switch (comboStatus.getSelectedIndex()) {
			case 0:
				closed = null;
				break;
			case 1:
				closed = true;
				break;
			case 2:
				closed = false;
				break;
			case 3:
				forDeletion = true;
				break;
			case 4:
				forDeletion = false;
				break;
			}

			// Getting waiter
			User waiter = null;
			if (comboWaiters.getSelectedIndex() > 0) {
				waiter = ((WaiterWrapper) comboWaiters.getSelectedItem()).waiter;
			}

			Calendar from = null;
			Calendar to = null;
			switch (comboPeriodType.getSelectedIndex()) {
			case 2:
				to = Calendar.getInstance();
				to.setTime(filterDateTo.getDate());
				// There is no break, because from date also should be set
			case 1:
				from = Calendar.getInstance();
				from.setTime(filterDateFrom.getDate());
				break;
			}

			ordersPanel.filterOrders(from, to, waiter, closed,forDeletion);
			updateOrderReport();
		}
	}

}
