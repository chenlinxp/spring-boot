package com.bootdo.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

public class BeanUtilsBean extends org.apache.commons.beanutils.BeanUtilsBean {

	private static final ContextClassLoaderLocal beansByClassLoader = new ContextClassLoaderLocal() {
		@Override
		protected Object initialValue() {
			return new BeanUtilsBean();
		}
	};

	public static synchronized BeanUtilsBean getInstance() {
		return ((BeanUtilsBean) beansByClassLoader.get());
	}

	@Override
	public void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		int i;
		String name;
		Object value;
		if (dest == null) {
			throw new IllegalArgumentException("No destination bean specified");
		}

		if (orig == null) {
			throw new IllegalArgumentException("No origin bean specified");
		}

		if (orig instanceof DynaBean) {
			DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass().getDynaProperties();

			for (i = 0; i < origDescriptors.length; ++i) {
				name = origDescriptors[i].getName();
				if (getPropertyUtils().isWriteable(dest, name)) {
					value = ((DynaBean) orig).get(name);
					copyProperty(dest, name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator names = ((Map) orig).keySet().iterator();
			while (names.hasNext()) {
				name = (String) names.next();
				if (getPropertyUtils().isWriteable(dest, name)) {
					value = ((Map) orig).get(name);
					copyProperty(dest, name, value);
				}
			}
		} else {
			PropertyDescriptor[] origDescriptors = getPropertyUtils().getPropertyDescriptors(orig);

			for (i = 0; i < origDescriptors.length; ++i) {
				name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue;
				}
				if ((!(getPropertyUtils().isReadable(orig, name))) || (!(getPropertyUtils().isWriteable(dest, name))))
					continue;
				try {
					value = getPropertyUtils().getSimpleProperty(orig, name);

					copyProperty(dest, name, value);
				} catch (NoSuchMethodException e) {
				}
			}
		}
	}

	@Override
	public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
		if (value == null)
			return;
		super.copyProperty(bean, name, value);
	}
}
