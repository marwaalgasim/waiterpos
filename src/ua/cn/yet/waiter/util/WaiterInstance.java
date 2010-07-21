package ua.cn.yet.waiter.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class WaiterInstance {
	
	private static Log log = LogFactory.getLog(WaiterInstance.class);
	
	public static final String CATEGORY_SERVICE = "categoryService";
	public static final String ITEM_SERVICE = "itemService";
	public static final String USER_SERVICE = "userService";
	public static final String ORDER_SERVICE = "orderService";
	public static final String ORDERED_ITEM_SERVICE = "orderedItemService";
	public static final String PRINTING_SERVICE = "printingService";
	
	private static WaiterInstance instance = new WaiterInstance();
	private ApplicationContext ac;
	
	private WaiterInstance() {
		ac = new ClassPathXmlApplicationContext("spring_config.xml");
	}
	
	public static void loadInstances() {
		if (instance != null) {
			log.info("Waiter instances are loaded");
		} else {
			log.fatal("Waiter instances are NOT loaded");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T forId(String id) {
		if (instance.ac != null) {
			if (log.isTraceEnabled()) {
				log.trace("Getting instance: " + id);
			}
			return (T) instance.ac.getBean(id);
		}
		return null;
	}

}
