package ua.cn.yet.waiter.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import ua.cn.yet.waiter.FormListener;
import ua.cn.yet.waiter.LoginListener;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.UserService;
import ua.cn.yet.waiter.util.Utils;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Login form of the application
 * 
 * @author Yuriy Tkach
 */
public class LoginForm extends AbstractForm {

	private JButton btnLogin;
	private LoginListener loginListener;
	private JLabel lbInfo;
	private JTextField textLogin;
	private JPasswordField textPassword;

	public LoginForm(String title, FormListener formListener,
			LoginListener loginListener) {
		super(title, formListener);

		this.loginListener = loginListener;

		createAndShowGUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.ui.AbstractForm#configureFrameAdditionalOptions()
	 */
	@Override
	protected void configureFrameAdditionalOptions() {
		frame.setResizable(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ua.cn.yet.waiter.ui.AbstractForm#configureFrameSize()
	 */
	@Override
	protected void configureFrameSize() {
		// Doing nothing, because we don't want to inherit parents size
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.AbstractForm#createComponents(java.awt.Container)
	 */
	@Override
	protected void createComponents(Container contentPane) {
		contentPane.setLayout(new MigLayout("insets 0", "[center,grow,fill]",
				"[grow,fill]"));

		JPanel picPanel = new JPanel(new MigLayout("insets 0",
				"20[center,grow]", "[grow]"));
		JLabel pic = new JLabel();
		pic.setIcon(AbstractForm.createImageIcon("owl_big.png"));
		picPanel.add(pic);

		contentPane.add(picPanel, "wrap, center, w 380::");

		contentPane.add(createLoginControls(), "wrap");

		contentPane.add(createLoginButtons());
	}

	/**
	 * @return panel with login controls
	 */
	private Component createLoginControls() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 5 5 0 5", "[right][fill,grow]",
				"[][]"));

		JLabel lbLogin = new JLabel("Имя пользователя:");
		panel.add(lbLogin);

		textLogin = new JTextField();
		lbLogin.setLabelFor(textLogin);
		panel.add(textLogin, "wrap");

		JLabel lbPassword = new JLabel("Пароль:");
		panel.add(lbPassword);

		textPassword = new JPasswordField();
		lbPassword.setLabelFor(textPassword);
		textPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					btnLogin.doClick();
				}
			}
		});
		panel.add(textPassword);

		return panel;
	}

	/**
	 * @return panel with login and exit buttons
	 */
	private Component createLoginButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0 5 5 5", "[grow, fill][right]",
				""));

		lbInfo = new JLabel();
		lbInfo.setForeground(Color.red);
		lbInfo.setText(" ");
		panel.add(lbInfo);

		btnLogin = new JButton("Войти");
		btnLogin.setIcon(AbstractForm.createImageIcon("login.png"));
		panel.add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performLogin();
			}
		});

		return panel;
	}

	/**
	 * Performing login for user
	 */
	private void performLogin() {
		String login = textLogin.getText();
		if (StringUtils.isEmpty(login)) {
			lbInfo.setText("Введите имя пользователя. Без него никак :(");
			return;
		}

		char[] pass = textPassword.getPassword();
		if ((null == pass) || (0 == pass.length)) {
			lbInfo.setText("Введите пароль. Без него никак :(");
			return;
		}

		boolean clearLoginText = false;

		try {
			UserService service = WaiterInstance
					.forId(WaiterInstance.USER_SERVICE);

			final User user = service.authenticateUser(textLogin.getText(),
					Utils.generateMd5(String.valueOf(textPassword.getPassword())));

			if (null == user) {
				lbInfo.setText("Неверные имя или пароль");
			} else {
				clearLoginText = true;

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						loginListener.userLoggedIn(user);
					}
				});
			}

		} catch (Exception e1) {
			log.error("Failed to authenticate user.", e1);

			lbInfo.setText("Ошибка. Свяжитесь с администратором");
		}

		textPassword.setText(null);
		textPassword.requestFocus();
		if (clearLoginText) {
			textLogin.setText(null);
			textLogin.requestFocus();
			lbInfo.setText(null);
		}

	}
}
