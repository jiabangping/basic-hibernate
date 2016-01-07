package org.jbp.basic.dao.impl;

import org.jbp.basic.dao.IUserDao;
import org.jbp.basic.model.User;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User> implements IUserDao<User> {

}
