package ua.cn.yet.waiter.ui;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog for input of reason for deletion
 * 
 * @author Yuriy Tkach
 */
public class ReasonForDelInputDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private String reason;

	private JComboBox comboReason;
	
	public static String getReason(Window parent) {
		ReasonForDelInputDialog dialog = new ReasonForDelInputDialog(parent);
		dialog.setVisible(true);
		return dialog.reason;
	}
	
	private ReasonForDelInputDialog(Window parent) {
		super(parent, "Причина для удаления", ModalityType.APPLICATION_MODAL);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		this.setLayout(new MigLayout("insets 5"));
		
		this.add(new JLabel("Выберите или введите причину для удаления заказа:"), "wrap");
		
		this.add(createReasonCombo(), "growx, wrap");
		
		this.add(createButtonPanel(),"growx");
		
		this.pack();
		this.setLocationRelativeTo(parent);
	}

	/**
	 * @return Panel with OK and Cancel buttons
	 */
	private Component createButtonPanel() {
		JPanel btnPanel = new JPanel(new MigLayout("insets 0",
				"[right,grow][right]", "[]"));
		add(btnPanel);

		//btnPanel.add(new JLabel());

		JButton btnOK = new JButton("ОК");
		btnPanel.add(btnOK, "w 100::");
		btnOK.setIcon(AbstractForm.createImageIcon("ok.png"));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reason = String.valueOf(comboReason.getSelectedItem());
				ReasonForDelInputDialog.this.dispose();
			}
		});
		
		JButton btnCancel = new JButton("Отмена");
		btnCancel.setIcon(AbstractForm.createImageIcon("no.png"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reason = "";
				ReasonForDelInputDialog.this.dispose();
			}
		});
		btnPanel.add(btnCancel,"w 100::");
		
		return btnPanel;
	}

	/**
	 * @return Editable combobox with reasons
	 */
	private Component createReasonCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement("Неправильно сформированный заказ");
		model.addElement("Тестовый заказ. Тренировка");
		model.addElement("Случайно закрытый заказ. Не все учтено");
		
		comboReason = new JComboBox(model);
		comboReason.setEditable(true);
		comboReason.setSelectedIndex(0);
		
		return comboReason;
	}

}
