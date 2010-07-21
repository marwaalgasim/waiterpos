package ua.cn.yet.waiter.service.impl;

import java.util.Collection;
import java.util.LinkedList;

import ua.cn.yet.waiter.model.Category;
import ua.cn.yet.waiter.model.OutputElement;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.CategoryService;

public class CategoryServiceImpl extends GenericServiceImpl<Category> implements
		CategoryService {

	public CategoryServiceImpl() {
		super(Category.class);
	}

	@Override
	protected void beforeEntityAddUpdate(Category entity)
			throws GeneralServiceException {
	}

	@Override
	protected void beforeEntityDelete(Category entity)
			throws GeneralServiceException {
	}

	@Override
	public Collection<OutputElement> getAllSortedAsOutputElements() {
		Collection<OutputElement> rez = new LinkedList<OutputElement>(
				getAllSorted("name", true));
		return rez;
	}

}
