package ua.cn.yet.waiter.ui.edit;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.common.ui.popup.PopupFactory;
import ua.cn.yet.common.ui.popup.PopupListener;
import ua.cn.yet.waiter.model.AbstractItem;
import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.model.ItemType;
import ua.cn.yet.waiter.service.CategoryService;
import ua.cn.yet.waiter.service.ItemService;
import ua.cn.yet.waiter.ui.AbstractForm;
import ua.cn.yet.waiter.ui.CropperDialog;
import ua.cn.yet.waiter.ui.table.editors.ColumnItemDelEditor;
import ua.cn.yet.waiter.ui.table.editors.ColumnLiquidEditor;
import ua.cn.yet.waiter.ui.table.editors.ColumnPicEditor;
import ua.cn.yet.waiter.ui.table.models.TableModelItemEdit;
import ua.cn.yet.waiter.ui.table.renderers.ColumnDelRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnLiquidRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnPicRenderer;
import ua.cn.yet.waiter.ui.table.renderers.ColumnPriceRenderer;
import ua.cn.yet.waiter.util.Utils;
import ua.cn.yet.waiter.util.WaiterInstance;

public class ItemsEditPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(ItemsEditPanel.class);

	private CategoryService categoryService;
	private ItemService itemService;

	private JTextField textCatName;
	private JTabbedPane tabsCategories;
	private JButton btnCategorySelectPic;
	private JLabel categoryEditPicture;
	private JLabel currentCategoryName;
	private JLabel currentCategoryPic;
	private JLabel currentCategoryItemNum;
	private JLabel totalItemNum;

	private List<Category> categories = new ArrayList<Category>();
	private List<TableModelItemEdit> tableModels = new ArrayList<TableModelItemEdit>();

	private JTextField textItemName;
	private JLabel itemEditPicture;
	private JButton btnItemSelectPic;
	private JSpinner spinItemMass;
	private JFormattedTextField textItemPrice;
	private JCheckBox checkItemDrink;
	private JCheckBox checkItemAlcohol;

	private JFrame parentFrame;

	private JCheckBox checkItemBar;

	private JCheckBox checkSingleItem;

	public ItemsEditPanel(JFrame parentFrame) {
		this.parentFrame = parentFrame;

		categoryService = WaiterInstance.forId(WaiterInstance.CATEGORY_SERVICE);
		itemService = WaiterInstance.forId(WaiterInstance.ITEM_SERVICE);
		
		this.setLayout(new MigLayout("insets 5", "[fill][fill, grow]",
				"[fill][fill,grow][fill]"));

		this.add(createCategoryAddPanel());

		this.add(createStatisticPanel(), "wrap");

		this.add(createCategoriesPanel(), "span 2, wrap");
		
		this.add(createItemAddPanel(), "span 2");
	}

	/**
	 * @return New panel for add/edit category
	 */
	private Component createCategoryAddPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Категория"));

		panel.setLayout(new MigLayout("", "[fill,300!,center][fill,75!]",
				"[fill][fill]10[fill]"));

		textCatName = new JTextField();
		textCatName.setFont(new Font("", Font.BOLD, 14));
		textCatName.addMouseListener(new PopupListener(PopupFactory
				.getEditPopup()));
		panel.add(textCatName);

		categoryEditPicture = new JLabel("");
		panel.add(categoryEditPicture, "w 75!, span 1 3, wrap");

		btnCategorySelectPic = new JButton("Картинка");
		btnCategorySelectPic.setIcon(AbstractForm.createImageIcon("img_add.png"));
		btnCategorySelectPic.addActionListener(new AddImageListener(
				btnCategorySelectPic, categoryEditPicture));
		panel.add(btnCategorySelectPic, "wrap, w 150!");

		JButton btnAdd = new JButton("Создать категорию");
		btnAdd.setEnabled(false);
		btnAdd.addActionListener(new AddCategoryListener());
		btnAdd.setIcon(AbstractForm.createImageIcon("cat_add.png"));
		panel.add(btnAdd);

		textCatName.addKeyListener(Utils.getRequiredFieldKeyListener(Arrays
				.asList(btnAdd)));

		clearCategoryAddValues();

		return panel;
	}

	/**
	 * Clearing values of the category add panel
	 */
	private void clearCategoryAddValues() {
		textCatName.setText("");
		categoryEditPicture.setIcon(AbstractForm.createImageIcon("no_cat.png"));
		btnCategorySelectPic.setToolTipText("");
	}

	/**
	 * @return New statistic panel
	 */
	private Component createStatisticPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Статистика"));

		panel.setLayout(new MigLayout("", "[right][fill,left,grow][fill]",
				"[fill]10[fill]20[fill]"));

		panel.add(new JLabel("Категория:"));

		currentCategoryName = new JLabel();
		currentCategoryName.setFont(new Font("", Font.BOLD, 14));
		panel.add(currentCategoryName);

		currentCategoryPic = new JLabel();
		panel.add(currentCategoryPic, "w 75!, span 1 3, wrap");

		panel.add(new JLabel("Элементов в категории:"));

		currentCategoryItemNum = new JLabel();
		currentCategoryItemNum.setFont(new Font("", Font.BOLD, 14));
		panel.add(currentCategoryItemNum, "wrap");

		panel.add(new JLabel("Всего элементов:"));

		totalItemNum = new JLabel();
		totalItemNum.setFont(new Font("", Font.BOLD, 14));
		panel.add(totalItemNum);

		updateTotalItemCount();

		return panel;
	}

	/**
	 * @return New categories with items panel
	 */
	private Component createCategoriesPanel() {
		tabsCategories = new JTabbedPane();
		loadCategoryTabs();
		tabsCategories.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateStatisticPanel();
			}
		});
		tabsCategories.addMouseListener(new PopupListener(PopupFactory
				.getGeneralPopup(new ChangeCategoryNameAction(),
						new ChangeCategoryPicAction(), null,
						new DeleteCategoryPicAction(), null,
						new DeleteCategoryAction())));
		updateStatisticPanel();

		return tabsCategories;
	}

	/**
	 * Loading all existing categories
	 */
	private void loadCategoryTabs() {
		tabsCategories.removeAll();
		categories.clear();
		tableModels.clear();

		Collection<Category> cats = categoryService.getAllSorted("name", true);
		for (Category category : cats) {
			addCategoryTab(category);
		}
	}

	/**
	 * Adding category tab
	 * 
	 * @param category
	 *            category to add
	 */
	private void addCategoryTab(Category category) {
		categories.add(category);
		Icon icon = null;
		// icon = Utils.getImageIconForElem(category);
		tabsCategories.addTab(category.getName(), icon,
				createItemsTable(category));
	}

	/**
	 * Creating component for displaying items of the category
	 * 
	 * @param category
	 *            Category to get items for
	 * @return new component for displaying category items
	 */
	private Component createItemsTable(Category category) {
		JScrollPane scrollReceipt = new JScrollPane();

		TableModelItemEdit tableModelItemEdit = new TableModelItemEdit(
				category, this.getParent());
		final JTable tableItemEdit = new JTable(tableModelItemEdit);
		scrollReceipt.setViewportView(tableItemEdit);

		configureColumnsInItemTable(tableItemEdit, category);

		tableItemEdit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableRowSorter<TableModelItemEdit> sorter = new TableRowSorter<TableModelItemEdit>(
				tableModelItemEdit);
		SortKey sortKey = new SortKey(TableModelItemEdit.COLUMN_NAME,
				SortOrder.ASCENDING);
		List<SortKey> listSortKeys = new LinkedList<SortKey>();
		listSortKeys.add(sortKey);
		sorter.setSortKeys(listSortKeys);
		sorter.setSortable(TableModelItemEdit.COLUMN_NUM, false);
		sorter.setSortable(TableModelItemEdit.COLUMN_PIC, false);
		sorter.setSortable(TableModelItemEdit.COLUMN_DEL, false);
		tableItemEdit.setRowSorter(sorter);

		tableModels.add(tableModelItemEdit);

		tableItemEdit.addMouseListener(Utils
				.getTableRightClickRowSelectListener(tableItemEdit));

		return scrollReceipt;
	}

	private void configureColumnsInItemTable(JTable table, Category category) {
		TableColumn col = table.getColumnModel().getColumn(
				TableModelItemEdit.COLUMN_NUM);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(50);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelItemEdit.COLUMN_PIC);
		render = new ColumnPicRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(75);
		col.setCellRenderer(render);
		col.setCellEditor(new ColumnPicEditor(parentFrame));

		col = table.getColumnModel().getColumn(TableModelItemEdit.COLUMN_NAME);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.LEADING);
		col.setCellRenderer(render);

		col = table.getColumnModel()
				.getColumn(TableModelItemEdit.COLUMN_LIQUID);
		ColumnLiquidRenderer liquid_render = new ColumnLiquidRenderer();
		liquid_render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(80);
		col.setCellRenderer(liquid_render);
		col.setCellEditor(new ColumnLiquidEditor());

		col = table.getColumnModel().getColumn(TableModelItemEdit.COLUMN_MASS);
		render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(100);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelItemEdit.COLUMN_PRICE);
		render = new ColumnPriceRenderer(true);
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(100);
		col.setCellRenderer(render);

		col = table.getColumnModel().getColumn(TableModelItemEdit.COLUMN_DEL);
		render = new ColumnDelRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		col.setMaxWidth(180);
		col.setMinWidth(100);
		col.setCellRenderer(render);
		col.setCellEditor(new ColumnItemDelEditor(category,
				(TableModelItemEdit) table.getModel(), this));
	}

	/**
	 * @return new panel for adding items to category
	 */
	private Component createItemAddPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Элемент"));
		panel
				.setLayout(new MigLayout(
						"",
						"[right,60::][fill,100::][left,20!][left,35!][left,50::][right,60::][fill, 100::]20[]20[left]",
						"[fill][fill][fill][grow]"));

		panel.add(new JLabel("Название:"));
		textItemName = new JTextField();
		textItemName.addMouseListener(new PopupListener(PopupFactory
				.getEditPopup()));
		panel.add(textItemName, "span 6");

		btnItemSelectPic = new JButton("Картинка");
		btnItemSelectPic.setIcon(AbstractForm.createImageIcon("img_add.png"));
		panel.add(btnItemSelectPic);

		itemEditPicture = new JLabel();
		btnItemSelectPic.addActionListener(new AddImageListener(
				btnItemSelectPic, itemEditPicture));
		panel.add(itemEditPicture, "span 1 3");

		JButton btnAddItem = new JButton("Добавить элемент");
		btnAddItem.addActionListener(new AddItemListener());
		btnAddItem.setIcon(AbstractForm.createImageIcon("add.png"));
		panel.add(btnAddItem, "wrap");
		btnAddItem.setEnabled(false);

		textItemName.addKeyListener(Utils.getRequiredFieldKeyListener(Arrays
				.asList(btnAddItem)));

		final JLabel lbMass = new JLabel("Выход:");
		panel.add(lbMass);

		spinItemMass = new JSpinner();
		SpinnerNumberModel spinModel = new SpinnerNumberModel();
		spinModel.setMinimum(0);
		spinModel.setStepSize(10);
		spinItemMass.setModel(spinModel);
		panel.add(spinItemMass);
		
		final JLabel lbItemOutMetric = new JLabel("г");
		panel.add(lbItemOutMetric);
		
		panel.add(new JLabel("или"));
		
		checkSingleItem = new JCheckBox("1-а порция", false);
		checkSingleItem.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (checkSingleItem.isSelected()) {
					lbMass.setEnabled(false);
					spinItemMass.setEnabled(false);
					spinItemMass.setValue(Integer.valueOf(0));
					lbItemOutMetric.setEnabled(false);
				} else {
					lbMass.setEnabled(true);
					spinItemMass.setEnabled(true);
					lbItemOutMetric.setEnabled(true);
				}
			}
		});
		panel.add(checkSingleItem);

		panel.add(new JLabel("Цена:"));

		textItemPrice = new JFormattedTextField();
		textItemPrice.setValue(new Double(0));
		textItemName.addMouseListener(new PopupListener(PopupFactory
				.getEditPopup()));
		panel.add(textItemPrice, "wrap");
		
		checkItemBar = new JCheckBox("Бар", false);
		panel.add(checkItemBar);
		
		checkItemDrink = new JCheckBox("Напиток", false);
		checkItemDrink.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (checkItemDrink.isSelected()) {
					lbItemOutMetric.setText("мл");
					checkItemAlcohol.setVisible(true);
					checkItemBar.setSelected(true);
					checkItemBar.setEnabled(false);
				} else {
					lbItemOutMetric.setText("г");
					checkItemAlcohol.setSelected(false);
					checkItemAlcohol.setVisible(false);
					checkItemBar.setSelected(false);
					checkItemBar.setEnabled(true);
				}
			}
		});
		panel.add(checkItemDrink);

		checkItemAlcohol = new JCheckBox("Алкоголь", false);
		panel.add(checkItemAlcohol, "span 3, wrap");
		checkItemAlcohol.setVisible(false);

		panel.add(new JLabel());

		clearItemAddValues();

		return panel;
	}

	/**
	 * Clearing values of the item add panel
	 */
	private void clearItemAddValues() {
		textItemName.setText("");
		spinItemMass.setValue(Integer.valueOf(0));
		textItemPrice.setValue(Double.valueOf(0));
		itemEditPicture.setIcon(AbstractForm.createImageIcon("no_pic.png"));
		btnItemSelectPic.setToolTipText("");
	}

	/**
	 * Updating statistic panel with info about all items
	 */
	private void updateStatisticPanel() {
		if (tabsCategories.getSelectedIndex() > -1) {
			Category cat = categories.get(tabsCategories.getSelectedIndex());

			this.currentCategoryName.setText(cat.getName());
			this.currentCategoryPic.setIcon(Utils.getImageIconForElem(cat));
			this.currentCategoryItemNum.setText(String.valueOf(cat.getItems()
					.size()));
		}
	}

	/**
	 * Updating count of total items
	 */
	public void updateTotalItemCount() {
		totalItemNum.setText(String.valueOf(itemService.getAllEntitiesCount()));
	}

	/**
	 * Checking if category with provided name exists
	 * 
	 * @param name
	 *            Name to check
	 * @return True if category exists
	 */
	private boolean categoryExists(String name) {
		for (Category category : categories) {
			if (category.getName().compareToIgnoreCase(name) == 0) {
				return true;
			}
		}
		return false;
	}

	private class AddCategoryListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			String name = textCatName.getText();
			String pic = btnCategorySelectPic.getToolTipText();

			if (StringUtils.isEmpty(name)) {
				JOptionPane.showMessageDialog(parentFrame,
						"Введите имя категории", "Немогу добавить :(",
						JOptionPane.ERROR_MESSAGE);
			} else if (categoryExists(name)) {
				JOptionPane.showMessageDialog(parentFrame,
						"Категория с именем '" + name + "' уже существует.",
						"Немогу добавить :(", JOptionPane.ERROR_MESSAGE);
			} else {
				Category cat = new Category();
				cat.setName(name);
				cat.setPicture(Utils.copyToPicturesDir(pic));

				try {
					categoryService.save(cat);
					if (log.isDebugEnabled()) {
						log.debug("Category added: " + cat);
					}

					clearCategoryAddValues();
					addCategoryTab(cat);
					tabsCategories.setSelectedIndex(categories.size() - 1);
				} catch (Exception e) {
					log.error(e);
					JOptionPane.showMessageDialog(parentFrame, e
							.getLocalizedMessage(), "Ошибка при добавлении",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class AddItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = textItemName.getText();
			String pic = btnItemSelectPic.getToolTipText();
			Integer mass = (Integer) spinItemMass.getValue();
			Double price = (Double) textItemPrice.getValue();

			Category cat = categories.get(tabsCategories.getSelectedIndex());

			if (StringUtils.isEmpty(name)) {
				JOptionPane.showMessageDialog(parentFrame,
						"Введите имя элемента", "Немогу добавить :(",
						JOptionPane.ERROR_MESSAGE);
			} else if (cat.itemExists(name)) {
				JOptionPane.showMessageDialog(parentFrame, "Элемент с именем '"
						+ name + "' уже существует в категории "
						+ cat.getName(), "Немогу добавить :(",
						JOptionPane.ERROR_MESSAGE);
			} else {
				Item item = new Item();
				item.setName(name);
				item.setPicture(Utils.copyToPicturesDir(pic));
				if (checkSingleItem.isSelected()) {
					item.setMass(AbstractItem.SINGLE_ITEM_NO_MASS);
				} else {
					item.setMass(mass);
				}
				item.setPriceBillsAndCoins(price);
				if (checkItemDrink.isSelected()) {
					if (checkItemAlcohol.isSelected()) {
						item.setItemType(ItemType.ALCOHOL);
					} else {
						item.setItemType(ItemType.SOFT_DRINK);
					}
				} else {
					if (checkItemBar.isSelected()) {
						item.setItemType(ItemType.BAR);
					} else {
						item.setItemType(ItemType.FOOD);
					}
				}

				cat.getItems().add(item);

				TableModelItemEdit tableModel = tableModels.get(tabsCategories
						.getSelectedIndex());

				try {
					itemService.save(item);
					categoryService.save(cat);

					clearItemAddValues();
					tableModel.setCategory(cat);
					tableModel.fireTableDataChanged();

					updateTotalItemCount();
				} catch (Exception e1) {
					log.error(e);
					JOptionPane.showMessageDialog(parentFrame, e1
							.getLocalizedMessage(),
							"Не получилось добавить :(",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		}
	}

	private class AddImageListener implements ActionListener {
		private JComponent component;
		private JLabel picView;

		AddImageListener(JComponent component, JLabel picView) {
			this.component = component;
			this.picView = picView;
		}

		public void actionPerformed(ActionEvent e) {
			String filename = CropperDialog.selectImageAndCrop(parentFrame);
			if (StringUtils.isNotEmpty(filename)) {
				component.setToolTipText(filename);
				picView.setText("");
				picView.setIcon(new ImageIcon(filename));
			}
		}
	}

	private class DeleteCategoryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DeleteCategoryAction() {
			super("Удалить категорию");
		}

		public void actionPerformed(ActionEvent e) {
			Category cat = categories.get(tabsCategories.getSelectedIndex());
			StringBuilder sb = new StringBuilder("Удаляем категорию '");
			sb.append(cat.getName());
			sb.append("'?\n\n");
			if (!cat.getItems().isEmpty()) {
				sb.append("Внимание! Будут также удалены все (");
				sb.append(cat.getItems().size());
				sb.append(") элементы этой категории!");
			}
			if (JOptionPane.showConfirmDialog(parentFrame, sb.toString(),
					"Точно?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

				try {
					categoryService.delEntity(cat);

					categories.remove(cat);
					tableModels.remove(tabsCategories.getSelectedIndex());

					tabsCategories.remove(tabsCategories.getSelectedIndex());

					updateTotalItemCount();
				} catch (Exception e1) {
					log.error("Failed to delete category: " + cat, e1);
					JOptionPane.showMessageDialog(parentFrame, e1
							.getLocalizedMessage(),
							"Не смог удалить категорию :(",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		}
	}

	private class ChangeCategoryNameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ChangeCategoryNameAction() {
			super("Переименовать категорию");
		}

		public void actionPerformed(ActionEvent e) {
			Category cat = categories.get(tabsCategories.getSelectedIndex());

			String newName = JOptionPane.showInputDialog(parentFrame,
					"Новое имя:", "Переименовываем категорию",
					JOptionPane.INFORMATION_MESSAGE);
			if (StringUtils.isNotEmpty(newName)) {

				if (categoryExists(newName)) {
					JOptionPane.showMessageDialog(parentFrame,
							"Категория с именем '" + newName
									+ "' уже существует.",
							"Немогу переименовать :(",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				cat.setName(newName);
				try {
					categoryService.save(cat);

					tabsCategories.setTitleAt(
							tabsCategories.getSelectedIndex(), newName);

					updateStatisticPanel();
				} catch (Exception e1) {
					log.error("Failed to change picture for: " + cat, e1);
					JOptionPane.showMessageDialog(parentFrame, e1
							.getLocalizedMessage(),
							"Не смог заменить картинку :(",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class DeleteCategoryPicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DeleteCategoryPicAction() {
			super("Удалить картинку");
		}

		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(parentFrame, "Удаляем картинку?",
					"Точно?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				Category cat = categories
						.get(tabsCategories.getSelectedIndex());

				cat.setPicture("");
				try {
					categoryService.save(cat);
					updateStatisticPanel();
					updateTotalItemCount();
				} catch (Exception e1) {
					log.error("Failed to change picture for: " + cat, e1);
					JOptionPane.showMessageDialog(parentFrame, e1
							.getLocalizedMessage(),
							"Не смог заменить картинку :(",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class ChangeCategoryPicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ChangeCategoryPicAction() {
			super("Изменить картинку");
		}

		public void actionPerformed(ActionEvent e) {
			String filename = CropperDialog.selectImageAndCrop(parentFrame);
			if (StringUtils.isNotEmpty(filename)) {
				Category cat = categories
						.get(tabsCategories.getSelectedIndex());

				Utils.deleteImageFile(cat);

				cat.setPicture(Utils.copyToPicturesDir(filename));
				try {
					categoryService.save(cat);
					updateStatisticPanel();
				} catch (Exception e1) {
					log.error("Failed to change picture for: " + cat, e1);
					JOptionPane.showMessageDialog(parentFrame, e1
							.getLocalizedMessage(),
							"Не смог заменить картинку :(",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

}
