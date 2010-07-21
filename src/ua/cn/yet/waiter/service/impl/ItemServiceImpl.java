package ua.cn.yet.waiter.service.impl;

import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.ItemService;

public class ItemServiceImpl extends GenericServiceImpl<Item> implements
		ItemService {

	public ItemServiceImpl() {
		super(Item.class);
	}

	@Override
	protected void beforeEntityAddUpdate(Item entity)
			throws GeneralServiceException {
	}

	@Override
	protected void beforeEntityDelete(Item entity)
			throws GeneralServiceException {
	}

}
