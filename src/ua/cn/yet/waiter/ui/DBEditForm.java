package ua.cn.yet.waiter.ui;

import java.awt.Container;

import javax.swing.JTabbedPane;

import ua.cn.yet.waiter.FormListener;
import ua.cn.yet.waiter.ui.edit.ItemsEditPanel;
import ua.cn.yet.waiter.ui.edit.OrdersEditPanel;
import ua.cn.yet.waiter.ui.edit.UsersEditPanel;

/**
 * Form for editing database
 * 
 * @author Yuriy Tkach
 */
public class DBEditForm extends AbstractForm {

	

	public DBEditForm(String title, FormListener formListener) {
		super(title, formListener);
		createAndShowGUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.waiter.ui.AbstractForm#createComponents(java.awt.Container)
	 */
	@Override
	protected void createComponents(Container contentPane) {
		JTabbedPane tabPane = new JTabbedPane();
		
		tabPane.addTab("Элементы меню", new ItemsEditPanel(frame));
		
		tabPane.addTab("Пользователи", new UsersEditPanel(frame));
		
		tabPane.addTab("Заказы", new OrdersEditPanel(frame));
		
		tabPane.setSelectedIndex(2);

		contentPane.add(tabPane);
	}

	
}
