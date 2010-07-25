package ua.cn.yet.waiter.service.print.lkt210;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderReport;
import ua.cn.yet.waiter.service.PrintingService;
import ua.cn.yet.waiter.util.Config;

/**
 * Print service that will print to termal printer LK-T210
 * 
 * @author Yuriy Tkach
 */
public class PrintServiceLKT210 implements PrintingService {

	protected final Log log = LogFactory.getLog(this.getClass());

	/**
	 * Printing printable object with showing print dialog
	 * 
	 * @param printable
	 *            Printable to print
	 * 
	 * @return true if printing was successful
	 */
	private boolean printPrintable(Printable printable) {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(printable);

		boolean showPrintDialog = Config.getBoolean(Config.SHOW_PRINT_DIALOG);
		boolean doPrint = true;
		if (showPrintDialog) {
			doPrint = job.printDialog();
		}

		if (doPrint) {
			try {
				job.print();
				return true;
			} catch (PrinterException e) {
				log.error("Failed to print.", e);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.PrintingService#printOrder(ua.cn.yet.waiter.
	 * model.Order)
	 */
	@Override
	public boolean printOrder(Order order) {
		if (log.isDebugEnabled()) {
			log.debug("Printing " + order);
		}

		OrderPrinter orderPrinter = new OrderPrinter(order, ItemType.FOOD);
		if (orderPrinter.willPrint()) {
			printPrintable(orderPrinter);
		}

		orderPrinter = new OrderPrinter(order, ItemType.BAR);
		if (orderPrinter.willPrint()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			printPrintable(orderPrinter);
		}

		orderPrinter = new OrderPrinter(order, ItemType.ALCOHOL);
		if (orderPrinter.willPrint()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			printPrintable(orderPrinter);
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.PrintingService#printReceipt(ua.cn.yet.waiter
	 * .model.Order)
	 */
	@Override
	public boolean printReceipt(Order order) {
		if (log.isDebugEnabled()) {
			log.debug("Printing receipt for " + order);
		}
		return printPrintable(new ReceiptPrinter(order));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.PrintingService#printOrderReport(ua.cn.yet.waiter
	 * .model.OrderReport)
	 */
	@Override
	public boolean printOrderReport(OrderReport report) {
		if (log.isDebugEnabled()) {
			log.debug("Printing " + report);
		}
		return printPrintable(new OrderReportPrinter(report));
	}

	@Override
	public boolean printOrderChanges(Order order) {
		if (log.isDebugEnabled()) {
			log.debug("Printing order changes for order " + order.getId());
		}
		return printPrintable(new OrderChangesPrinter(order));
	}

}
