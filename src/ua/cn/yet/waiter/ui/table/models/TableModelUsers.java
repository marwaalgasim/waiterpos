package ua.cn.yet.waiter.ui.table.models;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.model.User;
import ua.cn.yet.waiter.service.UserService;
import ua.cn.yet.waiter.util.WaiterInstance;

/**
 * Table model for users
 * 
 * @author Yuriy Tkach
 */
public class TableModelUsers extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(TableModelUsers.class);

	public static final int COLUMN_DEL = 7;
	public static final int COLUMN_ORDER_OPEN = 6;
	public static final int COLUMN_ORDER_CLOSED = 5;
	public static final int COLUMN_ADMIN = 4;
	public static final int COLUMN_ACTIVE = 3;
	public static final int COLUMN_FULL_NAME = 2;
	public static final int COLUMN_LOGIN = 1;
	public static final int COLUMN_NUM = 0;

	/**
	 * Names of all columns
	 */
	private final String[] columnNames = { "№", "Логин", "ФИО", "Активный",
			"Админ", "Заказы Вып.", "Заказы Откр.", "" };

	/** User service to use */
	private UserService userService;

	private List<User> users = new ArrayList<User>();

	private Component parentComponent;

	public TableModelUsers(Component parentComponent) {
		super();
		
		this.parentComponent = parentComponent;
		userService = WaiterInstance.forId(WaiterInstance.USER_SERVICE);

		updateLocalUsersList();
	}

	/**
	 * Updating local list of users
	 */
	public void updateLocalUsersList() {
		users = userService.getAllEntites();
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return users.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if ((rowIndex < 0) || (rowIndex >= getRowCount()) || (columnIndex < 0)
				|| (columnIndex >= getColumnCount())) {
			return null;
		} else {
			User user = users.get(rowIndex);

			switch (columnIndex) {
			case COLUMN_NUM:
				return rowIndex + 1;
			case COLUMN_LOGIN:
				return user.getUsername();
			case COLUMN_FULL_NAME:
				return user.getFullName();
			case COLUMN_ACTIVE:
				return Boolean.valueOf(user.isActive());
			case COLUMN_ADMIN:
				return Boolean.valueOf(user.isAdmin());
			case COLUMN_ORDER_CLOSED:
				return user.getOrdersClosed();
			case COLUMN_ORDER_OPEN:
				return user.getOrdersOpen();
			case COLUMN_DEL:
				return user;
			default:
				return null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case COLUMN_NUM:
		case COLUMN_ORDER_CLOSED:
		case COLUMN_ORDER_OPEN:
			return false;
		default:
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if ((rowIndex < 0) || (rowIndex >= getRowCount()) || (columnIndex < 0)
				|| (columnIndex >= getColumnCount()) || (null == aValue)) {
			return;
		}

		User user = users.get(rowIndex);

		switch (columnIndex) {
		case COLUMN_LOGIN:
			String sLogin = (String) aValue;
			if (!user.getUsername().equals(sLogin)) {
				if (userExists(sLogin)) {
					JOptionPane.showMessageDialog(parentComponent,
							"Пользователь с таким именем уже существует",
							"Немогу изменить :(", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					user.setUsername(sLogin);
				}
			} else {
				return;
			}
			break;
		case COLUMN_FULL_NAME:
			String sFullName = (String) aValue;
			if (!user.getFullName().equals(sFullName)) {
				user.setFullName(sFullName);
			} else {
				return;
			}
			break;
		case COLUMN_ACTIVE:
			Boolean bAction = (Boolean) aValue;
			if (user.isActive() != bAction) {
				user.setActive(bAction);
			} else {
				return;
			}
			break;
		case COLUMN_ADMIN:
			Boolean bAdmin = (Boolean) aValue;
			if (user.isAdmin() != bAdmin) {
				user.setAdmin(bAdmin);
			} else {
				return;
			}
			break;
		}

		try {
			userService.save(user);
			updateLocalUsersList();
		} catch (Exception e) {
			log.error("Failed to update item: " + user, e);
			JOptionPane.showMessageDialog(parentComponent, e
					.getLocalizedMessage(),
					"Не получилось обновить элемент :(",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Deleting user from model and DB
	 * 
	 * @param delUser
	 *            user to delete
	 * @throws Exception
	 */
	public void deleteUser(User delUser) throws Exception {
		userService.delEntity(delUser);
		updateLocalUsersList();
	}

	/**
	 * Adding user to the model and db
	 * 
	 * @param user
	 *            user to add.
	 */
	public void addUpdateUser(User user) throws Exception {
		userService.save(user);
		updateLocalUsersList();
	}

	/**
	 * Because there are not many users, thus checking in memory without hitting
	 * the database.
	 * 
	 * @param username
	 *            Username to check
	 * @return true if user with such login exists
	 */
	public boolean userExists(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Getting user object by it's index
	 * 
	 * @param modelRow
	 *            Model row (index) of the user
	 * @return Found user or <code>null</code>
	 */
	public User getUser(int modelRow) {
		if ((modelRow < 0) || (modelRow > (getRowCount()-1))) {
			return null;
		}
		
		return users.get(modelRow);
	}

}
