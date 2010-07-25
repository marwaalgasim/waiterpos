package ua.cn.yet.waiter.model;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;
import org.hibernate.validator.NotEmpty;

/**
 * User of the system
 * 
 * @author Yuriy Tkach
 */
@Entity
@Table(name="usr")
@NamedQueries({
	@NamedQuery(name=User.QUERY_BY_LOGIN_AND_PASS, 
			query="SELECT x FROM User x WHERE x.username = ?1 AND x.password = ?2"),
	@NamedQuery(name=User.QUERY_ALL_NON_ADMINS, 
					query="SELECT x FROM User x WHERE x.admin = false ORDER BY x.fullName"),
})
public class User extends DomainObject {

	private static final long serialVersionUID = 1L;
	
	public static final String QUERY_BY_LOGIN_AND_PASS = "userByLoginAndPass";
	public static final String QUERY_ALL_NON_ADMINS = "getAllNonAdmins";

	/** User name that is used to login */
	@NotEmpty
	private String username;

	/** Password to login */
	@NotEmpty
	private String password;

	/** Full name of the user */
	private String fullName;

	/** Specifies if user is active and can access system */
	
	private boolean active = true;
	
	/** Specifies if user is admin */
	private boolean admin = false;
	
	@Formula("(SELECT count(odr.id) FROM ordr as odr WHERE odr.waiter_id = id AND odr.closed = '1')")
	private int ordersClosed = 0;
	
	@Formula("(SELECT count(odr.id) FROM ordr as odr WHERE odr.waiter_id = id AND odr.closed = '0')")
	private int ordersOpen = 0;
	
	public User() {
		super();
	}

	public User(String username, String password, String fullName) {
		super();
		this.username = username;
		this.password = password;
		this.fullName = fullName;
	}
	
	/**
	 * Decreasing number of closed orders
	 */
	public void decreaseClosedOrdersCount() {
		if (ordersClosed > 0) {
			ordersClosed--;
		}
	}
	
	/**
	 * Decreasing number of open orders
	 */
	public void decreaseOpenOrdersCount() {
		if (ordersOpen > 0) {
			ordersOpen--;
		}
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * @return the ordersClosed
	 */
	public int getOrdersClosed() {
		return ordersClosed;
	}

	/**
	 * @return the ordersOpen
	 */
	public int getOrdersOpen() {
		return ordersOpen;
	}
}
