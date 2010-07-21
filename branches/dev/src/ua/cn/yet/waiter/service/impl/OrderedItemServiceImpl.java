package ua.cn.yet.waiter.service.impl;

import ua.cn.yet.waiter.model.OrderedItem;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.OrderedItemService;

public class OrderedItemServiceImpl extends GenericServiceImpl<OrderedItem> implements
		OrderedItemService {

	public OrderedItemServiceImpl() {
		super(OrderedItem.class);
	}

	@Override
	protected void beforeEntityAddUpdate(OrderedItem entity)
			throws GeneralServiceException {
	}

	@Override
	protected void beforeEntityDelete(OrderedItem entity)
			throws GeneralServiceException {
		entity.getOrder().getItems().remove(entity);
	}

	
}
