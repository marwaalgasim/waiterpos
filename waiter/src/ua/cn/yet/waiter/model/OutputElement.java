package ua.cn.yet.waiter.model;

/**
 * Interface of the element (category or item) that can be
 * output into button
 * 
 * @author Yuriy Tkach
 */
public interface OutputElement {
	
	public String getName();
	
	public String getPicture();
	
	public boolean isDisabled();

}
