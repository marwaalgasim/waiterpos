package ua.cn.yet.waiter.util;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collection;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.OutputElement;
import ua.cn.yet.waiter.ui.AbstractForm;

/**
 * Different handy methods
 * 
 * @author Yuriy Tkach
 */
public class Utils {

	private static final String PIC_DIR = "picDir";

	private static Log log = LogFactory.getLog(Utils.class);

	public final static String[] imageExt = { "bmp", "jpeg", "jpg", "gif",
			"png" };

	/**
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Getting image file for the elem or inexistent file
	 * 
	 * @param elem
	 *            elem to get file for
	 * @return Image file for the elem or inexistent file
	 */
	public static File getImageFile(OutputElement elem) {
		if (StringUtils.isEmpty(elem.getPicture())) {
			return new File(String.valueOf(System.nanoTime()));
		}

		String picDir = System.getProperty(PIC_DIR);
		if (StringUtils.isEmpty(picDir)) {
			log.error("picDir is not set!");
		}

		File file = new File(picDir + System.getProperty("file.separator")
				+ elem.getPicture());
		return file;
	}

	/**
	 * Delete elem's image file
	 * 
	 * @param elem
	 *            Elem to delete file from
	 * @return true if successful
	 */
	public static boolean deleteImageFile(OutputElement elem) {
		File oldFile = Utils.getImageFile(elem);
		try {
			FileUtils.forceDelete(oldFile);
			if (log.isDebugEnabled()) {
				log.debug("Deleted old image file: " + oldFile);
			}
			return true;
		} catch (IOException e2) {
			log.warn("Failed to delete old image: " + oldFile);
			return false;
		}
	}

	/**
	 * Getting image icon either from element's picture path or default one
	 * 
	 * @param elem
	 *            element to get icon for
	 * @return ImageIcon for element
	 */
	public static ImageIcon getImageIconForElem(OutputElement elem) {
		File file = getImageFile(elem);

		if (file.exists()) {
			return new ImageIcon(file.getAbsolutePath());
		}

		String noPicName;
		if (elem instanceof Category) {
			noPicName = "no_cat.png";
		} else {
			noPicName = "no_pic.png";
		}

		return AbstractForm.createImageIcon(noPicName);
	}

	/**
	 * @return Temporary file
	 */
	public static File getTempFile() {
		File file;
		try {
			file = File.createTempFile("owl", null);
		} catch (IOException e) {
			file = new File(System.nanoTime() + ".tmp");
		}
		file.deleteOnExit();
		return file;
	}

	/**
	 * @return Node to save preferences to
	 */
	public static Preferences getPreferencesNode() {
		return Preferences.userRoot().node("Yuriy E. Tkach").node("Waiter");
	}

	/**
	 * Copying file from old location to the pictures location
	 * 
	 * @param filename
	 *            filename to copy
	 * @return new file name
	 */
	public static String copyToPicturesDir(String filename) {
		if (StringUtils.isEmpty(filename)) {
			return filename;
		}

		String destDir = System.getProperty(PIC_DIR);
		if (StringUtils.isEmpty(destDir)) {
			log.error("picDir is not set!");
			return filename;
		}

		try {
			FileUtils.forceMkdir(new File(destDir));

			File dest = new File(destDir + System.getProperty("file.separator")
					+ System.nanoTime() + ".png");

			FileUtils.copyFile(new File(filename), dest, true);
			if (log.isDebugEnabled()) {
				log.debug("Copied file to: " + dest);
			}
			return dest.getName();
		} catch (IOException e) {
			log.error("Failed to copy " + filename + " to " + destDir, e);
			return filename;
		}
	}

	/**
	 * Incoding provided <code>input</code> with md5
	 * 
	 * @return md5 encoded input
	 */
	public static String generateMd5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("md5");

			md.update(input.getBytes(), 0, input.length());

			String rez = new BigInteger(1, md.digest()).toString(16);
			return rez;
		} catch (NoSuchAlgorithmException e) {
			log.error("Failed to get message digest for md5 pass encoding. "
					+ e);
		}
		return null;
	}

	/**
	 * Getting key listener for text input components that will check if some
	 * text and enable components if there is any
	 * 
	 * @param disabledComponents
	 *            Components to disable/enable based on text in listened
	 *            component
	 * @return KeyListener to user in text components
	 */
	public static KeyListener getRequiredFieldKeyListener(
			final Collection<? extends Component> disabledComponents,
			final JTextComponent... additionalCheckFields) {
		return new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				JTextComponent textComponent = (JTextComponent) e.getSource();
				boolean enable = StringUtils
						.isNotEmpty(textComponent.getText());

				for (int i = 0; i < additionalCheckFields.length; i++) {
					enable &= StringUtils.isNotEmpty(additionalCheckFields[i]
							.getText());
				}

				for (Component component : disabledComponents) {
					component.setEnabled(enable);
				}
			}
		};
	}

	/**
	 * Getting right click mouse listener for table to select rows on right
	 * click
	 * 
	 * @param table
	 *            table to get listener for
	 * @return mouse listener for table
	 */
	public static MouseListener getTableRightClickRowSelectListener(
			final JTable table) {
		return new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (MouseEvent.BUTTON3 == e.getButton()) {
					int row = table.rowAtPoint(e.getPoint());
					table.setRowSelectionInterval(row, row);
				}
			}
		};
	}

	/**
	 * Getting two calendar objects that specify range for the full day of the
	 * specified <code>date</code>. First object will have time 00:00:0,1. Second
	 * object will have time 23:59:59,999. The date will be the same and is taken
	 * from the specified <code>date</code>.
	 * 
	 * @param date
	 *            Date to get range for
	 * @return Array with two calendar objects
	 */
	public static Calendar[] getFullDayRangeForDate(Calendar date) {
		if (null == date) {
			date = Calendar.getInstance();
		}
		
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		
		start.setTime(date.getTime());
		end.setTime(date.getTime());
		
		adjustTimesForRangeRequest(start, end);
		
		Calendar[] rez = new Calendar[2];
		rez[0] = start;
		rez[1] = end;
		return rez;
	}
	
	public static void adjustTimesForRangeRequest(Calendar start, Calendar end) {
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 1);
		
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 999);
	}
}
