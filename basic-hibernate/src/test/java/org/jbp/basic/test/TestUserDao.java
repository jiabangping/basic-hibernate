package org.jbp.basic.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import junit.framework.Assert;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbp.basic.dao.IUserDao;
import org.jbp.basic.model.PageContext;
import org.jbp.basic.model.Pager;
import org.jbp.basic.model.User;
import org.jbp.basic.test.util.AbstractDbUnitTestCase;
import org.jbp.basic.test.util.EntitiesHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
public class TestUserDao extends AbstractDbUnitTestCase {
	//为数据库的数据初始化一些东西，测试查询，还要每次测试要用干净的数据来测试(测试不影响数据库)使用DBUnit
	@Inject
	private IUserDao<User> userDao;
	@Inject
	private SessionFactory sessionFactory;
	@Before
	public void setUp() throws DataSetException, SQLException, IOException {
		this.backupAllTable();//备份数据
		Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory,new org.springframework.orm.hibernate4.SessionHolder(session));
	}
	
	@Test
	public void testLoad() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		User u =  userDao.load(1);
		EntitiesHelper.assertUser(u);
	}
	
	@Test(expected=org.hibernate.ObjectNotFoundException.class)//如果抛出ObjectNotFoundException异常证明删除成功了 //(expected=LazyInitializationException.class)
	public void testDelete() throws DatabaseUnitException, SQLException {
		log.error("testDelete");
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		userDao.delete(1);
		User u = userDao.load(1);
		//如果这里需要直接使用方法，需要静态导入 import static org.junit.Assert.*;
		assertNotNull(u);
		log.error(u.getUsername());
	}

	@Test
	public void testListByArgs() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		PageContext.setOrder("desc");
		PageContext.setSort("id");
		List<User> actuals = userDao.list("from User where id>? and id<?",new Object[]{1,4});
		List<User> expecteds = Arrays.asList(new User(3,"admin3"),new User(2,"admin2"));
		EntitiesHelper.assertUsers(expecteds,actuals);
	}
	
	@Test
	public void testListByArgsAndAlias() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		PageContext.setOrder("asc");
		PageContext.setSort("id");
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,5,6,7,8,9));
		List<User> actuals = userDao.list("from User where id>? and id<? and id in(:ids)",new Object[]{1,4},alias);
		List<User> expecteds = Arrays.asList(new User(2,"admin2"),new User(3,"admin3"));
		EntitiesHelper.assertUsers(expecteds,actuals);
	}
	
	@Test
	public void testfindByArgs() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		PageContext.setPageOffset(0);
		PageContext.setPageSize(3);
		PageContext.setOrder("desc");
		PageContext.setSort("id");
		Pager<User> actuals = userDao.find("from User where id>=? and id<?",new Object[]{1,10});
		List<User> expecteds = Arrays.asList(new User(9,"admin9"),new User(8,"admin8"),new User(7,"admin7"));
		Assert.assertEquals(3, actuals.getSize());
		Assert.assertEquals(9, actuals.getTotal());
		EntitiesHelper.assertUsers(expecteds,actuals.getDatas());
	}
	
	
	@Test
	public void testfindByArgsAndAlias() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		PageContext.setPageOffset(0);
		PageContext.setPageSize(3);
		PageContext.setOrder("asc");
		PageContext.setSort("id");
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,5,6,7,8,9));
		Pager<User> actuals = userDao.find("from User where id>=? and id<? and id in(:ids)",new Object[]{1,10},alias);
		List<User> expecteds = Arrays.asList(new User(1,"admin1"),new User(2,"admin2"),new User(3,"admin3"));
		Assert.assertEquals("分页列表对应size应该为3,actual,size="+actuals.getSize(),3, actuals.getSize());
		Assert.assertEquals("分页列表total应该为8,actual,total="+actuals.getTotal(),8, actuals.getTotal());
		EntitiesHelper.assertUsers(expecteds,actuals.getDatas());
	}
	
	
	@Test
	public void testListBySqlAlias() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		PageContext.removeOrder();
		PageContext.removeSort();
		String sql = "select * from t_user where id>=? and id<? and id in(:ids)";
		Object[] args = new Object[]{1,10};
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2));
		List<User> actuals = userDao.listBySql(sql,User.class,args,alias,true);
		List<User> expected = Arrays.asList(new User(1,"admin1"),new User(2,"admin2"));
		Assert.assertNotNull(actuals);
		EntitiesHelper.assertUsers(expected, actuals);
	}
	
	
	@Test
	public void testFindBySql() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		PageContext.setOrder("desc");
		PageContext.setSort("id");
		PageContext.setPageOffset(0);
		PageContext.setPageSize(2);
		String sql = "select * from t_user where id>=? and id<? and id in(:ids)";
		Object[] args = new Object[]{1,10};
		Map<String,Object> alias = new HashMap<String,Object>();
		alias.put("ids", Arrays.asList(1,2,3,4,5,6,7));
		Pager<User> actuals = userDao.findBySql(sql,User.class,args,alias,true);
		List<User> expected = Arrays.asList(new User(7,"admin7"),new User(6,"admin6"));
		Assert.assertNotNull(actuals);
		EntitiesHelper.assertUsers(expected, actuals.getDatas());
	}
	
	
	@After
	public void tearDown() throws FileNotFoundException, DatabaseUnitException, SQLException {//卸载
		this.resumeTable();//删除测试数据，恢复备份数据
		org.springframework.orm.hibernate4.SessionHolder holder = (org.springframework.orm.hibernate4.SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		Session session = holder.getSession();
		session.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.closeSession(session);
	}
}
