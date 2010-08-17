package ua.cn.yet.waiter.service.print.lkt210;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.util.Config;

/**
 * Abstract class that provides handy methods for printing order or receipt on
 * the printer.
 * <p>
 * Information about that printer can be found here: <a href="http://www.miniprinter.com/new/english/product/product_detail.php?pn=&sn=&sn2=&product_first=POS%20Printer&product_second=Thermal&sequent=&keyfield=&search_key=&id_va=34"
 * > LK-T210</a>. The Window$ drivers can downloaded from their site. For Linux
 * drivers you should contact them. They will email them.
 * </p>
 * <p>
 * As it was empirically determined, the printer page size is 204x841 px. Java
 * sets big margins, so the clipping region should be specified manually. Also,
 * the page size is not transferred correctly from print dialog, so it also
 * should be set manually.
 * </p>
 * 
 * @author Yuriy Tkach
 */
abstract class LKT210Printer implements Printable {

	private static final int MARGIN = 2;
	private static final int PAGE_HEIGHT = 841;
	private static final int PAGE_WIDTH = 204;

	static Log log = LogFactory.getLog(OrderPrinter.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 * java.awt.print.PageFormat, int)
	 */
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int page)
			throws PrinterException {

		if (page >= getMaxPageCount()) {
			return NO_SUCH_PAGE;
		}
		
		if (!willPrint()) {
			return NO_SUCH_PAGE;
		}

		Graphics2D g2d = (Graphics2D) graphics;

		if (Config.getBoolean(Config.SET_PRINT_CLIPPING)) {
			setupClippingAndPage(pageFormat, g2d);
		}

		/*
		 * User's (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping.
		 */
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		g2d.setFont(new Font("", Font.PLAIN, 10));
		int line = g2d.getFontMetrics().getHeight();

		int pageWidth = (int) pageFormat.getImageableWidth();

		line = printHeader(g2d, line, pageWidth);

		printPage(page, g2d, line, pageWidth);

		/* tell the caller that this page is part of the printed document */
		return PAGE_EXISTS;
	}

	/**
	 * @param date
	 *            Date object to format
	 * @return Formatted date and time
	 */
	public static String formatDateTime(Date date, boolean full) {
		if (null == date) {
			return "";
		}

		int dateFormat;
		int timeFormat;
		if (full) {
			dateFormat = DateFormat.MEDIUM;
			timeFormat = DateFormat.MEDIUM;
		} else {
			dateFormat = DateFormat.SHORT;
			timeFormat = DateFormat.SHORT;
		}

		try {
			DateFormat df = DateFormat.getDateTimeInstance(dateFormat,
					timeFormat);
			return df.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Setting up clipping region and page for the graphics object
	 * 
	 * @param pageFormat
	 *            Page format to use
	 * @param g2d
	 *            graphics object to use
	 */
	protected void setupClippingAndPage(PageFormat pageFormat, Graphics2D g2d) {
		if (log.isTraceEnabled()) {
			log.trace("Passed clipping: " + g2d.getClip());
		}

		/*
		 * Setting clipping to the new range for printer, leaving 2px border on
		 * all sides
		 */
		g2d.setClip(MARGIN, MARGIN, PAGE_WIDTH - (MARGIN * 2), PAGE_HEIGHT
				- (MARGIN * 2));

		if (log.isTraceEnabled()) {
			log.trace(String.format("Passed page size: w - %f; h - %f",
					pageFormat.getPaper().getWidth(), pageFormat.getPaper()
							.getHeight()));
		}

		/*
		 * Setting paper size to the size of the printer's paper. Also setting
		 * imageable area to the same values as clipping for graphics, so all
		 * that area will be used for printing.
		 */
		Paper p = new Paper();
		p.setSize(PAGE_WIDTH, PAGE_HEIGHT);
		p.setImageableArea(MARGIN, MARGIN, PAGE_WIDTH - (MARGIN * 2),
				PAGE_HEIGHT - (MARGIN * 2));
		pageFormat.setPaper(p);
	}

	/**
	 * Drawing left string, then right string and dots between them.
	 * 
	 * @param g2d
	 *            graphics to draw on
	 * @param leftString
	 *            left string to draw
	 * @param rightString
	 *            right string to draw
	 * @param line
	 *            line where to draw strings (Y coordinate)
	 * @param pageWidth
	 *            width of the page
	 */
	protected void drawLeftDotsRight(Graphics2D g2d, String leftString,
			String rightString, int line, int pageWidth) {
		int widLeftString = g2d.getFontMetrics().charsWidth(
				leftString.toCharArray(), 0, leftString.length());
		g2d.drawString(leftString, 0, line);
		int posLeftString = widLeftString + 2;

		int widRightString = g2d.getFontMetrics().charsWidth(
				rightString.toCharArray(), 0, rightString.length());
		int posRightString = pageWidth - widRightString;

		g2d.drawString(rightString, posRightString, line);

		String dot = ".";
		int widDot = g2d.getFontMetrics().charsWidth(dot.toCharArray(), 0, 1);
		int dotCount = (int)((posRightString - posLeftString) / (widDot*1.2));
		if (dotCount > 0) {
			StringBuilder dots = new StringBuilder(dotCount);
			for (int i = 0; i < dotCount; i++) {
				dots.append('.');
			}
			g2d.drawString(dots.toString(), posLeftString, line);
		}
	}

	/**
	 * Drawing string centered on the page
	 * 
	 * @param g2d
	 *            graphics to draw on
	 * @param string
	 *            string to draw
	 * @param line
	 *            line where to draw
	 * @param pageWidth
	 *            width of the page
	 */
	protected void drawCentered(Graphics2D g2d, String string, int line,
			int pageWidth) {
		int widString = g2d.getFontMetrics().charsWidth(string.toCharArray(),
				0, string.length());

		int drawPos = (pageWidth - widString) / 2;
		g2d.drawString(string, drawPos, line);
	}

	/**
	 * @return max page count that will be printed
	 */
	protected abstract int getMaxPageCount();
	
	
	/**
	 * @return true if printer has something to print and will do that
	 */
	protected abstract boolean willPrint();

	/**
	 * Printing single page on the specified line
	 * 
	 * @param page
	 *            page to print
	 * @param g2d
	 *            graphics where to print
	 * @param line
	 *            line where to start printing
	 * @param pageWidth
	 *            width of the page
	 */
	protected abstract void printPage(int page, Graphics2D g2d, int line,
			int pageWidth);

	/**
	 * Printing page header with order information
	 * 
	 * @param g2d
	 *            graphics to print to
	 * @param line
	 *            Line where to start printing (the Y coordinate)
	 * @param pageWidth
	 *            width of the page
	 * @return New line position
	 */
	protected abstract int printHeader(Graphics2D g2d, int line, int pageWidth);

}
