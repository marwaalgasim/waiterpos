package ua.cn.yet.waiter.service.impl;

import ua.cn.yet.waiter.model.LoggedChange;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.LoggedChangeService;

public class LoggedChangeServiceImpl extends GenericServiceImpl<LoggedChange> implements LoggedChangeService{

	public LoggedChangeServiceImpl() {
		super(LoggedChange.class);
	}

	@Override
	protected void beforeEntityAddUpdate(LoggedChange entity)
			throws GeneralServiceException {
		
	}

	@Override
	protected void beforeEntityDelete(LoggedChange entity)
			throws GeneralServiceException {
		entity.getOrder().getChanges().remove(entity);
		
	}

}
