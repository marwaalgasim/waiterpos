package ua.cn.yet.waiter;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.ui.AbstractForm;
import ua.cn.yet.waiter.ui.DBEditForm;
import ua.cn.yet.waiter.ui.LoginForm;
import ua.cn.yet.waiter.ui.WaiterForm;
import ua.cn.yet.waiter.util.Config;
import ua.cn.yet.waiter.util.InstanceLock;
import ua.cn.yet.waiter.util.ProgramVersion;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Program entry point
 * 
 * @author Yuriy Tkach
 */
public class WaiterApp implements LoginListener, FormListener {

	private static Log log = LogFactory.getLog(WaiterApp.class);

	/** Title of the application */
	private String appTitle;

	/** Map of forms that are running and attached to user */
	private final Map<User, AbstractForm> runningForms = new HashMap<User, AbstractForm>();

	private AbstractForm loginForm;

	public static void main(String[] args) {
		new WaiterApp().runApps();
	}

	/**
	 * Run application
	 */
	private void runApps() {
		Locale.setDefault(new Locale("ru", "RU"));

		StringBuilder appTitleBuilder = new StringBuilder("Waiter ");
		appTitleBuilder.append(ProgramVersion.getVersion());
		appTitleBuilder.append(" - ");
		appTitleBuilder.append(Config.getBundleValue("institution.name"));

		appTitle = appTitleBuilder.toString();

		if (true) {
			if (!InstanceLock.registerInstanceLock()) {
				JOptionPane.showMessageDialog(null, "Приложение уже работает.",
						appTitle, JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

		WaiterInstance.loadInstances();

		runLogin();
	}

	/**
	 * Run login loginForm
	 */
	private void runLogin() {
		loginForm = new LoginForm(appTitle, this, this);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loginForm.show();
			}
		});
	}

	/**
	 * Run waiter loginForm
	 */
	private void runWaiter(User user) {
		AbstractForm form = new WaiterForm(appTitle, this, user);
		runForm(user, form);
	}

	/**
	 * Run super user loginForm
	 */
	private void runSuperUserForm(User user) {
		AbstractForm form = new DBEditForm(appTitle, this);
		runForm(user, form);
	}

	/**
	 * Running loginForm for user
	 * 
	 * @param user
	 *            User to run for
	 * @param loginForm
	 *            Form to run
	 */
	private void runForm(User user, final AbstractForm form) {
		loginForm.hide();

		runningForms.put(user, form);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				form.show();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.LoginListener#userLoggedIn(ua.cn.yet.waiter.model.User)
	 */
	@Override
	public void userLoggedIn(User user) {
		if (!user.isActive()) {
			JOptionPane.showMessageDialog(null,
					"Ваш пользователь неактивен. Обратитесь к администратору.",
					"Не получилось :(", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!activateRunningForm(user)) {
			if (user.isAdmin()) {
				runSuperUserForm(user);
			} else {
				runWaiter(user);
			}
		}
	}

	/**
	 * Activating running for user if there is one
	 * 
	 * @param user
	 *            User to active loginForm for
	 * @return true if activated, false otherwise
	 */
	private boolean activateRunningForm(User user) {
		AbstractForm form = runningForms.get(user);
		if (form != null) {
			form.activate();
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.FormListener#formClosing(ua.cn.yet.waiter.ui.AbstractForm
	 * )
	 */
	@Override
	public void formClosing(AbstractForm form) {

		if (loginForm.equals(form)) {
			if (runningForms.isEmpty()) {
				// Closing, if no other windows open
				cleanUpAndExit();
			} else {
				// Hiding if some form're open and activate first form
				loginForm.hide();
				runningForms.entrySet().iterator().next().getValue().activate();
			}
		} else {
			// Disposing form, showing login form
			for (Entry<User, AbstractForm> entry : runningForms.entrySet()) {
				if (entry.getValue().equals(form)) {
					runningForms.remove(entry.getKey());
					break;
				}
			}
			form.dispose();
			loginForm.show();
			loginForm.activate();
		}
	}

	/**
	 * Cleaning up resources and exiting
	 */
	private void cleanUpAndExit() {

		try {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (SQLException e1) {
			log.info(e1.getLocalizedMessage());
		}

		System.exit(0);
	}

}
