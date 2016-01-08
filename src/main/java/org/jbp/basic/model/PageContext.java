package org.jbp.basic.model;

/**
 * 传递列表的信息和排序的信息，使用 ThreadLocal
 * @author root
 */
public class PageContext {
	/**
	 * 列表大小
	 */
	private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();
	/**
	 * 列表起始页
	 */
	private static ThreadLocal<Integer> pageOffset = new ThreadLocal<Integer>();
	/**
	 * 列表使用的排序字段,通过哪个字段来排序
	 */
	private static ThreadLocal<String> sort = new ThreadLocal<String>();
	/**
	 * 列表的排序方式，asc/desc
	 */
	private static ThreadLocal<String> order = new ThreadLocal<String>();
	
	public static Integer getPageSize() {
		return pageSize.get();
	}
	
	public static void setPageSize(Integer _pageSize) {
		pageSize.set(_pageSize);
	}
	public static Integer getPageOffset() {
		return pageOffset.get();
	}
	
	public static void setPageOffset(Integer _pageOffset) {
		pageOffset.set(_pageOffset);
	}
	
	public static String getSort() {
		return sort.get();
	}
	
	public static void setSort(String _sort) {
		sort.set(_sort);;
	}
	
	public static String getOrder() {
		return order.get();
	}
	
	public static void setOrder(String _order) {
		order.set(_order);
	}
	
	public static void removePageSize() {
		pageSize.remove();
	}
	
	public static void removePageOffset(){
		pageOffset.remove();
	}
	
	public static void removeSort() {
		sort.remove();
	}
	
	public static void removeOrder() {
		order.remove();
	}
	
}
