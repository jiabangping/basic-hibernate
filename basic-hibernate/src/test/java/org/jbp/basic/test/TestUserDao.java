package org.jbp.basic.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.LazyInitializationException;
import org.jbp.basic.dao.IUserDao;
import org.jbp.basic.model.User;
import org.jbp.basic.test.util.AbstractDbUnitTestCase;
import org.jbp.basic.test.util.EntitiesHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
public class TestUserDao extends AbstractDbUnitTestCase {
	//为数据库的数据初始化一些东西，测试查询，还要每次测试要用干净的数据来测试(测试不影响数据库)使用DBUnit
	@Inject
	private IUserDao<User> userDao;
	
	@Before
	public void setUp() throws DataSetException, SQLException, IOException {
		this.backupAllTable();//备份数据
	}
	@Test
	public void testLoad() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		User u =  userDao.load(1);
		EntitiesHelper.assertUser(u);
	}
	
	@Test(expected=LazyInitializationException.class)
	public void testDelete() throws DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("t_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		userDao.delete(1);
		User u = userDao.load(1);
		//如果这里需要直接使用方法，需要静态导入 import static org.junit.Assert.*;
		assertNotNull(u);
		System.out.println(u.getUsername());
	}
	
	
	
	@After
	public void tearDown() throws FileNotFoundException, DatabaseUnitException, SQLException {//卸载
		this.resumeTable();//删除测试数据，恢复备份数据
	}
}
