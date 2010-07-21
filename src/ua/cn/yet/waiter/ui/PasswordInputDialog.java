package ua.cn.yet.waiter.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import ua.cn.yet.waiter.util.Utils;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog that asks for password input
 * 
 * @author Yuriy Tkach
 */
public class PasswordInputDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JButton btnOK;
	private JButton btnCancel;
	private JPasswordField passField;

	private String pass;

	public PasswordInputDialog(JFrame parent, String message, String title) {
		super(parent, title, true);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);

		setLayout(new MigLayout("insets 5", "[grow, fill, 300::]", "[]"));

		add(new JLabel(message), "wrap");

		passField = new JPasswordField();
		passField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
					if (passField.getPassword().length > 0) {
						btnOK.doClick();
					}
					break;
				case KeyEvent.VK_ESCAPE:
					btnCancel.doClick();
					break;
				}
			}
		});
		add(passField, "wrap");

		JPanel btnPanel = new JPanel(new MigLayout("insets 0",
				"[grow][fill,100::][fill,100::]", "[]"));
		add(btnPanel);

		btnPanel.add(new JLabel());

		btnOK = new JButton("ОК");
		btnPanel.add(btnOK);
		btnOK.setIcon(AbstractForm.createImageIcon("ok.png"));
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pass = String.valueOf(passField.getPassword());
				PasswordInputDialog.this.dispose();
			}
		});

		btnOK.setEnabled(false);
		passField.addKeyListener(Utils.getRequiredFieldKeyListener(Arrays
				.asList(btnOK)));
		
		btnCancel = new JButton("Отмена");
		btnCancel.setIcon(AbstractForm.createImageIcon("no.png"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pass = null;
				PasswordInputDialog.this.dispose();
			}
		});
		btnPanel.add(btnCancel);

		this.pack();
		this.setLocationRelativeTo(parent);
	}

	/**
	 * @param parent
	 *            Parent frame
	 * @param message
	 *            Message to display
	 * @param title
	 *            Title of the dialog
	 * @return Input password
	 */
	public static String showDialog(JFrame parent, String message, String title) {
		PasswordInputDialog dialog = new PasswordInputDialog(parent, message,
				title);
		dialog.setVisible(true);

		return dialog.pass;
	}

}
