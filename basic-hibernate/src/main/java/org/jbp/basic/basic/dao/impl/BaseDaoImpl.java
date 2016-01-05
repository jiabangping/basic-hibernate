/**
 * 
 */
package org.jbp.basic.basic.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbp.basic.basic.dao.IBaseDao;
import org.jbp.basic.basic.model.PageContext;
import org.jbp.basic.basic.model.Pager;

/**
 * @author root
 *  dao不做任何的异常处理，不做任何业务逻辑判断，只做对数据库的操作，所有的业务，事务在service层来做
 */
public class BaseDaoImpl<T> implements IBaseDao<T> {
	/**
	 * 在hibernate3中 spring整合 hibernate时 使用 hibernateDaoSupport，hibernate4中已经不用了,
	 * 4中只用hibernate 代码中不会出现的spring的东西，耦合低，不会spring可以灵活替换
	 */
	
	private SessionFactory sessionFactory;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	//在spring的老版本是用resource(jsr250)来注入，但是在新版本中不用了 使用 jsr330 @Inject 
	@Inject
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected Session getSession() {
		return sessionFactory.openSession();
	}
	//获取接口中的T泛型 Class，创建一个Class的对象来获取泛型的class
	private Class<T> clz;
	
	@SuppressWarnings("unchecked")
	public Class<T> getClz() {
		if(clz == null) {
			//获取泛型的Class对象
			clz = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return clz;
	}
	
	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#add(java.lang.Object)
	 */
	public T add(T t) {
		getSession().save(t);
		return t;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#update(java.lang.Object)
	 */
	public void update(T t) {
		getSession().update(t);
	}	

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#delete(int)
	 */
	public void delete(int id) {
		getSession().delete(this.load(id));
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#load(int)
	 */
	@SuppressWarnings("unchecked")
	public T load(int id) {
		//load 需要获取泛型 Class 对象
		return (T)getSession().load(getClz(), id);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String)
	 */
	public List<T> list(String hql) {
//		return this.list;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.lang.Object)
	 */
	public List<T> list(String hql, Object arg) {
		return this.list(hql, new Object[]{arg}, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.lang.Object[])
	 */
	public List<T> list(String hql, Object[] args) {
		return this.list(hql, args, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.util.Map)
	 */
	public List<T> listByAlias(String hql, Map<String, Object> alias) {
		return this.list(hql, null, alias);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.lang.Object[], java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		String order = PageContext.getOrder();
		String sort = PageContext.getSort();
		if(sort != null && !"".endsWith(sort.trim())) {
			hql += " order by "+sort;
			if(!"desc".equals(order)) {
				hql += " asc ";
			}else {
				hql += " desc ";
			}
		}
		
		Query query = getSession().createQuery(hql);
		
		if(alias != null) {//设置别名
			Set<String> keys = alias.keySet();
			for(String key : keys) {
				//基于别名的查询，分list和不是list两种
				Object val = alias.get(key);
				if(val instanceof Collection) {
					query.setParameterList(key, (Collection)val);
				}else {
					query.setParameter(key, val);
				}
			}
		}
		if(args != null && args.length > 0) {
			int index = 0;
			for(Object arg : args) {//设置占位符
				query.setParameter(index++, arg);
			}
		}
		
		return query.list();
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String)
	 */
	public List<T> find(String hql) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.lang.Object)
	 */
	public List<T> find(String hql, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.lang.Object[])
	 */
	public List<T> find(String hql, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.util.Map)
	 */
	public List<T> findByAlias(String hql, Map<String, Object> alias) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.lang.Object[], java.util.Map)
	 */
	public List<T> find(String hql, Object[] args, Map<String, Object> alias) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#queryObject(java.lang.String)
	 */
	public Object queryObject(String hql) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#queryObject(java.lang.String, java.lang.Object)
	 */
	public Object queryObject(String hql, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#queryObject(java.lang.String, java.lang.Object[])
	 */
	public Object queryObject(String hql, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#updateByHql(java.lang.String)
	 */
	public void updateByHql(String hql) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#updateByHql(java.lang.String, java.lang.Object)
	 */
	public void updateByHql(String hql, Object arg) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#updateByHql(java.lang.String, java.lang.Object[])
	 */
	public void updateByHql(String hql, Object[] args) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, boolean)
	 */
	public List<T> listBySql(String sql, Class<T> clz, boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.lang.Object, boolean)
	 */
	public List<T> listBySql(String sql, Class<T> clz, Object arg,
			boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 */
	public List<T> listBySql(String sql, Class<T> clz, Object[] args,
			boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.util.Map, boolean)
	 */
	public List<T> listBySql(String sql, Class<T> clz,
			Map<String, Object> alias, boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.lang.Object[], java.util.Map, boolean)
	 */
	public List<T> listBySql(String sql, Class<T> clz, Object[] args,
			Map<String, Object> alias, boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, boolean)
	 */
	public Pager<T> findBySql(String sql, Class<T> clz, boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.lang.Object, boolean)
	 */
	public Pager<T> findBySql(String sql, Class<T> clz, Object arg,
			boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 */
	public Pager<T> findBySql(String sql, Class<T> clz, Object[] args,
			boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.util.Map, boolean)
	 */
	public Pager<T> findBySql(String sql, Class<T> clz,
			Map<String, Object> alias, boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.lang.Object[], java.util.Map, boolean)
	 */
	public Pager<T> findBySql(String sql, Class<T> clz, Object[] args,
			Map<String, Object> alias, boolean hasEntity) {
		// TODO Auto-generated method stub
		return null;
	}

}
