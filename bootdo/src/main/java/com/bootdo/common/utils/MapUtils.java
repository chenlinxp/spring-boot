package com.bootdo.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.bootdo.common.exception.BaseException;


public class MapUtils extends org.apache.commons.collections.MapUtils{
	/**
	 * 支持key {emplooy:{name:"cc"}}  emplooy.name
	 * @param m
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Object get(Map m,String key,String defaultValue){
		String[] prem = key.split("\\.");
		Object o = null;
		for(String keyElement : prem){
			 o =  m.get(keyElement);
			 if(o instanceof Map)
				 m = (Map) o;
			 else break;
		}
		return o == null ? defaultValue : o;
	}
	
	/**
	 * Map映射为class的list、忽略大小写、下划线
	 * @param params
	 * @param clz
	 * @return
	 */
//	public static <T> List<T> map2List(List<Map<String, Object>> params, Class<T> clz) {
//		List<T> list = new ArrayList<T>();
//		for(Map<String, Object> map : params){
//			list.add(map2Bean(map, clz));
//		}
//		return list;
//	}
	/**
	 * class的list映射为Map
	 * @param params
	 * @param clz
	 * @return
	 */
	public static List<Map<String, Object>> list2Map(List params) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(Object map : params){
			list.add(beanToMap(map));
		}
		return list;
	}
	
	/**
	 * 功能描述: <br>
	 * 将对象转换为HashMap
	 * 
	 * @param obj
	 *            需要转换的对象
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Map<String, Object> obj2StrMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fds = BeanUtils.getDeclaredFields(obj.getClass());
		for (Field fd : fds) {
			try {
				Method md = obj.getClass().getMethod(BeanUtils.getMethodName(fd.getName()));
				Object val = md.invoke(md.getDeclaringClass().cast(obj));
				map.put(fd.getName(), val == null ? null : val.toString());
			} catch (Exception e) {
				throw new BaseException(e, "对象转换为Map失败");
			}
		}
		return map;
	}
	
	
	/**
	 * 对象转为map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!StringUtils.equals(name, "class")) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}
	/**
	 * Map映射为class、忽略大小写、下划线
	 * @param params
	 * @param clz
	 * @return
	 */
//	public static <T> T map2Bean(Map<String, Object> params, Class<T> clz) {
//
//		//TODO 添加hibernate 映射支持
//		T t = null;
//		try {
//			t = clz.newInstance();
//			Set<String> keys = params.keySet();
//			Set<Field> fset = new HashSet<Field>();
//			BeanUtils.loadSuperFields(clz, fset);
//			for (String key : keys) {
//				for (Field fd : fset) {
//					if (fd.getName().equalsIgnoreCase(key)||fd.getName().equalsIgnoreCase(key.replace("_", ""))) {
//						Object val = params.get(key);
//						if (val == null) {
//							continue;
//						}
//						if (fd.getType() != val.getClass()) {
//							val = DataFormater.convertType(fd.getType(), DataFormater.toString(val));
//						}
//						BeanUtils.setMethod(fd, clz).invoke(t, val);
//					}
//				}
//			}
//		} catch (Exception e) {
//			throw new BaseException(e);
//		}
//		return t;
//	}
	
	/**
	 * 对Map排序成treemap
	 * @param map 
	 * @param byKey  是否按照map的key排序?
	 * @param isDESC 是否降序 
	 * @param field  不按map的key排序时,按map的value的哪个字段排序?如果是null,表示按照value本身排序
	 * @return 
	 */

	
	/**
	 * 对Map排序成treemap,按照key升序排列
	 * @param map    
	 * @return 
	 */
//	public static <T> TreeMap<String, T> sort(Map<String, T> map) {
//		FieldComparator fieldComparator = new FieldComparator(null, false,true,map);
//		return new TreeMap<String, T>(fieldComparator);
//	}
	/**
	 * 对Map排序成treemap,按照key升序或者降序排列
	 * @param map
	 * @param isDESC  是否降序
	 * @return 
	 */
//	public static <T> TreeMap<String, T> sort(Map<String, T> map,boolean isDESC) {
//		FieldComparator fieldComparator = new FieldComparator(null, isDESC,true,map);
//		return new TreeMap<String, T>(fieldComparator);
//	}

	/**
	 * 对Map排序成treemap,按照Map的元素升序或者降序排列
	 * @param map 
	 * @param byKey  是否按照map的key排序?
	 * @param isDESC 是否降序 
	 * @return 
	 */
//	public static <T> TreeMap<String, T> sort(Map<String, T> map,boolean byKey,boolean isDESC) {
//		FieldComparator fieldComparator = new FieldComparator(null, isDESC,byKey,map);
//		return new TreeMap<String, T>(fieldComparator);
//	}
	
	
	/**
	 * 对Map排序成treemap,按照Map的元素中字段field升序
	 * @param map 
	 * @param field  不按map的key排序时,按map的value的哪个字段排序?如果是null,表示按照value本身排序
	 * @return 
	 */
//	public static <T> TreeMap<String, T> sort(Map<String, T> map,String field) {
//		FieldComparator fieldComparator = new FieldComparator(field, false,false,map);
//		TreeMap treeMap =  new TreeMap<String, T>(fieldComparator);
//		for(String k : map.keySet()){
//			treeMap.put(k, map.get(k));
//		}
//		return treeMap;
//	}
	
	/**
	 * 对Map排序成treemap,按照Map的元素中字段field升序或者降序
	 * @param map 
	 * @param isDESC 是否降序 
	 * @param field  不按map的key排序时,按map的value的哪个字段排序?如果是null,表示按照value本身排序
	 * @return 
	 */
//	public static <T> TreeMap<String, T> sort(Map<String, T> map,String field,boolean isDESC) {
//		FieldComparator fieldComparator = new FieldComparator(field, isDESC,false,map);
//		return new TreeMap<String, T>(fieldComparator);
//	}
	
	
	
	
	
	/**
	 * 字段排序
	 * @author Jing.Zhuo
	 */
//	private static class FieldComparator implements Comparator<Object> {
//		private final String field;
//		private final boolean isDESC;
//		private final boolean byKey;
//		private Method getMethodA;
//		private Method getMethodB;
//		private final Map src;
//
//		public FieldComparator(String field, boolean isDESC,boolean byKey,Map src) {
//			this.field = field;
//			this.isDESC = isDESC;
//			this.byKey = byKey;
//			this.src = src;
//		}

//		@Override
//		public int compare(Object a, Object b) {
//			try {
//				int rst = compareAsc(a, b);
//				return isDESC ? -rst : rst;
//			} catch (Exception e) {
//				throw new BaseException(e);
//			}
//		}

		/**
		 * 返回顺序对比结果
		 * 
		 * @param a
		 *            排序对象
		 * @param b
		 *            排序对象
		 * @return
		 * @throws Exception
		 */
//		public int compareAsc(Object a, Object b) throws Exception {
//			if (a == null && b == null) {
//				return 0;
//			}
//			if (a == null) {
//				return -1;
//			}
//			if (b == null) {
//				return 1;
//			}
//			Object fieldA = null, fieldB = null;
//			if(byKey || field == null){
//				fieldA = a;
//				fieldB = b;
//			}else{
//				if(src == null){
//					return 0;
//				}
//				a = src.get(a);
//				b = src.get(b);
//				if (a == null && b == null) {
//					return 0;
//				}
//				if (a == null) {
//					return -1;
//				}
//				if (b == null) {
//					return 1;
//				}
//				if (field != null) {
//					if (a instanceof Map) {
//						fieldA = ((Map) a).get(field);
//					} else {
//						if (getMethodA == null) {
//							getMethodA = BeanUtils.getMethod(field, a.getClass());
//						}
//						if (getMethodA != null)
//							fieldA = getMethodA.invoke(a);
//					}
//
//					if (b instanceof Map) {
//						fieldB = ((Map) b).get(field);
//					} else {
//						if (getMethodB == null) {
//							getMethodB = BeanUtils.getMethod(field, b.getClass());
//						}
//						if (getMethodB != null)
//							fieldB = getMethodB.invoke(b);
//					}
//					if (fieldA == null && fieldB == null) {
//						return 0;
//					}
//					if (fieldA == null) {
//						return -1;
//					}
//					if (fieldB == null) {
//						return 1;
//					}
//				} else {
//					fieldA = a;
//					fieldB = b;
//				}
//			}
//
//			Class clz = fieldA.getClass();
//			if (clz == Short.class || clz == short.class)
//				return compareShort(fieldA, fieldB);
//			if (clz == Integer.class || clz == int.class)
//				return compareInteger(fieldA, fieldB);
//			if (clz == Long.class || clz == long.class)
//				return compareLong(fieldA, fieldB);
//			if (clz == Float.class || clz == float.class)
//				return compareFloat(fieldA, fieldB);
//			if (clz == Double.class || clz == double.class)
//				return compareDouble(fieldA, fieldB);
//			if (clz == BigDecimal.class)
//				return compareBigDecimal(fieldA, fieldB);
//			if (clz == Date.class) {
//				return compareDate(fieldA, fieldB);
//			}
//			return compareString(fieldA, fieldB);
//
//		}

//		private int compareString(Object fieldA, Object fieldB) {
//			String strA = fieldA.toString();
//			String strB = fieldB.toString();
//
//			int length = strA.length() < strB.length() ? strA.length() : strB.length();
//			for (int i = 0; i < length; i++) {
//
//				String s1 = strA.substring(i, i + 1);
//				String s2 = strB.substring(i, i + 1);
//
//				if (s1.length() != s1.getBytes().length && s2.length() != s2.getBytes().length) {
//					if (s1.equals(s2)) {
//						continue;
//					}
//					return concatPinyinStringArray(PinyinHelper.toHanyuPinyinStringArray(s1.charAt(0))).compareTo(concatPinyinStringArray(PinyinHelper.toHanyuPinyinStringArray(s2.charAt(0))));
//				} else {
//					if (s1.equals(s2)) {
//						continue;
//					}
//					return s1.compareTo(s2);
//				}
//			}
//			return strA.length() - strB.length();
//		}
//
//		private int compareShort(Object a, Object b) {
//			return (Short) a - (Short) b;
//		}
//
//		private int compareInteger(Object a, Object b) {
//			return (Integer) a - (Integer) b;
//		}
//
//		private int compareLong(Object a, Object b) {
//			return (int) ((Long) a - (Long) b);
//		}
//
//		private int compareFloat(Object a, Object b) {
//			return (int) ((Float) a - (Float) b);
//		}
//
//		private int compareDouble(Object a, Object b) {
//			return (int) ((Double) a - (Double) b);
//		}
//
//		private int compareBigDecimal(Object a, Object b) {
//			return ((BigDecimal) a).compareTo(((BigDecimal) b));
//		}
//
//		private int compareDate(Object a, Object b) {
//			return ((Date) a).compareTo(((Date) b));
//		}
//
//		private String concatPinyinStringArray(String[] pinyinArray) {
//			StringBuffer sb = new StringBuffer();
//			if (pinyinArray != null && pinyinArray.length > 0) {
//				for (int i = 0; i < pinyinArray.length; i++) {
//					sb.append(pinyinArray[i]);
//				}
//			}
//			return sb.toString();
//		}
//	}
}
