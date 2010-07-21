package ua.cn.yet.waiter.ui.table.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.apache.commons.lang.StringUtils;

import ua.cn.yet.common.ui.popup.PopupFactory;
import ua.cn.yet.common.ui.popup.PopupListener;
import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.ui.CropperDialog;
import ua.cn.yet.waiter.util.Utils;

public class ColumnPicEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;

	//private static Log log = LogFactory.getLog(ColumnPicEditor.class);

	private JLabel editComponent;
	private JFrame parentFrame;

	private Item delItem = null;
	
	private String newPic = null;

	public ColumnPicEditor(JFrame parentFrame) {
		editComponent = new JLabel();
		// editComponent.addMouseListener(new EditMouseListener());
		editComponent.addMouseListener(new PopupListener(PopupFactory
				.getGeneralPopup(new ChangePicAction("Изменить картинку"),
						null, new DelPicAction("Удалить картинку"))));
		this.parentFrame = parentFrame;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		delItem = (Item) value;
		editComponent.setIcon(Utils.getImageIconForElem(delItem));
		newPic = null;
		return editComponent;
	}

	@Override
	public Object getCellEditorValue() {
		return newPic;
	}

	private class DelPicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DelPicAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(parentFrame, "Удаляем картинку?",
					"Точно?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				newPic = "";
				fireEditingStopped(); // Make the renderer reappear.
			} else {
				fireEditingCanceled(); // Make the renderer reappear.
			}
		}
	}

	private class ChangePicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ChangePicAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String filename = CropperDialog.selectImageAndCrop(parentFrame);
			if (StringUtils.isNotEmpty(filename)) {
				newPic = filename;
				fireEditingStopped(); // Make the renderer reappear.
			}
			fireEditingCanceled(); // Make the renderer reappear.
		}
	}

}
