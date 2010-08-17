package ua.cn.yet.waiter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class that loads configuration from the properties file
 * 
 * @author Yuriy Tkach
 */
public class Config {
	
	public static final String SHOW_PRINT_DIALOG = "show.print.dialog";
	public static final String SET_PRINT_CLIPPING = "set.print.clipping";
	public static final String SHOW_CHANGE_COUNT_ON_ORDER_CLOSE = "show.change.count.on.order.close";
	public static final String TABLE_COUNT="table.count";
	public static final String TABLE_COL_COUNT="table.col.count";
	public static final String DISCOUNT_VALUES="discount.values";

	private static Log log = LogFactory.getLog(Config.class);

	private static Config instance = new Config();

	private Properties props = new Properties();

	private Config() {
		init();
	}

	/**
	 * Initializing properties
	 */
	private void init() {
		String configDir = System.getProperty("configDir");
		if (StringUtils.isNotEmpty(configDir)) {
			String propsFileName = configDir + File.separatorChar
					+ "config.properties";
			InputStream is = null;
			try {
				is = new FileInputStream(propsFileName);
				props.load(is);
			} catch (FileNotFoundException e) {
				log.error("Properties file not found " + propsFileName, e);
			} catch (IOException e) {
				log.error("Failed to load properties", e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		} else {
			log
					.error("Configuration directory is not specified in system properties."
							+ "Add -DconfigDir=[dir] parameter to the program arguments.");
		}
	}

	/**
	 * Getting string value for the configuration key
	 * 
	 * @param key
	 *            Key to get value for
	 * @return String value or empty string if not found
	 */
	public static String getString(String key) {
		return instance.props.getProperty(key, "");
	}

	/**
	 * Getting boolean value for the configuration key
	 * 
	 * @param key
	 *            Key to get value for
	 * @return Boolean value or <code>false</code> if not found
	 */
	public static boolean getBoolean(String key) {
		String val = getString(key);
		return BooleanUtils.toBoolean(val);
	}
	
	/**
	 * Getting integer value for the configuration key
	 * 
	 * @param key
	 *            Key to get value for
	 * @return Integer value or <code>0 (zero)</code> if not found
	 */
	public static int getInteger(String key) {
		String val = getString(key);
		return NumberUtils.toInt(val);
	}
	
	/**
	 * Getting double value for the configuration key
	 * 
	 * @param key
	 *            Key to get value for
	 * @return Double value or <code>0.0 (zero)</code> if not found
	 */
	public static double getDouble(String key) {
		String val = getString(key);
		return NumberUtils.toDouble(val);
	}

}
