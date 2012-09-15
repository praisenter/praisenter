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
	 * @param prefix the settings property prefix
	 * @param root the settings this grouping belongs to
	 */
	protected PartialSettings(String prefix, RootSettings<?> root) {
		super(root.properties);
		this.prefix = prefix + PartialSettings.PREFIX_DELIMETER;
	}
}