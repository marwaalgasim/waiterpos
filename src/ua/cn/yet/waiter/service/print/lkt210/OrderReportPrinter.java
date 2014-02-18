package ua.cn.yet.waiter.service.print.lkt210;

import java.awt.Font;
import java.awt.Graphics2D;

import org.apache.commons.lang.StringUtils;

import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.model.OrderReport;
import ua.cn.yet.waiter.util.Config;

/**
 * Printer to print order reports
 * 
 * @author Yuriy Tkach
 */
class OrderReportPrinter extends LKT210Printer {

	private final OrderReport report;

	public OrderReportPrinter(OrderReport report) {
		this.report = report;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#getMaxPageCount()
	 */
	@Override
	protected int getMaxPageCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#printHeader(java.
	 * awt.Graphics2D, int, int)
	 */
	@Override
	protected int printHeader(Graphics2D g2d, int line, int pageWidth) {
		g2d.setFont(new Font("", Font.PLAIN, 10));
		int lineHeight = g2d.getFontMetrics().getHeight();

		drawCentered(g2d, Config.getBundleValue("institution.name"), line,
				pageWidth);
		line += lineHeight;

		StringBuilder sb = new StringBuilder();
		sb.append("Отчет за ").append(report.getFromDateStr());
		if (StringUtils.isNotEmpty(report.getToDateStr())) {
			sb.append(" - ").append(report.getToDateStr());
		}
		drawCentered(g2d, sb.toString(), line, pageWidth);

		g2d.setFont(new Font("", Font.PLAIN, 8));
		line += lineHeight;

		sb = new StringBuilder();
		sb.append("Заказы: ");
		if (report.isAllIncluded()) {
			sb.append("Все");
		} else if (report.getOnlyClosed() != null) {

			if (report.getOnlyClosed()) {
				sb.append("Закрытые");
			} else {
				sb.append("Открытые");
			}
		} else if (report.getOnlyDeleted() != null) {

			if (report.getOnlyDeleted()) {
				sb.append("Удаленные/отмененные");
			} else {
				sb.append("Без удаленных/отмененных");
			}
		}

		drawCentered(g2d, sb.toString(), line, pageWidth);
		line += lineHeight;

		sb = new StringBuilder();
		sb.append("Официант: ");
		if (report.isAllWaitersIncluded()) {
			sb.append("Все");
		} else {
			sb.append(report.getWaiter().getFullName());
		}
		drawCentered(g2d, sb.toString(), line, pageWidth);
		line += lineHeight;

		line += (int) (lineHeight * 0.5);
		g2d.drawLine(0, line, pageWidth, line);
		line += (int) (lineHeight * 1.2);
		return line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#printPage(int,
	 * java.awt.Graphics2D, int, int)
	 */
	@Override
	protected void printPage(int page, Graphics2D g2d, int line, int pageWidth) {
		g2d.setFont(new Font("", Font.PLAIN, 8));
		int lineHeight = g2d.getFontMetrics().getHeight();

		String ordersStr = report.getTotalOrdersStr();
		if ((report.isClosedAndOpenIncluded())
				&& (report.getTotalOpenOrders() > 0)) {
			StringBuilder sb = new StringBuilder();
			sb.append("(Открытых: ").append(report.getTotalOpenOrders())
					.append(")  ").append(ordersStr);
			ordersStr = sb.toString();
		}
		drawLeftDotsRight(g2d, "Заказов", ordersStr, line, pageWidth);
		line += lineHeight;

		line += lineHeight;

		// Printing totals for types

		String totalsStr = report.getTotalsForTypeStr(ItemType.FOOD);
		if ((report.isClosedAndOpenIncluded())
				&& (report.getTotalOpenOrders() > 0)) {
			StringBuilder sb = new StringBuilder();
			sb.append("(")
					.append(report.getTotalsForTypeOpenOrdersStr(ItemType.FOOD))
					.append(")  ").append(totalsStr);
			totalsStr = sb.toString();
		}
		drawLeftDotsRight(g2d, "Кухня", totalsStr, line, pageWidth);
		line += lineHeight;

		totalsStr = report.getTotalsForBarStr();
		if ((report.isClosedAndOpenIncluded())
				&& (report.getTotalOpenOrders() > 0)) {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(report.getTotalsForBarOpenOrdersStr())
					.append(")  ").append(totalsStr);
			totalsStr = sb.toString();
		}
		drawLeftDotsRight(g2d, "Бар", totalsStr, line, pageWidth);
		line += lineHeight;

		totalsStr = report.getTotalsForTypeStr(ItemType.ALCOHOL);
		if ((report.isClosedAndOpenIncluded())
				&& (report.getTotalOpenOrders() > 0)) {
			StringBuilder sb = new StringBuilder();
			sb.append("(")
					.append(report
							.getTotalsForTypeOpenOrdersStr(ItemType.ALCOHOL))
					.append(")  ").append(totalsStr);
			totalsStr = sb.toString();
		}
		drawLeftDotsRight(g2d, "Алкоголь", totalsStr, line, pageWidth);
		line += lineHeight;

		// Printing line and subtotals

		g2d.setFont(new Font("", Font.BOLD, 8));
		lineHeight = g2d.getFontMetrics().getHeight();

		line += (int) (lineHeight * 0.5);
		g2d.drawLine(0, line, pageWidth, line);
		line += lineHeight;

		totalsStr = report.getTotalSumStr();
		if ((report.isClosedAndOpenIncluded())
				&& (report.getTotalOpenOrders() > 0)) {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(report.getTotalSumOpenOrdersStr())
					.append(")  ").append(totalsStr);
			totalsStr = sb.toString();
		}
		drawLeftDotsRight(g2d, "ИТОГО:", totalsStr, line, pageWidth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#willPrint()
	 */
	@Override
	protected boolean willPrint() {
		return true;
	}
}
