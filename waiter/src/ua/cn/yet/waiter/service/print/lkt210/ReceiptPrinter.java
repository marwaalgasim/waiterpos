package ua.cn.yet.waiter.service.print.lkt210;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderedItem;

/**
 * Class that prints receipt from the order to the client
 * 
 * @author Yuriy Tkach
 */
class ReceiptPrinter extends LKT210Printer {

	private Order order;

	ReceiptPrinter(Order order) {
		this.order = order;
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

		drawCentered(g2d, "Бар \"Сова\"", line, pageWidth);
		line += lineHeight;

		StringBuilder sb = new StringBuilder();
		sb.append("Заказ №").append(order.getId());

		drawCentered(g2d, sb.toString(), line, pageWidth);

		g2d.setFont(new Font("", Font.PLAIN, 8));
		lineHeight = g2d.getFontMetrics().getHeight();

		line += (int) (lineHeight * 0.5);
		g2d.drawLine(0, line, pageWidth, line);
		line += lineHeight;

		drawCentered(g2d, "Официант: " + order.getWaiter().getFullName(), line,
				pageWidth);
		line += lineHeight;

		drawCentered(g2d,
				formatDateTime(Calendar.getInstance().getTime(), true), line,
				pageWidth);

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

		for (OrderedItem item : order.getItems()) {
			line += lineHeight;

			StringBuilder itemName = new StringBuilder();
			itemName.append(StringUtils.abbreviate(item.getName(), 55));

			g2d.drawString(itemName.toString(), 0, line);
			line += lineHeight;

			StringBuilder itemInfo = new StringBuilder();
			itemInfo.append(item.getCount());
			itemInfo.append(" x ");
			if (item.getMass() != item.getNewMass()) {
				itemInfo.append(item.getNewMass());
				itemInfo.append("мг");
				itemInfo.append(" x ");
			}
			
			itemInfo.append(String.format("%.2f", item.getPriceBillsAndCoins()));
			itemInfo.append(" грн.");

			StringBuilder price = new StringBuilder();
			price.append(String.format("%.2f", item
					.getOrderedPriceBillAndCoins()));
			price.append(" грн.");

			drawLeftDotsRight(g2d, itemInfo.toString(), price.toString(),
					line, pageWidth);
		}	
		
		if (order.getDiscount() > 0) {
			
			line += lineHeight*2;
			
			StringBuilder actualPrice = new StringBuilder();
			actualPrice.append(String.format("%.2f", order.getSum(false)));
			actualPrice.append(" грн.");
			drawLeftDotsRight(g2d, "Сумма без скидки", actualPrice.toString(), line, pageWidth);
			
			line += lineHeight;
		
			StringBuilder discount = new StringBuilder();
			discount.append(String.format("%.2f грн. (%.0f", 
					order.getSum(false)*order.getDiscount(), order.getDiscount()*100));
			discount.append("%)");
			drawLeftDotsRight(g2d, "Скидка", discount.toString(), line, pageWidth);
		
		}

		
		g2d.setFont(new Font("", Font.BOLD, 8));
		lineHeight = g2d.getFontMetrics().getHeight();

		line += (int) (lineHeight * 0.5);
		g2d.drawLine(0, line, pageWidth, line);
		line += lineHeight;
		
		StringBuilder totalPrice = new StringBuilder();
		totalPrice.append(String.format("%.2f", order.getSum(true)));
		totalPrice.append(" грн.");

		drawLeftDotsRight(g2d, "ИТОГО:", totalPrice.toString(), line, pageWidth);
	}

	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#willPrint()
	 */
	@Override
	protected boolean willPrint() {
		return true;
	}

}
