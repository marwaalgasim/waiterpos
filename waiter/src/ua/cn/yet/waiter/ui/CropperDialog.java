package ua.cn.yet.waiter.ui;

import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.ui.components.ImageCropper;
import ua.cn.yet.waiter.util.Utils;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.CompoundFilter;
import com.jhlabs.image.GrayscaleFilter;

/**
 * Dialog, that provides panel for cropping image and saving the result into
 * file. It also helps with selecting an image from the drive.
 * 
 * @author Yuriy Tkach
 */
public class CropperDialog {

	private static final int PIC_RESIZE_VALUE = 75;

	private static final String LAST_PIC_DIR = "last_pic_dir";

	private static Log log = LogFactory.getLog(CropperDialog.class);

	private JDialog dialog;

	private String savedFilePath = "";

	private ImageCropper cropper = null;

	/**
	 * Selecting image and then cropping it
	 * 
	 * @return Path to the newly saved image
	 */
	public static String selectImageAndCrop(JFrame parent) {
		final CropperDialog dialog = new CropperDialog(parent);
		final File image = dialog.selectImage();
		if (image != null) {
			dialog.loadImageForCrop(image);
			dialog.show();
		}

		return dialog.getSavedFilePath();
	}

	public CropperDialog(JFrame parent) {
		dialog = new JDialog(parent, "Изменение картинки", true);
		dialog.setSize(800, 600);
		dialog.setLocationRelativeTo(parent);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.setLayout(new MigLayout("", "[fill, grow, 800::]",
				"[fill, grow, 450::][fill]"));
		dialog.add(createButtonPanel());
	}

	/**
	 * @return panel with control buttons
	 */
	private Component createButtonPanel() {
		JPanel panel = new JPanel();

		JButton btnChoose = new JButton("Выбрать другую картинку");
		btnChoose.setIcon(AbstractForm.createImageIcon("img_add.png"));
		btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = selectImage();
				if (file != null) {
					loadImageForCrop(file);
				}
			}
		});
		panel.add(btnChoose);

		JButton btnSave = new JButton("Сохранить");
		btnSave.setIcon(AbstractForm.createImageIcon("ok.png"));
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = cropper.getCroppedImage();
				image = cropper.getScaledInstance(image, PIC_RESIZE_VALUE, PIC_RESIZE_VALUE,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);

				File file = Utils.getTempFile();
				try {
					ImageIO.write(image, "png", file);
					savedFilePath = file.getAbsolutePath();
					dialog.dispose();
				} catch (IOException e1) {
					log.error("Failed to save image to: " + file, e1);
					JOptionPane.showMessageDialog(dialog, 
							"Ошибка при сохранинии картинки\n\n"+e1.getLocalizedMessage(),
							"Что-то случилось :(", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel.add(btnSave);

		JButton btnCancel = new JButton("Отменить");
		btnCancel.setIcon(AbstractForm.createImageIcon("no.png"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		panel.add(btnCancel);

		return panel;
	}

	/**
	 * Loading cropper for image and adding to the content pane
	 * 
	 * @param imageFile
	 *            Image file to load
	 */
	private void loadImageForCrop(File imageFile) {
		if (null == imageFile) {
			return;
		}

		if (null != cropper) {
			dialog.remove(cropper);
		}

		BufferedImage bImage;
		try {
			bImage = ImageIO.read(imageFile);
			cropper = new ImageCropper(bImage, true);

			BufferedImageOp filter = new CompoundFilter(new BoxBlurFilter(3, 3,
					3), new GrayscaleFilter());
			cropper.setFilter(filter);

			dialog.add(cropper, "wrap", 0);
			dialog.pack();

			dialog.setTitle("Изменение картинки - " + imageFile.getName());
		} catch (IOException e) {
			log.error("Failed to load image for crop: " + imageFile, e);
			JOptionPane.showMessageDialog(dialog, "Ошибка загрузки картинки. Попробуйте другую картинку.",
					"Картинка не подходит", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Showing file chooser dialog for selecting an image file
	 * 
	 * @return Selected file or <code>null</code>
	 */
	public File selectImage() {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new ImageFilter());
		
		Preferences prefs = Utils.getPreferencesNode();
		String lastPath = prefs.get(LAST_PIC_DIR, null);
		if (StringUtils.isNotEmpty(lastPath)) {
			fc.setCurrentDirectory(new File(lastPath));
		}
		
		int rez = fc.showOpenDialog(dialog);
		if (JFileChooser.APPROVE_OPTION == rez) {
			File file = fc.getSelectedFile();
			if (log.isDebugEnabled()) {
				log.debug("Selected file for cropping: " + file);
			}
			
			prefs.put(LAST_PIC_DIR, file.getParent());
			
			return file;
		}
		return null;
	}

	/**
	 * Displaying dialog
	 */
	private void show() {
		dialog.setVisible(true);
	}

	/**
	 * @return the savedFilePath
	 */
	public String getSavedFilePath() {
		return savedFilePath;
	}

}
