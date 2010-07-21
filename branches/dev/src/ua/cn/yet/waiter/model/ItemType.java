package ua.cn.yet.waiter.model;

public enum ItemType {
	FOOD ("Еда"), 
	SOFT_DRINK ("Напиток"), 
	ALCOHOL ("Алкоголь"), 
	BAR ("Бар");
	
	private String name;
	
	private ItemType(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
}