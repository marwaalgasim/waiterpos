package ua.cn.yet.waiter.ui;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ua.cn.yet.waiter.FormListener;

public abstract class AbstractForm {

	protected Log log = LogFactory.getLog(getClass());

	private static String LOOKANDFEEL = "GTK+";

	public static final boolean RIGHT_TO_LEFT = false;

	protected JFrame frame = null;

	protected String appTitle;

	private FormListener formListener;

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String name) {
		java.net.URL imgURL = AbstractForm.class.getResource("images/" + name);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Cannot find image images/" + name); //$NON-NLS-1$
			return new ImageIcon();
		}
	}

	/**
	 * Create the class and frame
	 */
	public AbstractForm(String title, FormListener formListener) {
		super();
		this.appTitle = title;
		this.formListener = formListener;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	protected void createAndShowGUI() {
		// set the look and feel.
		initLookAndFeel();

		// Make sure we have new window decorations
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window
		frame = new JFrame(appTitle);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				closeWindow();
			}
		});

		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		frame.setMaximizedBounds(env.getMaximumWindowBounds()); // taskbar not
		// covered

		frame.setIconImage(createImageIcon("waiter.png").getImage()); //$NON-NLS-1$

		configureFrameSize();
		
		configureFrameAdditionalOptions();

		if (RIGHT_TO_LEFT) {
			frame.getContentPane().setComponentOrientation(
					ComponentOrientation.RIGHT_TO_LEFT);
		}

		createComponents(frame.getContentPane());

		frame.pack();

		// Center window on the screen
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Configuring frame size to default dimensions. Override this method for
	 * different size
	 */
	protected void configureFrameSize() {
		frame.setBounds(0, 0, 1100, 700);

		frame.setPreferredSize(new Dimension(1100, 700));
	}
	
	/**
	 * Configure additional options for the frame
	 */
	protected void configureFrameAdditionalOptions() {}

	/**
	 * Creating components for the frame
	 * 
	 * @param contentPane
	 *            Pane to add components
	 */
	protected abstract void createComponents(Container contentPane);
	
	/**
	 * Notify listeners that this window will close
	 */
	protected void closeWindow() {
		formListener.formClosing(AbstractForm.this);
	}

	/**
	 * Initializing look and feel for the application
	 */
	private void initLookAndFeel() {
		String lookAndFeel = null;
		if (LOOKANDFEEL != null) {
			if (LOOKANDFEEL.equals("Metal")) { //$NON-NLS-1$
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			} else if (LOOKANDFEEL.equals("System")) { //$NON-NLS-1$
				lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			} else if (LOOKANDFEEL.equals("Motif")) { //$NON-NLS-1$
				lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel"; //$NON-NLS-1$
			} else if (LOOKANDFEEL.equals("GTK+")) { // new in 1.4.2  //$NON-NLS-1$
				lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; //$NON-NLS-1$
			} else {
				// System.err.println(Messages
				//						.getString("WaiterForm.UnexpectedLFValue") //$NON-NLS-1$
				// + LOOKANDFEEL);
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			}

			try {
				UIManager.setLookAndFeel(lookAndFeel);
			} catch (ClassNotFoundException e) {
				//				System.err.println(Messages.getString("WaiterForm.NoClassForLF") //$NON-NLS-1$
				// + lookAndFeel);
				//				System.err.println(Messages.getString("WaiterForm.LibLFThere")); //$NON-NLS-1$
				//				System.err.println(Messages.getString("WaiterForm.UseDefLF")); //$NON-NLS-1$
			} catch (UnsupportedLookAndFeelException e) {
				//				System.err.println(Messages.getString("WaiterForm.UnableUseLF") //$NON-NLS-1$
				// + lookAndFeel
				//						+ Messages.getString("WaiterForm.OnThisPlatform")); //$NON-NLS-1$
				//				System.err.println(Messages.getString("WaiterForm.UseDefLF")); //$NON-NLS-1$
			} catch (Exception e) {
				//				System.err.println(Messages.getString("WaiterForm.UnableGetLF") //$NON-NLS-1$
				// + lookAndFeel
				//						+ Messages.getString("WaiterForm.ForSomeReason")); //$NON-NLS-1$
				//				System.err.println(Messages.getString("WaiterForm.UseDefLF")); //$NON-NLS-1$
				e.printStackTrace();
			}
		}
	}

	/**
	 * Displaying form
	 */
	public void show() {
		frame.setVisible(true);
		// Center window on the screen
		frame.setLocationRelativeTo(null);		
	}

	/**
	 * Hiding form
	 */
	public void hide() {
		frame.setVisible(false);
	}
	
	/**
	 * Activating running form
	 */
	public void activate() {
		frame.setExtendedState(JFrame.NORMAL);
	}

	/**
	 * Disposing form
	 */
	public void dispose() {
		frame.dispose();		
	}

}
