package ua.cn.yet.waiter.ui.table.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class for column del editor. Renders del button, asks for
 * confirmation when clicked, catches exceptions
 * 
 * @author Yuriy Tkach
 */
public abstract class AbstractColumnDelEditor extends AbstractCellEditor
		implements TableCellEditor, ActionListener {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(ColumnItemDelEditor.class);

	private static final String DEL = "Удалить";
	private JButton button;

	private Object delValue;

	public AbstractColumnDelEditor() {
		button = new JButton();
		button.setActionCommand(DEL);
		button.addActionListener(this);
		button.setBorderPainted(false);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		delValue = value;
		return button;
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (DEL.equals(e.getActionCommand())) {

			Component c = (Component) e.getSource();

			if (JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(c), "Удаляем '"
					+ getDelObjectName(delValue) + "'?", "Точно?",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

				try {

					performObjectDelete(delValue);

				} catch (Exception e2) {
					log.error("Failed to delete: " + delValue, e2);
					JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(c), e2
							.getLocalizedMessage(), "Не смог удалить :(",
							JOptionPane.ERROR_MESSAGE);
				}
				fireEditingStopped(); // Make the renderer reappear.
			}
		}
		fireEditingCanceled(); // Make the renderer reappear.
	}

	/**
	 * Deleting object
	 * 
	 * @param delObject
	 *            Object to delete
	 */
	protected abstract void performObjectDelete(Object delObject)
			throws Exception;

	/**
	 * @param delObject
	 *            object to delete
	 * @return Object's human-readable name
	 */
	protected abstract String getDelObjectName(Object delObject);

}
