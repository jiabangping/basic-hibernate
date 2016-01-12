/**
 * 
 */
package org.jbp.basic.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.jbp.basic.dao.IBaseDao;
import org.jbp.basic.model.PageContext;
import org.jbp.basic.model.Pager;

/** 查询分为 通过Alias别名 和parameter两种方式，组合成多个方式
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
//		return sessionFactory.openSession();
		return sessionFactory.getCurrentSession();
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
	
	private String initSort(String hql) {
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
		return hql;
	}
	
	@SuppressWarnings("rawtypes")
	private void setAliasParameter(Query query,Map<String,Object> alias) {
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
	}
	
	private void setParameter(Query query,Object[] args) {
		if(args != null && args.length > 0) {
			int index = 0;
			for(Object arg : args) {//设置占位符
				query.setParameter(index++, arg);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void setPagers(Query query,Pager pages) {
		Integer pageSize = PageContext.getPageSize();
		Integer pageOffset = PageContext.getPageOffset();
		if(pageOffset == null || pageOffset < 0) {
			pageOffset = 0;
		}
		if(pageSize == null || pageSize < 0) {
			pageSize = 15;
		}
		pages.setOffset(pageOffset);
		pages.setSize(pageSize);
		query.setFirstResult(pageOffset).setMaxResults(pageSize);
	}
	
	//isFetch == true 替换掉fetch为"" 
	private String getCountHql(String hql,boolean isHql) {
		String end = hql.substring(hql.indexOf("from"));
		String countSQL = "select count(*) "+end;
		if(isHql) {
			countSQL.replaceAll("fetch", "");//将抓取 fetch 替换为空
		}
		return countSQL;
	}
	
	@Override
	public T add(T t) {
		getSession().save(t);
		return t;
	}

	@Override
	public void update(T t) {
		getSession().update(t);
	}	

	@Override
	public void delete(int id) {
		getSession().delete(this.load(id));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T load(int id) {
		//load 需要获取泛型 Class 对象
		return (T)getSession().load(getClz(), id);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String)
	 */
	@Override
	public List<T> list(String hql) {
		return this.list(hql, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.lang.Object)
	 */
	@Override
	public List<T> list(String hql, Object arg) {
		return this.list(hql, new Object[]{arg}, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<T> list(String hql, Object[] args) {
		return this.list(hql, args, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.util.Map)
	 */
	@Override
	public List<T> listByAlias(String hql, Map<String, Object> alias) {
		return this.list(hql, null, alias);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#list(java.lang.String, java.lang.Object[], java.util.Map)
	 */
	@SuppressWarnings({"unchecked" })
	@Override
	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		hql = initSort(hql);
		Query query = getSession().createQuery(hql);
		setAliasParameter(query,alias);
		setParameter(query, args);
		return query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public <X extends Object> List<X> listObj(String hql,Object ... args) {
		hql = initSort(hql);
		Query query = getSession().createQuery(hql);
		setParameter(query, args);
		return query.list();
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String)
	 */
	@Override
	public Pager<T> find(String hql) {
		return this.find(hql, null, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.lang.Object)
	 */
	@Override
	public Pager<T> find(String hql, Object arg) {
		return this.find(hql, new Object[]{arg}, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Pager<T> find(String hql, Object[] args) {
		return this.find(hql,args,null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public Pager<T> findByAlias(String hql, Map<String, Object> alias) {
		return this.find(hql, null, alias);
	}
	
	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#find(java.lang.String, java.lang.Object[], java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pager<T> find(String hql, Object[] args, Map<String, Object> alias) {
		String countSQL = getCountHql(hql,true);
		hql = initSort(hql);
		Query query = getSession().createQuery(hql);
		Query countQuery = getSession().createQuery(countSQL);//查出 totol总记录数
		setAliasParameter(query, alias);//设置别名
		setAliasParameter(countQuery, alias);
		setParameter(query, args);//设置量化参数
		setParameter(countQuery, args);
		Pager<T> pages = new Pager<T>();
		setPagers(query,pages);
		List<T> datas = query.list();
		long total = (long)countQuery.uniqueResult();
		pages.setDatas(datas);
		pages.setTotal(total);
		return pages;
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#queryObject(java.lang.String)
	 */
	@Override
	public Object queryObject(String hql) {
		return this.queryObject(hql, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#queryObject(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object queryObject(String hql, Object arg) {
		return this.queryObject(hql, new Object[]{arg});
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#queryObject(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object queryObject(String hql, Object[] args) {
		return this.queryObjectByAlias(hql, args,null);
	}
	
	public Object queryObjectByAlias(String hql,Map<String,Object> alias) {
		return this.queryObjectByAlias(hql, null, alias);
	}
	
	public Object queryObjectByAlias(String hql,Object[]args,Map<String,Object> alias){
		Query query = getSession().createQuery(hql);
		setParameter(query, args);
		setAliasParameter(query, alias);
		return query.uniqueResult();
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#updateByHql(java.lang.String)
	 */
	@Override
	public void updateByHql(String hql) {
		this.updateByHql(hql, null);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#updateByHql(java.lang.String, java.lang.Object)
	 */
	@Override
	public void updateByHql(String hql, Object arg) {
		this.updateByHql(hql, new Object[]{arg});
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#updateByHql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void updateByHql(String hql, Object[] args) {
		Query query = getSession().createQuery(hql);
		setParameter(query, args);
		query.executeUpdate();
	}

	/*********************************************************************************************
	 * SQL begin
	*********************************************************************************************/
	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object> List<N> listBySql(String sql, Class<?> clz, boolean hasEntity) {
		return this.listBySql(sql, clz, null, null, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.lang.Object, boolean)
	 */
	@Override
	public <N extends Object> List<N> listBySql(String sql, Class<?> clz, Object arg,
			boolean hasEntity) {
		return this.listBySql(sql, clz, new Object[]{arg}, null, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 */
	@Override
	public <N extends Object> List<N> listBySql(String sql, Class<?> clz, Object[] args,
			boolean hasEntity) {
		return this.listBySql(sql, clz, args, null, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.util.Map, boolean)
	 */
	@Override
	public <N extends Object> List<N> listByAliasSql(String sql, Class<?> clz,
			Map<String, Object> alias, boolean hasEntity) {
		return this.listBySql(sql, clz, null, alias, hasEntity);
	}

	/* (non-Javadoc)  
	 * @see org.jbp.basic.basic.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, java.lang.Object[], java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public<N extends Object> List<N> listBySql(String sql, Class<?> clz, Object[] args,
			Map<String, Object> alias, boolean hasEntity) {
		sql = initSort(sql);
		SQLQuery sq = getSession().createSQLQuery(sql);
		setAliasParameter(sq, alias);
		setParameter(sq, args);
		if(hasEntity) {//实体被hibernate管理
			sq.addEntity(clz);
		}else{
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		return sq.list();
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, boolean)
	 */
	@Override
	public <N extends Object> Pager<N> findBySql(String sql, Class<?> clz, boolean hasEntity) {
		return this.findBySql(sql, clz, null, null, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.lang.Object, boolean)
	 */
	@Override
	public <N extends Object> Pager<N> findBySql(String sql, Class<?> clz, Object arg,
			boolean hasEntity) {
		return this.findBySql(sql, clz, new Object[]{arg}, null, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 */
	@Override
	public <N extends Object> Pager<N> findBySql(String sql, Class<?> clz, Object[] args,
			boolean hasEntity) {
		return this.findBySql(sql, clz, args, null, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.util.Map, boolean)
	 */
	@Override
	public <N extends Object> Pager<N> findByAliasSql(String sql, Class<?> clz,
			Map<String, Object> alias, boolean hasEntity) {
		return this.findBySql(sql, clz, null, alias, hasEntity);
	}

	/* (non-Javadoc)
	 * @see org.jbp.basic.basic.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, java.lang.Object[], java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <N extends Object> Pager<N> findBySql(String sql, Class<?> clz, Object[] args,
			Map<String, Object> alias, boolean hasEntity) {
		String countSQL = getCountHql(sql,false);
		sql = initSort(sql);
		SQLQuery countQuery = getSession().createSQLQuery(countSQL);
		SQLQuery query = getSession().createSQLQuery(sql);
		setAliasParameter(query, alias);
		setAliasParameter(countQuery, alias);
		setParameter(query, args);
		setParameter(countQuery, args);
		Pager<N> pages = new Pager<N>();
		setPagers(query, pages);
		if(hasEntity) {//实体被hibernate管理
			query.addEntity(clz);
		}else{
			query.setResultTransformer(Transformers.aliasToBean(clz));
		}
		BigInteger total = (BigInteger) countQuery.uniqueResult();
		List<N> datas = query.list();
		pages.setTotal(total.longValue());
		pages.setDatas(datas);
		return pages;
	}
	/*********************************************************************************************
	 * SQL begin
	*********************************************************************************************/

}
