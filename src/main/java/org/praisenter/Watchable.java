package org.praisenter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for property values that can be "watched."  The primary use of this annoation
 * is to flag what properties will take part in undo/redo.
 * @author William Bittle
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Watchable {
	public String name() default "";
}
