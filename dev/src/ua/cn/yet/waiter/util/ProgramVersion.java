package ua.cn.yet.waiter.util;

import java.util.ResourceBundle;

import ua.cn.yet.waiter.WaiterApp;

public class ProgramVersion {
	
	private static String version;
	
	static {
		version = ""; //$NON-NLS-1$
		try {
			ResourceBundle bundle = ResourceBundle.
				getBundle(WaiterApp.class.getPackage().getName() + ".build_number"); //$NON-NLS-1$
			version = bundle.getString("major.version.number") +  //$NON-NLS-1$
				"." + bundle.getString("minor.version.number") +  //$NON-NLS-1$ //$NON-NLS-2$
				"." + bundle.getString("build.number"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getVersion() {
		return version;
	}

}
