package ua.cn.yet.waiter.service.print.lkt210;

import java.awt.Font;
import java.awt.Graphics2D;

import ua.cn.yet.waiter.model.LoggedChange;
import ua.cn.yet.waiter.model.Order;

public class OrderChangesPrinter extends LKT210Printer{
	
	private Order order;
	
	public OrderChangesPrinter(Order order) {
		super();
		this.order = order;
	}

	@Override
	protected int getMaxPageCount() {
		return 1;
	}

	@Override
	protected int printHeader(Graphics2D g2d, int line, int pageWidth) {
		g2d.setFont(new Font("", Font.PLAIN, 10));
		int lineHeight = g2d.getFontMetrics().getHeight();
		
		StringBuilder sb = new StringBuilder("Изменения заказа № ");
		sb.append(order.getId());
		sb.append(", официант: ");
		sb.append(order.getWaiter().getFullName());
		g2d.drawString(sb.toString(), 0, line);
		
		line += (int) (lineHeight * 0.5);
		g2d.drawLine(0, line, pageWidth, line);
		line+=lineHeight;
		
		return line;
	}

	@Override
	protected void printPage(int page, Graphics2D g2d, int line, int pageWidth) {
		int lineHeight = g2d.getFontMetrics().getHeight();
		for(LoggedChange loggedChange: order.getChanges()){
			StringBuilder sb = new StringBuilder();
			sb.append(formatDateTime(loggedChange.getTime().getTime(), false));
			sb.append(": "+loggedChange.getItemName()+", ");
			sb.append(loggedChange.getMessage());
			g2d.drawString(sb.toString(), 0, line);
			line += lineHeight;
		}
	}

	@Override
	protected boolean willPrint() {
		return !order.getChanges().isEmpty();
	}

}
