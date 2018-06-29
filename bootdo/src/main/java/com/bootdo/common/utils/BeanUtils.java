package com.bootdo.common.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;


import com.bootdo.common.exception.BaseException;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
	static {
		ConvertUtils.register(new LongConverter(null), Long.class);
		ConvertUtils.register(new ShortConverter(null), Short.class);
		ConvertUtils.register(new IntegerConverter(null), Integer.class);
		ConvertUtils.register(new DoubleConverter(null), Double.class);
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		ConvertUtils.register(new BooleanConverter(null), Boolean.class);
		ConvertUtils.register(new FloatConverter(null), Float.class);
	}

	/**
	 * 复制属性
	 * @param dest
	 * @param orig
	 * @param skipIfnull
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void copyProperties(Object dest, Object orig,boolean skipIfnull) {
		if(skipIfnull){
			try {
				BeanUtilsBean.getInstance().copyProperties(dest, orig);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			try {
				org.apache.commons.beanutils.BeanUtils.copyProperties(dest, orig);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 批量复制
	 * @param list
	 * @param classType
	 * @param orig
	 * @param skipIfnull
	 * @return
	 */
	public static List copyBeans(List list, Class classType, boolean skipIfnull) {
		if (list == null)
			return null;
		Iterator iterator = list.iterator();
		List returnColl = new ArrayList();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			try {
				Object o = classType.newInstance();
				copyProperties(o,object,skipIfnull);
				returnColl.add(o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnColl;
	}

	public static final Logger log = LoggerFactory.getLogger(BeanUtils.class);
	private static Map<Class, Field[]> classMapping = new ConcurrentHashMap<Class, Field[]>();
	private static Map<String,Method> setMethodMapping = new ConcurrentHashMap<String, Method>();
	private static Map<String,Method> getMethodMapping = new ConcurrentHashMap<String, Method>();
	private static Map<String,Field> fieldMapping = new ConcurrentHashMap<String, Field>();


	/**
	 * 对象加码
	 * 
	 * @param object
	 * @param classType
	 * @return
	 * @throws Exception
	 */
	public static Object changeCode(Object object, Class classType) throws Exception {
		Field[] fields = classType.getDeclaredFields();
		Object objectCopy = classType.getConstructor(new Class[0]).newInstance(new Object[0]);
		for (int i = 0; i < fields.length; ++i) {
			Field field = fields[i];
			String fieldName = field.getName();
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getMethodName = (((!(field.getType().getName().equals("java.lang.Boolean"))) && (!(field.getType().getName().equals("boolean")))) ? "get" : "is") + firstLetter + fieldName.substring(1);
			String setMethodName = "set" + firstLetter + fieldName.substring(1);
			try {
				Method getMethod = classType.getMethod(getMethodName, new Class[0]);
				Method setMethod = classType.getMethod(setMethodName, new Class[] { field.getType() });
				Object value = null;
				if ((field.getType().getName().equals("java.lang.String"))) {
					value = getMethod.invoke(object, new Object[0]);
					setMethod.invoke(objectCopy, new Object[] { CharsetSwitch.decode(value.toString()) });
				} else {
					value = getMethod.invoke(object, new Object[0]);
					setMethod.invoke(objectCopy, new Object[] { value });
				}
			} catch (Exception localException) {
			}
		}
		return objectCopy;
	}

	/**
	 * 从cglib代理类中获取原值
	 * 
	 * @param object
	 * @param classType
	 * @return
	 * @throws Exception
	 */
	public static Object copyLazy(Object object, Class classType) throws Exception {
		Field[] fields = classType.getDeclaredFields();
		Object objectCopy = classType.getConstructor(new Class[0]).newInstance(new Object[0]);
		for (int i = 0; i < fields.length; ++i) {
			Field field = fields[i];

			String fieldName = field.getName();
			String f = field.getType().getName();
			if (f.indexOf("hibernate") > -1 || fieldName.indexOf("CGLIB") > -1)
				continue;
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getMethodName = (((!(field.getType().getName().equals("java.lang.Boolean"))) && (!(field.getType().getName().equals("boolean")))) ? "get" : "is") + firstLetter + fieldName.substring(1);
			String setMethodName = "set" + firstLetter + fieldName.substring(1);

			try {
				Method getMethod = classType.getMethod(getMethodName, new Class[0]);
				Method setMethod = classType.getMethod(setMethodName, new Class[] { field.getType() });
				Object value = null;

				if (((f.equals("java.lang.String")) || (f.equals("java.lang.Boolean")) || (f.equals("java.lang.Long")) || (f.equals("java.lang.Integer")) || (f.equals("java.lang.Double")) || (f.equals("java.lang.Float")) || (f.equals("java.math.BigInteger")) || (f.equals("java.util.Date")) || (f.equals("java.sql.Date")) || (f.equals("int")) || (f.equals("long")) || (f.equals("float")) || (f.equals("double")) || (f.equals("boolean")))) {
					value = getMethod.invoke(object, new Object[0]);
					setMethod.invoke(objectCopy, new Object[] { value });
				}
			} catch (Exception localException) {
			}
		}
		return objectCopy;
	}

	/**
	 * 执行某个对象的某个方法
	 * @param className
	 * @param beanName
	 * @param methodName
	 * @param args
	 * @return
	 */
//	public static Object exec(String className,String beanName,String methodName,Object[] args){
//		try {
//			Method method = null;
//			try {
//				Class type = Class.forName(className);
//				method = type.getMethod(methodName);
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//			return method.invoke(SpringContextUtil.getBean(beanName), args);
//		} catch (BeansException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//

	/**
	 * 判断bean的名为property的属性是否是可读的
	 * 
	 * @param bean
	 * @param property
	 * @return Administrator com.sxsihe.utils.common DataUtils.java 2012下午5:06:12 oxhide
	 */
	public static boolean propertyReadable(Object bean, String property) {
		boolean isReadable;
		if (bean instanceof Map) {
			return ((Map) bean).containsKey(property);
		}

		try {
			isReadable = PropertyUtils.isReadable(bean, property);
		} catch (IllegalArgumentException e) {
			isReadable = false;
		}

		return isReadable;
	}

	/**
	 * 获取bean的名为property的属性的值
	 * 
	 * @param bean
	 * @param property
	 * @return Administrator com.sxsihe.utils.common DataUtils.java 2012下午5:06:28 oxhide
	 */
	public static Object getPropertyValue(Object bean, String property) {
		Object result = null;
		try {
			if (propertyReadable(bean, property))
				result = PropertyUtils.getProperty(bean, property);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 设置bean的名为property的属性的值
	 * 
	 * @param bean
	 * @param property
	 * @return
	 */
	public static void setPropertyValue(Object bean, String property, Object value) {
		try {
			PropertyUtils.setProperty(bean, property, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数组contain方法
	 * 
	 * @param list
	 * @param src
	 * @return
	 */
	public static boolean contains(String[] list, String src) {
		if (list == null)
			return false;
		for (int i = 0; i < list.length; ++i)
			if (list[i].equals(src))
				return true;

		return false;
	}

	/**
	 * byte数组还原到对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object bytesToObject(byte[] bytes) {
		XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(bytes));
		return decoder.readObject();
	}

	/**
	 * 对象转换为byte数组
	 * 
	 * @param ob
	 * @return
	 */
	public static byte[] objectToBytes(Object ob) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(out);
		encoder.writeObject(ob);
		encoder.close();
		return out.toByteArray();
	}



	/**
	 * 获取项目路径
	 * 
	 * @param cls
	 * @return
	 */
	public static String getAppPath(Class cls) {
		// 检查用户传入的参数是否为空
		if (cls == null)
			throw new IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = null;
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + File.separator;
			else
				// 否则按照包名的组成部分，将包名转换为路径
				path = packName.replaceAll("\\.", "\\/");
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + "/" + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		if (realPath.startsWith("/"))
			realPath = realPath.substring(1, realPath.length());
		/*------------------------------------------------------------  
		 ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径  
		  中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要  
		  的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的  
		  中文及空格路径  
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}

	/**
	 * 获取cls所在的路径
	 * 
	 * @param cls
	 */
	public static String getClassPath(Class cls) {
		// 检查用户传入的参数是否为空
		if (cls == null)
			throw new IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = null;
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库,引导类
			if (loader == null)
				throw new IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + File.separator;
			else
				// 否则按照包名的组成部分，将包名转换为路径
				path = packName.replaceAll("\\.", "\\/");
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + "/" + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		if (realPath.startsWith("/"))
			realPath = realPath.substring(1, realPath.length());
		/*------------------------------------------------------------  
		 ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径  
		  中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要  
		  的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的  
		  中文及空格路径  
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}





	
	/**
	 * 判断是否是基本数据类型
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isPrimitive(Class clz) {
		if (clz == Byte.class || clz == byte.class)
			return true;
		if (clz == Integer.class || clz == int.class)
			return true;
		if (clz == Short.class || clz == short.class)
			return true;
		if (clz == Long.class || clz == long.class)
			return true;
		if (clz == Float.class || clz == float.class)
			return true;
		if (clz == Double.class || clz == double.class)
			return true;
		if (clz == Character.class || clz == char.class)
			return true;
		if (clz == Boolean.class || clz == boolean.class)
			return true;
		if (clz == String.class)
			return true;
		return false;
	}


	/**
	 * 从Http请求中获取对象的值
	 * 
	 * @param <T>
	 * @param request
	 * @param clz
	 * @return
	 */
	public static <T> T getBeanFromRequest(HttpServletRequest request, Class<T> clz) {
		Set<Field> fset = new HashSet<Field>();
		loadSuperFields(clz, fset);
		Object bean;
		try {
			bean = clz.newInstance();
			String fname = null;
			for (Field fd : fset) {
				fname = fd.getName();
				BeanUtils.setProperty(bean, fname, request.getParameter(fname));
			}
		} catch (Exception e) {
			throw new BaseException(e, "Request参数获取异常");
		}

		return (T) bean;
	}

	/**
	 * 加载父类属性
	 * 
	 * @param clz
	 * @param fset
	 */
	public static void loadSuperFields(Class clz, Set<Field> fset) {
		Field[] fds = getDeclaredFields(clz);
		for (Field fd : fds) {
			fset.add(fd);
			if (clz.getSuperclass() != Object.class) {
				loadSuperFields(clz.getSuperclass(), fset);
			}
		}
	}

	/**
	 * 获取过滤后的字段<br>
	 * 排除final、static、 native类型字段
	 * 
	 * @param clz
	 *            需要解析的类
	 * @return
	 */
	public static Field[] getDeclaredFields(Class clz) {
		Field[] fds = classMapping.get(clz);
		if (fds == null) {
			fds = clz.getDeclaredFields();
			List<Field> fls = new ArrayList<Field>();
			for (Field fd : fds) {
				switch (fd.getModifiers()) {
				case Modifier.FINAL:
				case Modifier.STATIC:
				case Modifier.NATIVE:
					continue;
				default:
					fls.add(fd);
				}
			}
			fds = fls.toArray(new Field[fls.size()]);
			classMapping.put(clz, fds);
		}
		return fds;
	}


	/**
	 * 获取get方法名
	 * 
	 * @param fieldName
	 * @return
	 */
	public static String getMethodName(String fieldName) {
		return "get" + firstToUpper(fieldName);
	}

	/**
	 * 获取set方法名
	 * 
	 * @param fieldName
	 * @return
	 */
	public static String setMethodName(String fieldName) {
		return "set" + firstToUpper(fieldName);
	}

	/**
	 * 首字母转换大写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstToUpper(String str) {
		if (str == null)
			return null;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 首字母转换小写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstToLower(String str) {
		if (str == null)
			return null;
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	/**
	 * 获取字段
	 * @param fieldName
	 * @param clz
	 * @return
	 */
//	public static Field getFields(String fieldName, Class clz){
//		if(fieldMapping.containsKey(fieldName + clz.getName())){
//			return fieldMapping.get(fieldName + clz.getName());
//		}
//		if (HibernateProxy.class.isAssignableFrom(clz)) {
//			clz = clz.getSuperclass();
//		}
//		Field field = null;
//		for(;clz != Object.class;clz = clz.getSuperclass()){
//			try {
//				field = clz.getDeclaredField(fieldName);
//				fieldMapping.put(fieldName + clz.getName(),field);
//				return field;
//			} catch (Exception e) {
//			}
//		}
//		log.debug("未匹配到"+clz.getName()+"的字段：[" + fieldName + "]");
//		return null;
//	}
//
	
	/**
	 * 获取get方法
	 * 
	 * @param fieldName
	 * @param clz
	 * @return
	 * @throws SecurityException
	 * @throws
	 */
//	public static Method getMethod(String fieldName, Class clz) {
//		if(getMethodMapping.containsKey(fieldName + clz.getName())){
//			return getMethodMapping.get(fieldName + clz.getName());
//		}
//		if (HibernateProxy.class.isAssignableFrom(clz)) {
//			clz = clz.getSuperclass();
//		}
//		Method method = null;
//		String name = getMethodName(fieldName);
//		for(;clz != Object.class;clz = clz.getSuperclass()){
//			try {
//				method = clz.getMethod(name);
//				getMethodMapping.put(fieldName + clz.getName(),method);
//				return method;
//			} catch (Exception e) {
//			}
//		}
//		log.debug("未匹配到"+clz.getName()+"字段为：[" + fieldName + "]的Get方法");
//		return null;
//	}

	/**
	 * 获取set方法
	 * 
	 * @param fieldName
	 * @param clz
	 * @return
	 */
//	public static Method setMethod(Field field, Class clz) {
//		if(setMethodMapping.containsKey(field.getName() + clz.getName())){
//			return setMethodMapping.get(field.getName() + clz.getName());
//		}
//		if (HibernateProxy.class.isAssignableFrom(clz)) {
//			clz = clz.getSuperclass();
//		}
//		Method method = null;
//		String name = setMethodName(field.getName());
//		for(;clz != Object.class;clz = clz.getSuperclass()){
//			try {
//				method = clz.getMethod(name,field.getType());
//				setMethodMapping.put(field.getName() + clz.getName(),method);
//				return method;
//			} catch (Exception e) {
//			}
//		}
//		log.debug("未匹配到"+clz.getName()+"字段为：[" + field.getName() + "]的Set方法");
//		return null;
//	}

	/**
	 * 获取set方法
	 * 
	 * @param fieldName
	 * @param clz
	 * @return
	 */
//	public static Method setMethod(String fieldName, Class clz) {
//		if(setMethodMapping.containsKey(fieldName + clz.getName())){
//			return setMethodMapping.get(fieldName + clz.getName());
//		}
//		Method method = null;
//		String name = setMethodName(fieldName);
//		for(;clz != Object.class;clz = clz.getSuperclass()){
//			try {
//				method = clz.getMethod(name, getField(fieldName, clz).getType());
//				setMethodMapping.put(fieldName + clz.getName(),method);
//				return method;
//			} catch (Exception e) {
//			}
//		}
//		log.debug("未匹配到"+clz.getName()+"字段为：[" + fieldName + "]的Set方法");
//		return null;
//	}

	/**
	 * 获取泛型属性的类型
	 * 
	 * @param fd
	 * @return
	 */
	public static Class<?> getCollectionType(Field fd) {
		Class<?>[] ts = getCollectionTypes(fd);
		return ts.length > 0 ? ts[0] : Object.class;
	}

	/**
	 * 获取泛型属性的类型
	 * 
	 * @param fd
	 * @return
	 */
	public static Class<?>[] getCollectionTypes(Field fd) {
		Type t = fd.getGenericType();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			Type[] ts = pt.getActualTypeArguments();
			if (ts != null && ts.length > 0) {
				Class<?>[] clss = new Class<?>[ts.length];
				for (int i = 0; i < ts.length; i++) {
					clss[i] = (Class<?>) ts[i];
				}
				return clss;
			}
		}
		return new Class[0];
	}

//	/**
//	 * 将Hibernate对象类型转换为普通对象类型
//	 *
//	 * @param clz
//	 * @return
//	 */
//	public static Class<?> hibernateClass2Common(Class<?> clz) {
//		if (HibernateProxy.class.isAssignableFrom(clz)) {
//			clz = clz.getSuperclass();
//		}
//		return clz;
//	}

	/**
	 * 克隆对象
	 * @param o
	 * @param t
	 * @return
	 */
	public static <T> T clone(Object o, Class<T> t) {
		ByteArrayOutputStream byteOut = null;
		ObjectOutputStream objOut = null;
		ByteArrayInputStream byteIn = null;
		ObjectInputStream objIn = null;

		try {
			byteOut = new ByteArrayOutputStream();
			objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(o);

			byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			objIn = new ObjectInputStream(byteIn);

			return (T) objIn.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Clone Object failed in IO.", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found.", e);
		} finally {
			try {
				byteIn = null;
				byteOut = null;
				if (objOut != null)
					objOut.close();
				if (objIn != null)
					objIn.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * 比较任意类型值,返回-1表示A < B;返回 1表示A > B;返回0 表示A == B
	 * @param A
	 * @param B
	 * @return
	 */
	public static int compare(Object A,Object B){
		return new CompareAny(A, B).exec();
	}
	
	private static class CompareAny {
		Object fieldA;
		Object fieldB;
		public CompareAny(Object A,Object B){
			this.fieldA = A;
			this.fieldB = B;
		}
		
		public int exec(){
			if (fieldB == null && fieldA == null) {
				return 0;
			}
			if (fieldA == null) {
				return -1;
			}
			if (fieldB == null) {
				return 1;
			}


			Class clz = fieldA.getClass();
			if (clz == Short.class || clz == short.class)
				return compareShort(fieldA, fieldB);
			if (clz == Integer.class || clz == int.class)
				return compareInteger(fieldA, fieldB);
			if (clz == Long.class || clz == long.class)
				return compareLong(fieldA, fieldB);
			if (clz == Float.class || clz == float.class)
				return compareFloat(fieldA, fieldB);
			if (clz == Double.class || clz == double.class)
				return compareDouble(fieldA, fieldB);
			if (clz == BigDecimal.class)
				return compareBigDecimal(fieldA, fieldB);
			if (clz == Date.class) {
				return compareDate(fieldA, fieldB);
			}
			return compareString(fieldA, fieldB);
		}
		
		private int compareString(Object fieldA, Object fieldB) {
			String strA = fieldA.toString();
			String strB = fieldB.toString();

			int length = strA.length() < strB.length() ? strA.length() : strB.length();
			for (int i = 0; i < length; i++) {

				String s1 = strA.substring(i, i + 1);
				String s2 = strB.substring(i, i + 1);

				if (s1.length() != s1.getBytes().length && s2.length() != s2.getBytes().length) {
					if (s1.equals(s2)) {
						continue;
					}
					return concatPinyinStringArray(PinyinHelper.toHanyuPinyinStringArray(s1.charAt(0))).compareTo(concatPinyinStringArray(PinyinHelper.toHanyuPinyinStringArray(s2.charAt(0))));
				} else {
					if (s1.equals(s2)) {
						continue;
					}
					return s1.compareTo(s2);
				}
			}
			return strA.length() - strB.length();
		}

		private int compareShort(Object a, Object b) {
			return (Short) a - (Short) b;
		}

		private int compareInteger(Object a, Object b) {
			return (Integer) a - (Integer) b;
		}

		private int compareLong(Object a, Object b) {
			return (int) ((Long) a - (Long) b);
		}

		private int compareFloat(Object a, Object b) {
			return (int) ((Float) a - (Float) b);
		}

		private int compareDouble(Object a, Object b) {
			return (int) ((Double) a - (Double) b);
		}

		private int compareBigDecimal(Object a, Object b) {
			return ((BigDecimal) a).compareTo(((BigDecimal) b));
		}

		private int compareDate(Object a, Object b) {
			return ((Date) a).compareTo(((Date) b));
		}

		private String concatPinyinStringArray(String[] pinyinArray) {
			StringBuffer sb = new StringBuffer();
			if (pinyinArray != null && pinyinArray.length > 0) {
				for (int i = 0; i < pinyinArray.length; i++) {
					sb.append(pinyinArray[i]);
				}
			}
			return sb.toString();
		}
	}
	
}
