需要复习 hibernate的 别名查询  占位符查询的方式

注意在测试类的时候，最好不要加properties文件， 此文件在 clean的时候删不掉

junit  Assert如何用
dbunit easymock 使用

//为数据库的数据初始化一些东西，测试查询，

每次测试要用干净的数据来测试(测试不影响数据库)使用 org.dbunit
在测试 delete时，org.hibernate.HibernateException:illegally attempted to associate a proxy with two open Sessions
大概意思是 试图打开两个Session
解决： sessionFactory().getCurrentSession();  不应该是每次都openSession();
Hibernate4 No Session found for current thread原因：http://www.yihaomen.com/article/java/466.htm(这种对于测试 有问题)

参考：http://www.iteye.com/topic/1126047 此文章挺好
把工作中经常用的东西 融合进去，作为脚手架，方便以后查询 (zhangkaitao): http://www.iteye.com/topic/1120924

could not initialize proxy - no Session: 解决方式： http://my.oschina.net/alexgaoyh/blog/313541(web中，可以在web.xml配置openSessionInView的filter,但是使用junit单元测试时需要别的方法)
也即spring junit 延迟加载： http://www.360doc.com/content/10/1124/15/4744550_72049294.shtml

泛型 



