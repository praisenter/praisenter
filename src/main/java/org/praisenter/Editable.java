package org.praisenter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to represent that a given object is "editable" in the sense that the user can
 * modify it through a UI.
 * @author William Bittle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Editable {
	public String value() default "";
}
