package ua.cn.yet.waiter.service;

import java.util.Collection;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.OutputElement;

public interface CategoryService extends GenericService<Category> {
	
	public Collection<OutputElement> getAllSortedAsOutputElements();

}
