package org.praisenter;

public interface DisplayText {
	/**
	 * Returns a string useful for displaying on a given display type.
	 * <p>
	 * The returned string can contain formatting elements and may not be ready for
	 * display immediately.  The caller should be aware of this and should do some
	 * preprocessing on it before display.
	 * @param type the display type
	 * @return String
	 */
	public abstract String getDisplayText(DisplayType type);
}
