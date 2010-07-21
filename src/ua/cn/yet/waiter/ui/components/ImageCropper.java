package ua.cn.yet.waiter.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * <b>Changes since original: </b> Added fixedSquareRation to be able to crop
 * fixed squares. Removed the mouse wheel listener, so the zoom will be removed,
 * because it is untested yet.
 * </p>
 * 
 * @author Christophe Le Besnerais (Original Author)
 * @author Yuriy Tkach (Minor enhancements)
 */
public class ImageCropper extends JLayeredPane {

	private static final long serialVersionUID = 1L;

	private BufferedImage image;
	private BufferedImage thumbnail;
	private BufferedImage croppedImage;
	private JPanel background;
	private JPanel croppingPanel;
	private JPanel resizePanel;
	private Rectangle crop;
	private float zoom = 1.0f;
	private int whellZoomAmount = 5;
	private BufferedImageOp filter;
	private transient ChangeEvent changeEvent;

	/** Specifies if the fixed square ration for cropper is used */
	private boolean fixedSquareRation;

	public ImageCropper(BufferedImage image, boolean fixedSquareRatio) {
		InnerListener listener = new InnerListener();
		this.addComponentListener(listener);
		this.image = image;

		this.fixedSquareRation = fixedSquareRatio;

		// this.crop = new Rectangle(image.getWidth() / 5, image.getHeight() /
		// 5,
		// 3 * image.getWidth() / 5, 3 * image.getHeight() / 5);

		int minSize = Math.min(image.getWidth(), image.getHeight());

		this.crop = new Rectangle(0, 0, minSize / 2, minSize / 2);
		this.croppedImage = image.getSubimage(crop.x, crop.y, crop.width,
				crop.height);

		this.background = new JPanel(true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(getThumbbail(), (this.getWidth() - getThumbbail()
						.getWidth()) / 2, (this.getHeight() - getThumbbail()
						.getHeight()) / 2, null);
			}
		};
		this.add(background, JLayeredPane.DEFAULT_LAYER);

		this.croppingPanel = new JPanel(true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(croppedImage, 0, 0, this.getWidth(), this
						.getHeight(), null);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
			}
		};
		this.croppingPanel.addMouseListener(listener);
		this.croppingPanel.addMouseMotionListener(listener);

		// this.croppingPanel.addMouseWheelListener(listener);

		this.croppingPanel.setCursor(Cursor
				.getPredefinedCursor(Cursor.MOVE_CURSOR));
		this.add(croppingPanel, JLayeredPane.PALETTE_LAYER, 2);

		this.resizePanel = new JPanel(true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(Color.GRAY);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
				g.drawLine(2, 4, 4, 2);
				g.drawLine(2, 7, 7, 2);
				g.drawLine(5, 7, 7, 5);
			}
		};
		this.resizePanel.addMouseListener(listener);
		this.resizePanel.addMouseMotionListener(listener);
		this.resizePanel.setCursor(Cursor
				.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
		this.resizePanel.setSize(10, 10);
		this.add(resizePanel, JLayeredPane.PALETTE_LAYER, 0);
	}

	public BufferedImageOp getFilter() {
		return filter;
	}

	public void setFilter(BufferedImageOp filter) {
		this.filter = filter;
	}

	public BufferedImage getCroppedImage() {
		return croppedImage;
	}

	private BufferedImage getThumbbail() {
		if (thumbnail == null) {
			int width = ImageCropper.this.image.getWidth();
			int height = ImageCropper.this.image.getHeight();
			float ratio = (float) width / (float) height;
			if ((float) this.getWidth() / (float) this.getHeight() > ratio) {
				height = this.getHeight();
				width = (int) (height * ratio);
			} else {
				width = this.getWidth();
				height = (int) (width / ratio);
			}

			thumbnail = getGraphicsConfiguration().createCompatibleImage(width,
					height, ImageCropper.this.image.getTransparency());
			Graphics2D g2d = thumbnail.createGraphics();
			// g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			// RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.drawImage(ImageCropper.this.image, 0, 0, width, height, null);
			g2d.dispose();

			if (filter != null) {
				thumbnail = filter.filter(thumbnail, null);
			}
		}

		return thumbnail;
	}

	private void changePosition() {
		if (crop.x < 0) {
			crop.x = 0;
		}
		if (crop.x > image.getWidth()) {
			crop.x = image.getWidth();
		}
		if (crop.y < 0) {
			crop.y = 0;
		}
		if (crop.y > image.getHeight()) {
			crop.y = image.getHeight();
		}
		if (crop.x + crop.width > image.getWidth()) {
			crop.x = image.getWidth() - crop.width;
		}
		if (crop.y + crop.height > image.getHeight()) {
			crop.y = image.getHeight() - crop.height;
		}

		changeCrop(true);
	}

	private void changeSize() {
		if (crop.width <= 0) {
			crop.width = 1;
		}
		if (crop.height <= 0) {
			crop.height = 1;
		}
		float ratio = (float) crop.width / (float) crop.height;
		if (crop.x + crop.width > image.getWidth()) {
			crop.width = image.getWidth() - crop.x;
			crop.height = (int) (crop.width / ratio);
		}
		if (crop.y + crop.height > image.getHeight()) {
			crop.height = image.getHeight() - crop.y;
			crop.width = (int) (crop.height * ratio);
		}
		if (crop.width >= image.getWidth()) {
			crop.width = image.getWidth();
			crop.height = (int) (crop.width / ratio);
		}
		if (crop.height >= image.getHeight()) {
			crop.height = image.getHeight();
			crop.width = (int) (crop.height * ratio);
		}

		changeCrop(true);
	}

	private void changeZoom() {
		if (crop.x < 0) {
			crop.x = 0;
		}
		if (crop.y < 0) {
			crop.y = 0;
		}
		if (crop.width <= 0) {
			crop.width = 1;
		}
		if (crop.height <= 0) {
			crop.height = 1;
		}
		float ratio = (float) crop.width / (float) crop.height;
		if (crop.x + crop.width > image.getWidth()) {
			crop.width = image.getWidth() - crop.x;
			crop.height = (int) (crop.width / ratio);
		}
		if (crop.y + crop.height > image.getHeight()) {
			crop.height = image.getHeight() - crop.y;
			crop.width = (int) (crop.height * ratio);
		}
		if (crop.width >= image.getWidth()) {
			crop.width = image.getWidth();
			crop.height = (int) (crop.width / ratio);
		}
		if (crop.height >= image.getHeight()) {
			crop.height = image.getHeight();
			crop.width = (int) (crop.height * ratio);
		}

		changeCrop(false);
	}

	private void changeCrop(boolean updatePanel) {
		this.croppedImage = image.getSubimage(crop.x, crop.y, crop.width,
				crop.height);
		this.fireStateChanged();

		if (updatePanel) {
			float ratio = (float) image.getWidth()
					/ (float) getThumbbail().getWidth();
			this.croppingPanel.setSize((int) (zoom * crop.width / ratio),
					(int) (zoom * crop.height / ratio));
			this.croppingPanel.setLocation((int) (crop.x / ratio - crop.width
					/ ratio * (zoom - 1) / 2)
					+ (getWidth() - getThumbbail().getWidth()) / 2,
					(int) (crop.y / ratio - crop.height / ratio * (zoom - 1)
							/ 2)
							+ (getHeight() - getThumbbail().getHeight()) / 2);
			this.resizePanel.setLocation(croppingPanel.getX()
					+ croppingPanel.getWidth() - resizePanel.getWidth() / 2,
					croppingPanel.getY() + croppingPanel.getHeight()
							- resizePanel.getHeight() / 2);
		} else {
			this.croppingPanel.repaint();
		}
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	public ChangeListener[] getChangeListeners() {
		return listenerList.getListeners(ChangeListener.class);
	}

	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	private class InnerListener extends MouseAdapter implements
			ComponentListener, MouseMotionListener, MouseWheelListener {

		private Point cursorPosition;

		public void componentResized(ComponentEvent e) {
			thumbnail = null;
			background.setSize(ImageCropper.this.getSize());
			float ratio = (float) getThumbbail().getWidth()
					/ (float) image.getWidth();
			croppingPanel.setSize((int) (zoom * crop.width * ratio),
					(int) (zoom * crop.height * ratio));
			croppingPanel.setLocation((int) (crop.x * ratio - crop.width
					* ratio * (zoom - 1) / 2)
					+ (getWidth() - getThumbbail().getWidth()) / 2,
					(int) (crop.y * ratio - crop.height * ratio * (zoom - 1)
							/ 2)
							+ (getHeight() - getThumbbail().getHeight()) / 2);
			resizePanel.setLocation(croppingPanel.getX()
					+ croppingPanel.getWidth() - resizePanel.getWidth() / 2,
					croppingPanel.getY() + croppingPanel.getHeight()
							- resizePanel.getHeight() / 2);
			thumbnail = null;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			cursorPosition = e.getPoint();
			setLayer(e.getComponent(), JLayeredPane.DRAG_LAYER);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			cursorPosition = null;
			setLayer(e.getComponent(), JLayeredPane.PALETTE_LAYER);
			setPosition(croppingPanel, 1);
			setPosition(resizePanel, 0);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (cursorPosition != null) {
				if (e.getComponent().equals(croppingPanel)) {
					float ratio = (float) image.getWidth()
							/ (float) getThumbbail().getWidth();
					Point pos = SwingUtilities.convertPoint(croppingPanel, e
							.getPoint(), background);
					pos.translate(-cursorPosition.x, -cursorPosition.y);
					pos.translate(croppingPanel.getWidth() / 2, croppingPanel
							.getHeight() / 2);
					pos.translate(
							-(getWidth() - getThumbbail().getWidth()) / 2,
							-(getHeight() - getThumbbail().getHeight()) / 2);
					pos.setLocation(pos.x * ratio, pos.y * ratio);
					pos.translate(-crop.width / 2, -crop.height / 2);
					crop.setLocation(pos.x, pos.y);
					changePosition();

				} else if (e.getComponent().equals(resizePanel)) {
					float ratio = (float) image.getWidth()
							/ (float) getThumbbail().getWidth();
					Point start = SwingUtilities.convertPoint(resizePanel,
							cursorPosition, background);
					Point end = SwingUtilities.convertPoint(resizePanel, e
							.getPoint(), background);

					int moveX = end.x - start.x;
					int moveY = end.y - start.y;
					if (fixedSquareRation) {
						int maxMove = Math.max(moveX, moveY);
						moveX = maxMove;
						moveY = maxMove;
					}

					crop.setSize(crop.width + (int) (moveX * ratio),
							crop.height + (int) (moveY * ratio));
					changeSize();
				}
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() > 0 && crop.x > 0
					&& crop.x + crop.width < image.getWidth() && crop.y > 0
					&& crop.y + crop.height < image.getHeight()
					|| e.getWheelRotation() < 0 && crop.width > 1
					&& crop.height > 1) {
				float ratio = (float) crop.height / (float) crop.width;
				int newWidth = (crop.width + e.getWheelRotation()
						* whellZoomAmount);
				int newHeight = (int) (newWidth * ratio);
				crop.setBounds(crop.x - (newWidth - crop.width) / 2, crop.y
						- (newHeight - crop.height) / 2, newWidth, newHeight);
				zoom = (float) (croppingPanel.getWidth() * image.getWidth())
						/ (float) (crop.width * getThumbbail().getWidth());
				changeZoom();
			}
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentShown(ComponentEvent e) {
		}

		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

	}

	/**
	 * @return the fixedSquareRation
	 */
	public boolean isFixedSquareRation() {
		return fixedSquareRation;
	}

	/**
	 * @param fixedSquareRation
	 *            the fixedSquareRation to set
	 */
	public void setFixedSquareRation(boolean fixedSquareRation) {
		this.fixedSquareRation = fixedSquareRation;
	}

	/**
	 * Convenience method that returns a scaled instance of the provided {@code
	 * BufferedImage}.
	 * 
	 * <p>
	 * Taken from <a href="http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html"
	 * >here</a>
	 * </p>
	 * 
	 * @param img
	 *            the original image to be scaled
	 * @param targetWidth
	 *            the desired width of the scaled instance, in pixels
	 * @param targetHeight
	 *            the desired height of the scaled instance, in pixels
	 * @param hint
	 *            one of the rendering hints that corresponds to {@code
	 *            RenderingHints.KEY_INTERPOLATION} (e.g. {@code
	 *            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR}, {@code
	 *            RenderingHints.VALUE_INTERPOLATION_BILINEAR}, {@code
	 *            RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality
	 *            if true, this method will use a multi-step scaling technique
	 *            that provides higher quality than the usual one-step technique
	 *            (only useful in downscaling cases, where {@code targetWidth}
	 *            or {@code targetHeight} is smaller than the original
	 *            dimensions, and generally only when the {@code BILINEAR} hint
	 *            is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public BufferedImage getScaledInstance(BufferedImage img, int targetWidth,
			int targetHeight, Object hint, boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

}