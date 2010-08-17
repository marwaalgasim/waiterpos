package ua.cn.yet.waiter.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;
import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.model.OrderReport;
import ua.cn.yet.waiter.service.OrderService;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.util.WaiterInstance;

public class ReportForDayDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel lbDate;
	private JLabel lbOrderCount;
	private JLabel lbSumFood;
	private JLabel lbSumBar;
	private JLabel lbSumAlcohol;
	private JLabel lbSumTotal;
	private JRadioButton radioToday;
	private JRadioButton radioYesterday;
	private OrderService orderService;
	private JLabel lbOrderCountOpen;
	private JLabel lbSumFoodOpen;
	private JLabel lbSumBarOpen;
	private JLabel lbSumAlcoholOpen;
	private JLabel lbSumTotalOpen;
	private PrintingService printService;
	private OrderReport report;

	public ReportForDayDialog(Window parent) {
		super(parent, "Отчет за день", ModalityType.APPLICATION_MODAL);

		orderService = WaiterInstance.forId(WaiterInstance.ORDER_SERVICE);
		printService = WaiterInstance.forId(WaiterInstance.PRINTING_SERVICE);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		makeUI();
		
		radioToday.setSelected(true);
		updateReport();

		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Creating UI components for the dialog
	 */
	private void makeUI() {
		setLayout(new MigLayout("insets 8", "[fill,grow][center,grow][center,grow]", "[]"));

		createRangeBlock();
		createReportBlock();
		createButtonBlock();
	}

	/**
	 * Range selection block
	 */
	private void createRangeBlock() {
		radioToday = new JRadioButton("Сегодня");
		radioYesterday = new JRadioButton("Вчера");
		
		KeyListener escKeyListener = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					ReportForDayDialog.this.dispose();
				}
			}
		};
		
		radioToday.addKeyListener(escKeyListener);
		radioYesterday.addKeyListener(escKeyListener);
		
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateReport();
			}
		};
		radioToday.addActionListener(actionListener);
		radioYesterday.addActionListener(actionListener);

		ButtonGroup group = new ButtonGroup();
		group.add(radioToday);
		group.add(radioYesterday);

		this.add(new JLabel());
		this.add(radioToday);
		this.add(radioYesterday, "wrap");

		this.add(new JSeparator(), "span, wrap");
	}

	/**
	 * Report labels
	 */
	private void createReportBlock() {
		Font fontBold = new Font("", Font.BOLD, 12);
		Color openOrderColor = new Color(0, 0, 100);

		this.add(new JLabel("Дата:"));
		lbDate = new JLabel();
		this.add(lbDate, "span, wrap");
		
		this.add(new JLabel());
		JLabel lbAll = new JLabel("Всех");
		lbAll.setFont(fontBold);
		this.add(lbAll);
		JLabel lbOpen = new JLabel("Открытых");
		lbOpen.setForeground(openOrderColor);
		lbOpen.setFont(fontBold);
		this.add(lbOpen, "wrap");

		this.add(new JLabel("Заказов:"));
		lbOrderCount = new JLabel();
		this.add(lbOrderCount);
		lbOrderCountOpen = new JLabel();
		lbOrderCountOpen.setForeground(openOrderColor);
		this.add(lbOrderCountOpen, "wrap");
		
		// Small workaround to have width of dialog as needed
		JLabel lbStub1 = new JLabel("9999.99 www.");
		lbStub1.setForeground(this.getBackground());
		this.add(lbStub1);
		JLabel lbStub2 = new JLabel("9999.99 www.");
		lbStub2.setForeground(this.getBackground());
		this.add(lbStub2);
		JLabel lbStub3 = new JLabel("9999.99 www.");
		lbStub3.setForeground(this.getBackground());
		this.add(lbStub3,"wrap");

		this.add(new JLabel("Кухня:"));
		lbSumFood = new JLabel();
		this.add(lbSumFood);
		lbSumFoodOpen = new JLabel();
		lbSumFoodOpen.setForeground(openOrderColor);
		this.add(lbSumFoodOpen, "wrap");

		this.add(new JLabel("Бар:"));
		lbSumBar = new JLabel();
		this.add(lbSumBar);
		lbSumBarOpen = new JLabel();
		lbSumBarOpen.setForeground(openOrderColor);
		this.add(lbSumBarOpen, "wrap");

		this.add(new JLabel("Алкоголь:"));
		lbSumAlcohol = new JLabel();
		this.add(lbSumAlcohol);
		lbSumAlcoholOpen = new JLabel();
		lbSumAlcoholOpen.setForeground(openOrderColor);
		this.add(lbSumAlcoholOpen, "wrap");
		
		this.add(new JLabel(""), "wrap");

		this.add(new JLabel("Всего:"));
		lbSumTotal = new JLabel();
		lbSumTotal.setFont(fontBold);
		this.add(lbSumTotal);
		lbSumTotalOpen = new JLabel();
		lbSumTotalOpen.setFont(fontBold);
		lbSumTotalOpen.setForeground(openOrderColor);
		this.add(lbSumTotalOpen, "wrap");
		
		this.add(new JSeparator(), "span, wrap");
	}

	/**
	 * Buttons for the dialog
	 */
	private void createButtonBlock() {
		this.add(new JLabel());
		
		JButton btnPrint = new JButton("Печать");
		this.add(btnPrint);
		btnPrint.setIcon(AbstractForm.createImageIcon("fileprint.png"));
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (report != null) {
					printService.printOrderReport(report);
				}
			}
		});

		JButton btnClose = new JButton("Закрыть");
		this.add(btnClose);
		btnClose.setIcon(AbstractForm.createImageIcon("no.png"));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportForDayDialog.this.dispose();
			}
		});
	}

	private void updateReport() {
		Calendar date = Calendar.getInstance();
		if (radioYesterday.isSelected()) {
			date.add(Calendar.DAY_OF_YEAR, -1);
		}

		report = orderService.getOrderReport(date, null, null, null, false);

		lbDate.setText(report.getFromDateStr());
		lbOrderCount.setText(report.getTotalOrdersStr());
		lbOrderCountOpen.setText(report.getTotalOpenOrdersStr());
		
		lbSumAlcohol.setText(report.getTotalsForTypeStr(ItemType.ALCOHOL));
		lbSumBar.setText(report.getTotalsForBarStr());
		lbSumFood.setText(report.getTotalsForTypeStr(ItemType.FOOD));
		lbSumTotal.setText(report.getTotalSumStr());
		
		if (report.getTotalOpenOrders() > 0) {
			lbSumAlcoholOpen.setText(report.getTotalsForTypeOpenOrdersStr(ItemType.ALCOHOL));
			lbSumBarOpen.setText(report.getTotalsForBarOpenOrdersStr());
			lbSumFoodOpen.setText(report.getTotalsForTypeOpenOrdersStr(ItemType.FOOD));
			lbSumTotalOpen.setText(report.getTotalSumOpenOrdersStr());
		} else {
			lbSumAlcoholOpen.setText("");
			lbSumBarOpen.setText("");
			lbSumFoodOpen.setText("");
			lbSumTotalOpen.setText("");
		}
	}

}
