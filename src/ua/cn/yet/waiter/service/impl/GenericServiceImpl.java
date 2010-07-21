package ua.cn.yet.waiter.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ua.cn.yet.waiter.model.DomainObject;
import ua.cn.yet.waiter.service.GeneralServiceException;
import ua.cn.yet.waiter.service.GenericService;

/**
 * Generic service that implements {@link GenericService} methods
 * 
 * @author Yuriy Tkach
 * 
 * @param <T>
 *            Type of entity to work with
 */
public abstract class GenericServiceImpl<T extends DomainObject> implements
		GenericService<T> {

	private static final String QUERY_SELECT_BY_ID_LIST = "SELECT x FROM %s x WHERE x.id IN (?1)"; //$NON-NLS-1$

	private static final String QUERY_SELECT_ALL = "SELECT x FROM %s x"; //$NON-NLS-1$

	private static final String QUERY_COUNT_ALL = "SELECT COUNT(x) FROM %s x"; //$NON-NLS-1$

	/** Spring's jpa template that is used to access db */
	protected JpaTemplate jpaTemplate;

	/** Persistent class that this dao works with */
	protected Class<T> persistentClass;

	/** Log object used for logging */
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * Constructor with fields
	 * 
	 * @param persistentClass
	 *            Class that this dao will work with
	 */
	public GenericServiceImpl(Class<T> persistentClass) {
		super();
		this.persistentClass = persistentClass;
	}

	/**
	 * @return the jpaTemplate
	 */
	public JpaTemplate getJpaTemplate() {
		return jpaTemplate;
	}

	/**
	 * @param jpaTemplate
	 *            the jpaTemplate to set
	 */
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	/**
	 * <p>
	 * This template method executes query with performing all needed
	 * operations, like creating EntityManager, creating transaction,
	 * committing, or rolling it back.
	 * </p>
	 * <p>
	 * Methods sets parameters for the query as they appear in the parameters
	 * list, by number starting from 1. So, the first parameter in your named
	 * query should be referenced as <code>?1</code>, second as <code>?2</code>
	 * and so on.
	 * </p>
	 * <p>
	 * If <code>singleResult = true</code> and no result is found, then
	 * <code>null</code> is returned.
	 * </p>
	 * <p>
	 * Be aware, that when multiple results are returned, they are being
	 * dynamically casted to <code>REZ</code> class. It should be able to cast
	 * to {@link java.util.List}. When returning single result, <code>REZ</code>
	 * should be a single persistent entity class.
	 * </p>
	 * 
	 * @param <REZ>
	 *            Class of the result
	 * @param queryOrQueryName
	 *            Query string or NamedQuery name
	 * @param namedQuery
	 *            Specifies, whether query is named query
	 * @param singleResult
	 *            Specifies, whether single result should be returned
	 * @param initializeObjects
	 *            If true, then function will iterate through all object to
	 *            initialize them
	 * @param parameters
	 *            Parameters. You can specify multiple parameters separated by
	 *            comma
	 * @return Result of the query
	 * @throws PersistenceException
	 *             If error occurs
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	protected <REZ> REZ executeQuery(String queryOrQueryName,
			boolean namedQuery, boolean singleResult, Object... parameters) {

		if (StringUtils.isBlank(queryOrQueryName)) {
			throw new IllegalArgumentException(
					"Query for executing cannot be null");
		}

		if (log.isDebugEnabled()) {
			log
					.debug(String
							.format(
									"Executing query '%s' to return single result '%s'", //$NON-NLS-1$
									queryOrQueryName, singleResult));
		}

		REZ result;
		List<?> list;

		// Executing either named or simple query
		if (namedQuery) {
			list = jpaTemplate.findByNamedQuery(queryOrQueryName, parameters);
		} else {
			list = jpaTemplate.find(queryOrQueryName, parameters);
		}

		// Returning needed result
		if (singleResult) {
			if (CollectionUtils.isNotEmpty(list)) {
				result = (REZ) list.get(0);
			} else {
				result = null;
			}
		} else {
			result = (REZ) list;
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	protected int executeUpdateQuery(final String queryOrQueryName,
			final boolean namedQuery, final Object... parameters)
			throws IllegalArgumentException, GeneralServiceException {

		if (StringUtils.isBlank(queryOrQueryName)) {
			throw new IllegalArgumentException(
					"Query for executing cannot be null");
		}

		if (log.isDebugEnabled()) {
			log
					.debug(String
							.format(
									"Executing update query '%s' with params %s", //$NON-NLS-1$
									queryOrQueryName, ArrayUtils
											.toString(parameters)));
		}

		try {
			Object rez = jpaTemplate.execute(new JpaCallback() {

				@Override
				public Object doInJpa(EntityManager em)
						throws PersistenceException {
					Query query;

					if (namedQuery) {
						query = em.createNamedQuery(queryOrQueryName);
					} else {
						query = em.createQuery(queryOrQueryName);
					}
					if (parameters.length > 0) {
						for (int i = 0; i < parameters.length; i++) {
							query.setParameter(i + 1, parameters[i]);
						}
					}

					return Integer.valueOf(query.executeUpdate());
				}
			});

			return ((Integer) rez).intValue();
		} catch (Exception e) {
			throw new GeneralServiceException(e);
		}
	}

	/**
	 * Method that is called, before entity is being updated or added. Method
	 * must be overwritten in subclasses to make some checks.
	 * 
	 * @param entity
	 *            Entity that will be added or updated
	 * @throws GeneralServiceException
	 *             If error occurs
	 */
	protected abstract void beforeEntityAddUpdate(T entity)
			throws GeneralServiceException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#getAllEntitiesCount()
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getAllEntitiesCount() {
		try {
			return executeQuery(String.format(QUERY_COUNT_ALL, persistentClass
					.getSimpleName()), false, true);
		} catch (Exception e) {
			log.error("Failed to get all entities count", e); //$NON-NLS-1$
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ua.cn.yet.birthdayGifts.services.IGenericService#save(ua.cn.yet.birthdayGifts
	 * .domain.DomainObject)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public T save(T entity) throws IllegalArgumentException,
			GeneralServiceException {
		if (entity == null) {
			throw new IllegalArgumentException(
					"Entity for saving cannot be null!");
		}

		T savedEntity = null;

		try {

			if (entity.getId() == null) {

				if (log.isDebugEnabled()) {
					log.debug("Saving new entity: " + entity); //$NON-NLS-1$
				}

				jpaTemplate.persist(entity);
				savedEntity = entity;
			} else {

				if (log.isDebugEnabled()) {
					log.debug("Updating entity: " + entity); //$NON-NLS-1$
				}

				savedEntity = jpaTemplate.merge(entity);
			}

		} catch (Exception e) {
			if (entity.getId() == null) {
				throw new GeneralServiceException("Failed to add entity "
						+ entity.getClass().getSimpleName(), e); //$NON-NLS-1$
			} else {
				throw new GeneralServiceException("Failed to update entity "
						+ entity.getClass().getSimpleName() + " with id"
						+ entity.getId(), e);
			}
		}

		return savedEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#delEntity(java.lang
	 * .Long)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delEntity(Long id) throws IllegalArgumentException,
			GeneralServiceException {
		if (id == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}

		// Getting entity by id
		T savedEntity = getEntityById(id);

		if (savedEntity != null) {
			// Deleting entity
			delEntity(savedEntity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#delEntity(biz.smartymedia
	 * .common.domain.DomainObject)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delEntity(T entity) throws IllegalArgumentException,
			GeneralServiceException {
		if (entity == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}

		if (entity.getId() == null) {
			log.warn("Trying to delete transient entity: " + entity); //$NON-NLS-1$
			return;
		}

		beforeEntityDelete(entity);

		try {
			// Deleting entity object
			if (!jpaTemplate.contains(entity)) {
				if (log.isDebugEnabled()) {
					log.debug("Attaching detached entity before deleting " //$NON-NLS-1$
							+ entity);
				}
				T attachedEntity = jpaTemplate.merge(entity);
				jpaTemplate.remove(attachedEntity);
			} else {
				jpaTemplate.remove(entity);
			}
		} catch (Exception e) {
			// Catching all runtime exceptions that might occur when operating
			// with entity. Returning our service exception in such case
			throw new GeneralServiceException("Failed to delete entity "
					+ entity.getClass().getSimpleName() + " with id "
					+ entity.getId(), e);
		}
	}

	/**
	 * Method that is called just before entity is deleted
	 * 
	 * @param entity
	 *            Entity to delete
	 * @throws GeneralServiceException
	 *             If error occurs
	 */
	protected abstract void beforeEntityDelete(T entity)
			throws GeneralServiceException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#delAllEntities(java
	 * .util.Collection)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delAllEntities(Collection<Long> ids)
			throws IllegalArgumentException, GeneralServiceException {

		if (ids == null) {
			throw new IllegalArgumentException("Argument is null");
		}

		if (!ids.isEmpty()) {

			for (Long id : ids) {
				T savedEntity = getEntityById(id);

				if (savedEntity != null) {
					// Deleting entity
					delEntity(savedEntity);
				} else {
					// Doing nothing if entity is not found
					log
							.info("Entity with id " + id + " was not deleted, because it was not found"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see biz.smartymedia.common.service.IGenericServiceNew#getAllEntites()
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getAllEntites() {
		try {
			// Getting all entities
			List<T> list = executeQuery(String.format(QUERY_SELECT_ALL,
					persistentClass.getSimpleName()), false, false);
			if (list != null)
				return list;
			else
				return new ArrayList<T>();

		} catch (Exception e) {
			log.error("Failed to get list of all entities", e); //$NON-NLS-1$
			return new ArrayList<T>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#getEntityById(java.
	 * lang.Long)
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T getEntityById(Long id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("Entity is null");
		}
		if (log.isDebugEnabled()) {
			log.debug("Getting entity " + persistentClass.getSimpleName() //$NON-NLS-1$
					+ " by id " + id); //$NON-NLS-1$
		}

		try {

			// Getting entity by id
			T savedEntity = jpaTemplate.find(persistentClass, id);

			// Checking if we got the entity that is not NULL
			if ((savedEntity == null) || (savedEntity.getId() == null)) {
				StringBuilder mess = new StringBuilder("Entity by id "); //$NON-NLS-1$

				mess.append(id).append(" was not found"); //$NON-NLS-1$

				log.warn(mess.toString());

				return null;
			}

			return savedEntity;
		} catch (Exception e) {
			log.error("Failed to get entity by id " + id, e); //$NON-NLS-1$
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#getAllSorted(java.lang
	 * .String, boolean)
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getAllSorted(String propertySortBy, boolean asc)
			throws IllegalArgumentException {
		if (StringUtils.isBlank(propertySortBy)) {
			throw new IllegalArgumentException("SortBy is null"); //$NON-NLS-1$
		}

		try {
			String query = String.format(QUERY_SELECT_ALL, persistentClass
					.getSimpleName());

			String newQuery = addSortPropertyToQuery(query, propertySortBy, asc);

			return executeQuery(newQuery, false, false);
		} catch (Exception e) {
			log.error("Failed to get sorted entities", e);
			return new ArrayList<T>();
		}
	}

	/**
	 * Appending sort information to query and returning new query
	 * 
	 * @param query
	 *            Query to append with sort information
	 * @param propertySortBy
	 *            Property to sort by
	 * @param asc
	 *            Specifies sort direction
	 * @return new query with added sort
	 */
	protected String addSortPropertyToQuery(String query,
			String propertySortBy, boolean asc) {
		StringBuilder sb = new StringBuilder();
		sb.append(query);
		sb.append(" "); //$NON-NLS-1$
		sb.append("order by "); //$NON-NLS-1$
		sb.append(propertySortBy);

		if (asc) {
			sb.append(" asc"); //$NON-NLS-1$
		} else {
			sb.append(" desc"); //$NON-NLS-1$
		}

		String newQuery = sb.toString();
		return newQuery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * biz.smartymedia.common.service.IGenericServiceNew#getEntitiesByIds(java
	 * .util.List)
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getEntitiesByIds(List<Long> ids)
			throws IllegalArgumentException {

		if (ids == null) {
			throw new IllegalArgumentException("List is null"); //$NON-NLS-1$
		}

		if (ids.isEmpty()) {
			return new ArrayList<T>();
		}

		try {
			return executeQuery(String.format(QUERY_SELECT_BY_ID_LIST,
					persistentClass.getSimpleName()), false, false, ids);
		} catch (Exception e) {
			log.error("failed to get entities by id", e);
			return new ArrayList<T>();
		}
	}
}
