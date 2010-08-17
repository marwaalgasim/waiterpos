package ua.cn.yet.waiter.ui.table.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ua.cn.yet.waiter.model.Item;
import ua.cn.yet.waiter.model.LoggedChange;
import ua.cn.yet.waiter.model.Order;
import ua.cn.yet.waiter.model.OrderedItem;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.LoggedChangeService;
import ua.cn.yet.waiter.service.OrderedItemService;

import static org.junit.Assert.*;


public class TestTableModelReceiptLogging {
	
	private TableModelReceipt tableModelReceipt;
	
	/** My mock db entry for OrderedItem entities */
	private List<OrderedItem> savedOrderedItems = new ArrayList<OrderedItem>();
	
	private Order order;
	private OrderedItem orderedItem;
	
	@Before
	public void prepareOrder(){
		order = new Order();
		order.setPrinted(true);
		Item item = new Item();
		item.setName("test item");
		item.setMass(200);
		item.setPrice(BigDecimal.valueOf(10));
		orderedItem = new OrderedItem(item, order, 100, 200);
		orderedItem.setId(Long.valueOf(1));
		order.getItems().add(orderedItem);
		savedOrderedItems.add(orderedItem);
		
		tableModelReceipt= new TableModelReceipt(true);
		tableModelReceipt.setLoggedChangeService(new MockLoggedChangeService());
		tableModelReceipt.setOrderedItemService(new MockOrderedItemService());
		tableModelReceipt.getItems().add(orderedItem);
	}
	
	@Test
	public void testItemDeletionLogging(){
		tableModelReceipt.deleteItem(orderedItem);
		assertEquals(1, order.getChanges().size());
	}
	
	@Test
	public void testItemChangeLogging(){
		//instantiating new ordered item in order for saved ordered item to stay untouched
		OrderedItem newOrderedItem = new OrderedItem();
		newOrderedItem.copyFieldsValuesFrom(orderedItem);
		newOrderedItem.setId(orderedItem.getId());
		tableModelReceipt.updateItemValue(orderedItem.getNewMass()-1, TableModelReceipt.COLUMN_MASS, newOrderedItem);
		assertEquals(1, order.getChanges().size());
	}
	
	private class MockOrderedItemService implements OrderedItemService{

		@Override
		public void delAllEntities(Collection<Long> ids)
				throws IllegalArgumentException, GeneralServiceException {			
		}

		@Override
		public void delEntity(Long id) throws IllegalArgumentException,
				GeneralServiceException {			
		}

		@Override
		public void delEntity(OrderedItem entity)
				throws IllegalArgumentException, GeneralServiceException {			
		}

		@Override
		public List<OrderedItem> getAllEntites() {
			return null;
		}

		@Override
		public long getAllEntitiesCount() {
			return 0;
		}

		@Override
		public List<OrderedItem> getAllSorted(String propertySortBy, boolean asc)
				throws IllegalArgumentException {
			return null;
		}

		@Override
		public List<OrderedItem> getEntitiesByIds(List<Long> ids)
				throws IllegalArgumentException {
			return null;
		}

		@Override
		public OrderedItem getEntityById(Long id)
				throws IllegalArgumentException {
			for(OrderedItem savedOrderedItem:savedOrderedItems){
				if(savedOrderedItem.getId().equals(id)){
					return savedOrderedItem;
				}
			}
			return null;
		}

		@Override
		public OrderedItem save(OrderedItem entity)
				throws IllegalArgumentException, GeneralServiceException {
			return entity;
		}
		
	}
	
	private class MockLoggedChangeService implements LoggedChangeService{

		@Override
		public void delAllEntities(Collection<Long> ids)
				throws IllegalArgumentException, GeneralServiceException {			
		}

		@Override
		public void delEntity(Long id) throws IllegalArgumentException,
				GeneralServiceException {			
		}

		@Override
		public void delEntity(LoggedChange entity)
				throws IllegalArgumentException, GeneralServiceException {			
		}

		@Override
		public List<LoggedChange> getAllEntites() {
			return null;
		}

		@Override
		public long getAllEntitiesCount() {
			return 0;
		}

		@Override
		public List<LoggedChange> getAllSorted(String propertySortBy,
				boolean asc) throws IllegalArgumentException {
			return null;
		}

		@Override
		public List<LoggedChange> getEntitiesByIds(List<Long> ids)
				throws IllegalArgumentException {
			return null;
		}

		@Override
		public LoggedChange getEntityById(Long id)
				throws IllegalArgumentException {
			return null;
		}

		@Override
		public LoggedChange save(LoggedChange entity)
				throws IllegalArgumentException, GeneralServiceException {
			return null;
		}
		
	}
	
}
