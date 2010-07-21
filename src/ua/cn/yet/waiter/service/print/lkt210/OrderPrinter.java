package ua.cn.yet.waiter.service.print.lkt210;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderedItem;

/**
 * <p>
 * Class that prints an order for personnel on the LK-T210 thermal printer.
 * </p>
 * 
 * @author Yuriy Tkach
 */
class OrderPrinter extends LKT210Printer {

	private Order order;
	private ItemType itemType;
	private Collection<OrderedItem> items;
	private String title;

	OrderPrinter(Order order, ItemType type) {
		this.order = order;
		this.itemType = type;
		
		switch (itemType) {
		case FOOD:
			items = order.getItemsForCook();
			title = "Кухня";
			break;
		case BAR:
		case SOFT_DRINK:
			items = order.getItemsForBar();
			title = "Бар";
			break;
		case ALCOHOL:
			items = order.getItemsForAlcohol();
			title = "Бар алкоголь";
			break;
		}
	}
	
	/* (non-Javadoc)
	 * @see ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#willPrint()
	 */
	@Override
	protected boolean willPrint() {
		return !items.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.service.print.lkt210.LKT210Printer#printPage(int,
	 * java.awt.Graphics2D, int, int)
	 */
	@Override
	protected void printPage(int page, Graphics2D g2d, int line, int pageWidth) {
		printOrderItems(title, items, g2d, line, pageWidth);
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
		
		StringBuilder sb = new StringBuilder("Заказ № ");
		sb.append(order.getId());
		sb.append(" от ");
		sb.append(formatDateTime(order.getCreationDate().getTime(), false));

		g2d.drawString(sb.toString(), 0, line);
		line += lineHeight;

		g2d.setFont(new Font("", Font.ITALIC, 8));
		g2d.drawString(order.getWaiter().getFullName(), 0, line);
		line += (int) (lineHeight * 1.2);
		return line;
	}

	/**
	 * Printing order items with title
	 * 
	 * @param title
	 *            title of the section
	 * @param items
	 *            items to print
	 * @param pageFormat
	 *            page format
	 * @param g2d
	 *            graphics to print to
	 * @param line
	 *            line position where to print (Basically it is the Y
	 *            coordinate)
	 * @return new line position
	 */
	private int printOrderItems(String title, Collection<OrderedItem> items,
			Graphics2D g2d, int line, int pageWidth) {

		if (items.isEmpty()) {
			return line;
		}

		g2d.setFont(new Font("", Font.BOLD, 9));
		g2d.drawString(title, 0, line);
		g2d.setFont(new Font("", Font.PLAIN, 8));

		int lineHeight = g2d.getFontMetrics().getHeight();
		line += (int) (lineHeight * 0.5);
		g2d.drawLine(0, line, pageWidth, line);

		for (OrderedItem item : items) {
			line += lineHeight;

			StringBuilder itemName = new StringBuilder();
			StringBuilder itemInfo = new StringBuilder();
			
			int nameAbbreviate;
			
			if (item.getMass() != item.getNewMass()) {
				itemInfo.append(item.getNewMass());
				if (item.isLiquid()) {
					nameAbbreviate = 34;
					itemInfo.append("мг - ");
				} else {
					nameAbbreviate = 35;
					itemInfo.append("г - ");
				}
			} else {
				nameAbbreviate = 39;
			}
			itemInfo.append(item.getCount());
			
			itemName.append(StringUtils.abbreviate(item.getName(), nameAbbreviate));

			drawLeftDotsRight(g2d, itemName.toString(), itemInfo.toString(),
					line, pageWidth);
		}

		line += (int) (lineHeight * 1.5);

		return line;
	}
}