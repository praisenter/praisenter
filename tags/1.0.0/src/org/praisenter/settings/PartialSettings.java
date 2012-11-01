package org.praisenter.settings;

/**
 * Represents a group of settings that is contained in {@link RootSettings}.
 * <p>
 * These settings objects use the given {@link RootSettings} properties and
 * do not have properties of their own.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class PartialSettings extends Settings {
	/** The prefix delimeter */
	private static final String PREFIX_DELIMETER = ".";
	
	/** The component prefix */
	protected String prefix;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Please use the {@link #PartialSettings(String, RootSettings)} to specify
	 * the prefix for the partial settings.  This is useful when there are multiple
	 * {@link PartialSettings} in one settings group to avoid key collisions.
	 * @param root the settings this grouping belongs to
	 */
	protected PartialSettings(RootSettings<?> root) {
		this(null, root);
	}
	
	/**
	 * Optional constructor.
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	protected PartialSettings(String prefix, RootSettings<?> root) {
		super(root.properties);
		if (prefix == null || prefix.length() == 0) {
			this.prefix = "";
		} else {
			this.prefix = prefix + PartialSettings.PREFIX_DELIMETER;
		}
	}
}