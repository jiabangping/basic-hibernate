package org.jbp.basic.test.util;

import java.util.List;

import junit.framework.Assert;

import org.jbp.basic.model.User;

public class EntitiesHelper {
	private static User expectedUser = new User(1,"admin1");//预期 希望的结果是 1,admin1
	
	public static void assertUser(User expected,User actual) {
		Assert.assertNotNull(expected);
		Assert.assertEquals(expected.getId(), actual.getId());
		Assert.assertEquals(expected.getUsername(), actual.getUsername());
	}
	
	public static void assertUsers(List<User> expected,List<User> actuals) {
		for(int i=0;i<expected.size();i++) {
			User eu = expected.get(i);
			User au = actuals.get(i);
			assertUser(eu, au);
		}
	}
	
	/**
	 * @param actual 程序查询后得出的结果
	 */
	public static void assertUser(User actual) {
		assertUser(expectedUser, actual);//预期的结果是 1,admin1 与真实的actual对象做比较
	}
	
}
