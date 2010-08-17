package ua.cn.yet.waiter.ui.components;

import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;


public class QuickSearchTextField extends JTextField 
			implements FocusListener{

	private static final long serialVersionUID = 1L;
	
	private String defaultLabel = "";

	public QuickSearchTextField() {
		super();
		init();
	}
	
	public QuickSearchTextField(String defaultLabel) {
		super(defaultLabel);
		this.defaultLabel=defaultLabel;
		init();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		switchToManual();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		if(getText().length() == 0){
			switchToDefault();
		}
	}
	
	private void init(){
		addFocusListener(this);
		setToolTipText(defaultLabel);		
		switchToDefault();
	}
	
	private void switchToDefault(){
		setText(defaultLabel);
		setFont(getFont().deriveFont(Font.ITALIC));
	}
	
	private void switchToManual(){
		setText("");
		setFont(getFont().deriveFont(Font.PLAIN));
	}
}
