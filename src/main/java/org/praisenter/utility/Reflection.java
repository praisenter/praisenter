package org.praisenter.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;

public final class Reflection {
	private Reflection() {}
	
	public static final List<Method> getWritablePropertyMethodsForAnnotation(Class<? extends Annotation> annotationClass, Class<?> objectClass) {
		List<Method> propertyMethods = new ArrayList<>();
		
		for (Class<?> c = objectClass; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
            	// has to have the annotation
            	if (method.isAnnotationPresent(annotationClass) &&
            		Observable.class.isAssignableFrom(method.getReturnType())) {
            		propertyMethods.add(method);
            	}
            }
        }
		
		return propertyMethods;
	}
}
