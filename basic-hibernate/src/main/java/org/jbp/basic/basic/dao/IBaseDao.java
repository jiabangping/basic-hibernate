package org.jbp.basic.basic.dao;

import java.util.List;
import java.util.Map;

import org.jbp.basic.basic.model.Pager;

/**
 * 公共的Dao处理对象，这个对象中包含了Hibernate的所有基本操作和对SQL的操作
 * @author root
 * @param <T>
 */
public interface IBaseDao<T> {
	
	/**
	 * 添加对象
	 * @param t
	 * @return
	 */
	public T add(T t);
	
	/**
	 * 更新对象
	 * @param t
	 */
	public void update(T t);
	
	/**
	 * 根据id删除对象
	 * @param id
	 */
	public void delete(int id);
	
	/**
	 * 根据id加载对象
	 * @param id
	 * @return
	 */
	public T load(int id);
	
	//列表分2种情况：分页的(find) 和 不分页(list)的
	/**************************************************************************
	 * 不分页 列表查询begin 使用 list
	**************************************************************************/
	/** 
	 * 查询 不分页列表
	 * @return
	 */
	public List<T> list(String hql);
	
	/**
	 * 占位符的方式查询 不分页列表
	 * @param arg
	 * @return
	 */
	public List<T> list(String hql,Object arg);
	
	/**
	 * 使用占位符的方式查询 不分页列表
	 * @param args
	 * @return
	 */
	public List<T> list(String hql,Object[] args);
	
	
	/**
	 * 通过别名方式查询 不分页列表
	 * @param alias  hql中 select role from Role where role.user.id in(:ids) and username= :username  用别名的查询方式
	 * @return
	 */
	public List<T> listByAlias(String hql,Map<String,Object> alias);
	
	/**
	 * 既有别名，也有使用 ？号的方式 查询  不分页列表
	 * @param args hql中 select user from User where user.username=? 使用 ?号 的方式
	 * @param alias hql中 select role from Role where role.user.id in(:ids) and username= :username  用别名的查询方式
	 * @return
	 */
	public List<T> list(String hql,Object[] args,Map<String,Object> alias);
	
	/**************************************************************************
	 * 不分页 列表查询end
	**************************************************************************/
	
	
	
	
	/**************************************************************************
	 * 分页 列表查询begin  使用find
	**************************************************************************/
	/** 
	 * 查询 分页列表
	 * @return
	 */
	public List<T> find(String hql);
	
	/**
	 * 占位符的方式查询  分页列表
	 * @param arg
	 * @return
	 */
	public List<T> find(String hql,Object arg);
	
	/**
	 * 使用占位符的方式查询 分页列表
	 * @param args
	 * @return
	 */
	public List<T> find(String hql,Object[] args);
	
	
	/**
	 * 通过别名方式查询 分页列表
	 * @param alias  hql中 select role from Role where role.user.id in(:ids) and username= :username  用别名的查询方式
	 * @return
	 */
	public List<T> findByAlias(String hql,Map<String,Object> alias);
	
	/**
	 * 既有别名，也有使用 ？号的方式 查询  分页列表
	 * @param hql
	 * @param args hql中 select user from User where user.username=? 使用 ?号 的方式
	 * @param alias hql中 select role from Role where role.user.id in(:ids) and username= :username  用别名的查询方式
	 * @return
	 */
	public List<T> find(String hql,Object[] args,Map<String,Object> alias);
	
	/**************************************************************************
	 * 分页 列表查询end
	**************************************************************************/
	
	
	
	/**通过hql查询对象
	 */
	public Object queryObject(String hql);
	
	/**通过hql查询对象
	 */
	public Object queryObject(String hql,Object arg);
	
	/**通过hql查询对象
	 */
	public Object queryObject(String hql,Object[] args);
	
	
	/**
	 * 根据hql更新对象
	 */
	public void updateByHql(String hql);
	
	/**
	 * 根据hql更新对象
	 */
	public void updateByHql(String hql,Object arg);
	
	/**
	 * 根据hql更新对象
	 */
	public void updateByHql(String hql,Object[] args);
	
	
	
	/**************************************************************************
	 * 根据SQL查询对象，不包含关联对象  begin  list：不分页   find：分页
	**************************************************************************/
	/**
	 * 如果对项目要求比较高，使用原生SQL代替hql
	 * 	(1)如果查询的是实体对象(使用@Entity注解的)
	 *   session.createSQLQuery("select * from CATS").addEntity(Cat.class);
	 *    需要将要查询的实体 class 传过去
	 *   如果查询的实体是DTO(并没有用@Entity表示 ，那需要
	 *   session.createSQLQuery(“select Name,BirthDate from CATS”)
	 *   .setResultTransformer(Transformers.aliasToBean(CatDTO.class))
	 *   
	 * @param sql
	 * @param args 
	 * @param clz 查询的实体对象 ，如果包含关联对象，那么这里会需要一组相应的 Class
	 * @hasEntity 该对象 是否为 hibernate管理的实体(Entity)对象，如果不是，需要使用setResultTransformer查询
	 * @return
	 */
	
	public List<T> listBySql(String sql,Class<T> clz,boolean hasEntity);
	
	public List<T> listBySql(String sql,Class<T> clz ,Object arg, boolean hasEntity);
	
	public List<T> listBySql(String sql,Class<T> clz, Object[] args, boolean hasEntity);
	
	public List<T> listBySql(String sql,Class<T> clz, Map<String,Object> alias, boolean hasEntity);
	
	public List<T> listBySql(String sql,Class<T> clz, Object[] args,Map<String,Object> alias, boolean hasEntity);
	
	
	public Pager<T> findBySql(String sql,Class<T> clz,boolean hasEntity);
	
	public Pager<T> findBySql(String sql,Class<T> clz ,Object arg, boolean hasEntity);
	
	public Pager<T> findBySql(String sql,Class<T> clz, Object[] args, boolean hasEntity);
	
	public Pager<T> findBySql(String sql,Class<T> clz, Map<String,Object> alias, boolean hasEntity);
	
	public Pager<T> findBySql(String sql,Class<T> clz, Object[] args,Map<String,Object> alias, boolean hasEntity);
	
	
	/**************************************************************************
	 * 根据SQL查询对象，不包含关联对象  end
	**************************************************************************/
}
