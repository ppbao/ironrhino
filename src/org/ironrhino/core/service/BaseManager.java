package org.ironrhino.core.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.ironrhino.core.model.BaseTreeableEntity;
import org.ironrhino.core.model.Persistable;
import org.ironrhino.core.model.ResultPage;
import org.springframework.orm.hibernate5.HibernateCallback;

public interface BaseManager<T extends Persistable<?>> {

	public Class<? extends Persistable<?>> getEntityClass();

	public void save(T obj);

	public void update(T obj);

	public T get(Serializable id);

	public T get(Serializable id, LockOptions lockOptions);

	public List<T> get(List<? extends Serializable> ids);

	public void evict(T obj);

	public void delete(T obj);

	public List<T> delete(Serializable... id);

	public DetachedCriteria detachedCriteria();

	public long countByCriteria(DetachedCriteria dc);

	public T findByCriteria(DetachedCriteria dc);

	public List<T> findListByCriteria(DetachedCriteria dc);

	public List<T> findBetweenListByCriteria(final DetachedCriteria dc, int from, int end);

	public List<T> findListByCriteria(DetachedCriteria dc, int pageNo, int pageSize);

	public ResultPage<T> findByResultPage(ResultPage<T> resultPage);

	public long countAll();

	public T findByNaturalId(Serializable... objects);

	public T findOne(Serializable... objects);

	public T findOne(boolean caseInsensitive, Serializable... objects);

	public List<T> findAll(Order... orders);

	public List<T> find(String queryString, Object... args);

	public <TE extends BaseTreeableEntity<TE>> TE loadTree();

	public int executeUpdate(String queryString, Object... args);

	@Deprecated
	public List<T> executeQuery(String queryString, Object... args);

	public <K> K execute(HibernateCallback<K> callback);

	public <K> K executeFind(HibernateCallback<K> callback);

	public long iterate(int fetchSize, IterateCallback callback);

	public long iterate(int fetchSize, IterateCallback callback, DetachedCriteria dc);

	public long iterate(int fetchSize, IterateCallback callback, DetachedCriteria dc, boolean commitPerFetch);

	@FunctionalInterface
	public static interface IterateCallback {
		public void process(Object[] entityArray, Session session);
	}

}
