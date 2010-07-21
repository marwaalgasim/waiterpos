package ua.cn.yet.waiter.ui.edit;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import ua.cn.yet.common.ui.popup.PopupFactory;
import ua.cn.yet.common.ui.popup.PopupListener;
import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.ui.AbstractForm;
import ua.cn.yet.waiter.ui.PasswordInputDialog;
import ua.cn.yet.waiter.ui.events.OrderDeletedEvent;
import ua.cn.yet.waiter.ui.table.editors.BooleanColumnEditor;
import ua.cn.yet.waiter.ui.table.editors.ColumnUserDelEditor;
import ua.cn.yet.waiter.ui.table.models.TableModelUsers;
import ua.cn.yet.waiter.ui.table.renderers.BooleanColumnRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnDelRenderer;
import ua.cn.yet.waiter.util.Utils;

public class UsersEditPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(UsersEditPanel.class);

	private JFrame parentFrame;

	private TableModelUsers tableModelUsers;

	public UsersEditPanel(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		
		AnnotationProcessor.process(this);

		setLayout(new MigLayout("insets 5", "[grow, fill]", "[grow,fill][]"));

		add(createUsersTable(), "wrap");

		add(createUsersAddPanel());
	}

	/**
	 * @return panel with users table
	 */
	private Component createUsersTable() {
		JScrollPane scroll = new JScrollPane();

		tableModelUsers = new TableModelUsers(this);
		final JTable tableUsers = new JTable(tableModelUsers);
		scroll.setViewportView(tableUsers);

		configureColumnsInUsersTable(tableUsers);

		tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableRowSorter<TableModelUsers> sorter = new TableRowSorter<TableModelUsers>(
				tableModelUsers);
		SortKey sortKey = new SortKey(TableModelUsers.COLUMN_LOGIN,
				SortOrder.ASCENDING);
		List<SortKey> listSortKeys = new LinkedList<SortKey>();
		listSortKeys.add(sortKey);
		sorter.setSortKeys(listSortKeys);
		sorter.setSortable(TableModelUsers.COLUMN_NUM, false);
		sorter.setSortable(TableModelUsers.COLUMN_DEL, false);
		tableUsers.setRowSorter(sorter);

		tableUsers.addMouseListener(Utils.getTableRightClickRowSelectListener(tableUsers));

		tableUsers.addMouseListener(new PopupListener(PopupFactory
				.getGeneralPopup(new PasswordChangeAction(tableUsers))));

		return scroll;
	}

	private class PasswordChangeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private JTable table;

		public PasswordChangeAction(JTable tableUsers) {
			super("Изменить пароль");
			this.table = tableUsers;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] rows = table.getSelectedRows();
			if (rows.length > 0) {
				int modelRow = table.convertRowIndexToModel(rows[0]);
				User user = tableModelUsers.getUser(modelRow);

				String newPass = PasswordInputDialog.showDialog(
						UsersEditPanel.this.parentFrame,
						"Введите новый пароль:", "Изменяем пароль для " + user.getUsername());
				
				if (StringUtils.isNotBlank(newPass)) {
					user.setPassword(Utils.generateMd5(newPass));
					try {
						tableModelUsers.addUpdateUser(user);
					} catch (Exception e1) {
						log.error("Failed to update pass for user: " + user, e1);
						JOptionPane.showMessageDialog(parentFrame, e1
								.getLocalizedMessage(),
								"Не получилось изменить пароль :(",
								JOptionPane.ERROR_MESSAGE);						
					}
				}
			}
		}
	}

	/**
	 * Configuring columns for table
	 * 
	 * @param table
	 *            table to configure
	 */
	private void configureColumnsInUsersTable(JTable table) {
		TableColumn col = table.getColumnModel().getColumn(
				TableModelUsers.COLUMN_NUM);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(30);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelUsers.COLUMN_LOGIN);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.LEADING);
		col.setPreferredWidth(50);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelUsers.COLUMN_ACTIVE);
		col.setCellRenderer(new BooleanColumnRenderer());
		col.setCellEditor(new BooleanColumnEditor());
		col.setMaxWidth(100);

		col = table.getColumnModel().getColumn(TableModelUsers.COLUMN_ADMIN);
		col.setCellRenderer(new BooleanColumnRenderer());
		col.setCellEditor(new BooleanColumnEditor());
		col.setMaxWidth(50);

		col = table.getColumnModel().getColumn(
				TableModelUsers.COLUMN_ORDER_OPEN);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(150);
		col.setMinWidth(100);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(
				TableModelUsers.COLUMN_ORDER_CLOSED);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(140);
		col.setMinWidth(90);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelUsers.COLUMN_DEL);
		render = new ColumnDelRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(180);
		col.setMinWidth(100);
		col.setCellRenderer(render);
		col.setCellEditor(new ColumnUserDelEditor(tableModelUsers));
	}

	/**
	 * @return panel for adding users
	 */
	private Component createUsersAddPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory
				.createTitledBorder("Добавить пользователя"));
		panel.setLayout(new MigLayout("insets 5",
				"[fill,20%][fill,20%][fill,grow][center][center,10%]", "[][]"));

		panel.add(new JLabel("Имя (логин)"));
		panel.add(new JLabel("Пароль"));
		panel.add(new JLabel("ФИО"));
		panel.add(new JLabel("Администратор"), "wrap");

		final JTextField textUserName = new JTextField();
		textUserName.addMouseListener(new PopupListener(PopupFactory
				.getEditPopup()));
		panel.add(textUserName);

		final JPasswordField textUserPassword = new JPasswordField();
		panel.add(textUserPassword);

		final JTextField textUserFullName = new JTextField();
		textUserFullName.addMouseListener(new PopupListener(PopupFactory
				.getEditPopup()));
		panel.add(textUserFullName);

		final JCheckBox checkUserAdmin = new JCheckBox();
		panel.add(checkUserAdmin);

		JButton btnAddUser = new JButton("Добавить");
		btnAddUser.setIcon(AbstractForm.createImageIcon("user_add.png"));
		panel.add(btnAddUser);
		btnAddUser.setEnabled(false);
		btnAddUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User user = new User();
				user.setUsername(textUserName.getText());
				user.setPassword(Utils.generateMd5(String
						.valueOf(textUserPassword.getPassword())));
				user.setFullName(textUserFullName.getText());
				user.setActive(true);
				user.setAdmin(checkUserAdmin.isSelected());

				try {
					if (validateUserFields(user)) {
						tableModelUsers.addUpdateUser(user);

						textUserName.setText(null);
						textUserPassword.setText(null);
						textUserFullName.setText(null);
						checkUserAdmin.setSelected(false);

						textUserName.requestFocus();
					}
				} catch (Exception e1) {
					log.error("Failed to add: " + user, e1);
					JOptionPane.showMessageDialog(parentFrame, e1
							.getLocalizedMessage(),
							"Не получилось добавить :(",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		textUserName.addKeyListener(Utils.getRequiredFieldKeyListener(Arrays
				.asList(btnAddUser), textUserPassword, textUserFullName));
		textUserPassword.addKeyListener(Utils.getRequiredFieldKeyListener(Arrays
				.asList(btnAddUser), textUserName, textUserFullName));
		textUserFullName.addKeyListener(Utils.getRequiredFieldKeyListener(Arrays
				.asList(btnAddUser), textUserName, textUserPassword));

		return panel;
	}

	/**
	 * Checking user fields for correctness
	 * 
	 * @param user
	 *            User object to check
	 * @return true if all fields are valid
	 */
	public boolean validateUserFields(User user) {
		if (StringUtils.isEmpty(user.getUsername())) {
			JOptionPane.showMessageDialog(parentFrame,
					"Введите имя пользователя", "Немогу добавить :(",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (StringUtils.isEmpty(user.getPassword())) {
			JOptionPane.showMessageDialog(parentFrame, "Введите имя пароль",
					"Немогу добавить :(", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		boolean userExists = tableModelUsers.userExists(user.getUsername());
		if (userExists) {
			JOptionPane.showMessageDialog(parentFrame,
					"Пользователь с таким именем уже существует",
					"Немогу добавить :(", JOptionPane.ERROR_MESSAGE);
		}

		return !userExists;
	}
	
	@EventSubscriber(eventClass=OrderDeletedEvent.class)
	public void onDelOrder(OrderDeletedEvent e) {
		tableModelUsers.updateLocalUsersList();
	}

}
